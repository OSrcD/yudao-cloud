package cn.iocoder.yudao.module.business.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskCreateReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceFrameDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizVideoReproduceFrameMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizVideoReproduceTaskMapper;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 视频复刻任务 Service 实现类
 */
@Slf4j
@Service
public class BizVideoReproduceServiceImpl implements BizVideoReproduceService {

    @Resource
    private BizVideoReproduceTaskMapper taskMapper;
    @Resource
    private BizVideoReproduceFrameMapper frameMapper;
    @Resource
    private IBizGeminiVideoService geminiVideoService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private BizLocalTaskService localTaskService;
    @Autowired
    private FileApi fileApi;

    @Resource
    @Lazy
    private BizVideoReproduceService self;

    @Value("${business.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizVideoReproduceTaskDO createAndStartTask(BizVideoReproduceTaskCreateReqVO createReqVO) {
        // 1. 保存原始视频到文件服务器
        String videoUrl = uploadFile(createReqVO.getVideoFile());

        // 2. 创建任务记录
        BizVideoReproduceTaskDO taskDO = new BizVideoReproduceTaskDO();
        taskDO.setOriginalVideoUrl(videoUrl);
        if (StringUtils.hasText(createReqVO.getProductConfigJson())) {
            try {
                taskDO.setProductConfigJson(objectMapper.readValue(createReqVO.getProductConfigJson(), new TypeReference<Map<String, Object>>() {}));
            } catch (Exception e) {
                log.error("解析产品配置JSON失败", e);
            }
        }
        taskDO.setCharImages(uploadFiles(createReqVO.getCharImages()));
        taskDO.setProductImages(uploadFiles(createReqVO.getProductImages()));
        taskDO.setStatus("RUNNING");
        taskMapper.insert(taskDO);

        // 3. 异步启动工作流
        if ("local".equalsIgnoreCase(createReqVO.getExecMode())) {
            self.startLocalAnalyzeWorkflow(taskDO.getId());
        } else {
            self.startFullWorkflow(taskDO.getId()); // Wait, startFullWorkflow previously took the task object. I'll check its signature.
        }

        return taskDO;
    }

    private String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            return fileApi.createFile(file.getBytes(), file.getOriginalFilename());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    private List<String> uploadFiles(MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String url = uploadFile(file);
                if (url != null) {
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    @Override
    @Async
    public void startLocalAnalyzeWorkflow(Long taskId) {
        BizVideoReproduceTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        updateTaskStatus(taskId, "10", "任务已下发至本地队列，等待分析...");
        
        // 生成提示词
        List<String> prompts = geminiVideoService.getVeo3Prompts(task.getProductConfigJson() != null ? 
            cn.hutool.json.JSONUtil.toJsonStr(task.getProductConfigJson()) : "");

        try {
            com.fasterxml.jackson.databind.node.ObjectNode params = objectMapper.createObjectNode();
            params.put("videoUrl", task.getOriginalVideoUrl());

            ArrayNode charArray = params.putArray("charUrls");
            task.getCharImages().forEach(charArray::add);

            ArrayNode prodArray = params.putArray("productUrls");
            task.getProductImages().forEach(prodArray::add);

            ArrayNode promptsArray = params.putArray("prompts");
            prompts.forEach(promptsArray::add);

            localTaskService.enqueueTask("ANALYZE_VIDEO", taskId, null, objectMapper.writeValueAsString(params));
        } catch (Exception e) {
            log.error("下发本地分析任务失败", e);
            updateTaskStatus(taskId, "9", "本地发单失败: " + e.getMessage());
        }
    }

    private void updateTaskStatus(Long id, String status, String remark) {
        BizVideoReproduceTaskDO update = new BizVideoReproduceTaskDO();
        update.setId(id);
        update.setStatus(status);
        update.setRemark(remark);
        taskMapper.updateById(update);
    }

    private List<String> extractKeyFrames(String videoPath, String outputDir) throws Exception {
        // ffmpeg -i video.mp4 -vf "select='gt(scene,0.4)',setpts=N/FRAME_RATE/TB" -vsync vfr out%03d.jpg
        String pattern = Paths.get(outputDir, "frame_%03d.jpg").toString();
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-i", videoPath, "-vf", "select='gt(scene,0.4)',setpts=N/FRAME_RATE/TB",
                "-vsync", "vfr", pattern
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        consumeProcessOutput(process);
        process.waitFor();
        
        File dir = new File(outputDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg"));
        if (files == null) return new ArrayList<>();
        
        List<String> paths = new ArrayList<>();
        for (File f : files) {
            paths.add(f.getAbsolutePath());
        }
        Collections.sort(paths);
        return paths;
    }

    private void consumeProcessOutput(Process process) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("FFmpeg: {}", line);
            }
        } catch (IOException e) {
            log.error("读取进程输出失败", e);
        }
    }

    private File saveToTemp(String url) throws IOException {
        byte[] bytes = HttpUtil.downloadBytes(url);
        File tempFile = File.createTempFile("reproduce_", ".tmp");
        FileUtil.writeBytes(bytes, tempFile);
        return tempFile;
    }

    private File saveToTemp(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload_", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

    @Override
    public void washImage(Long frameId, String washMode, String customPrompt, List<String> refImages, String execMode) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null) throw new RuntimeException("截帧不存在");
        
        BizVideoReproduceTaskDO task = taskMapper.selectById(frame.getTaskId());

        try {
            if ("local".equalsIgnoreCase(execMode)) {
                com.fasterxml.jackson.databind.node.ObjectNode params = objectMapper.createObjectNode();
                params.put("frameId", frameId);
                params.put("mode", washMode);
                params.put("customPrompt", customPrompt);
                
                // 根据模式选择源图：如果是原图模式，强制使用 originalImageUrl；如果是复刻图模式，优先使用 polishedImageUrl
                String sourceUrl;
                if (washMode.startsWith("original")) {
                    sourceUrl = frame.getOriginalImageUrl();
                } else {
                    sourceUrl = StringUtils.hasText(frame.getPolishedImageUrl()) ? frame.getPolishedImageUrl() : frame.getOriginalImageUrl();
                }
                params.put("sourceUrl", sourceUrl);

                com.fasterxml.jackson.databind.node.ObjectNode templates = params.putObject("templates");
                templates.put("analyze", geminiVideoService.getImageWashAnalyzeTemplate());
                templates.put("restyle", geminiVideoService.getImageWashRestyleTemplate());
                
                ArrayNode charArray = params.putArray("charUrls");
                task.getCharImages().forEach(charArray::add);

                ArrayNode prodArray = params.putArray("productUrls");
                task.getProductImages().forEach(prodArray::add);

                if (refImages != null) {
                    ArrayNode extraArray = params.putArray("extraMaterials");
                    refImages.forEach(extraArray::add);
                }

                localTaskService.enqueueTask("WASH_IMAGE", task.getId(), frameId, objectMapper.writeValueAsString(params));
                return;
            }

            // API 模式
            String sourceUrl;
            if (washMode.startsWith("original")) {
                sourceUrl = frame.getOriginalImageUrl();
            } else {
                sourceUrl = StringUtils.hasText(frame.getPolishedImageUrl()) ? frame.getPolishedImageUrl() : frame.getOriginalImageUrl();
            }
            String resultJson = geminiVideoService.polishImage(sourceUrl, task.getCharImages(), task.getProductImages(), washMode, customPrompt);
            JsonNode resultNode = objectMapper.readTree(resultJson);
            String finalPrompt = resultNode.path("final_prompt").asText(resultJson);

            byte[] generatedImageBytes = geminiVideoService.generateImage(frame.getOriginalImageUrl(), finalPrompt);
            String newImageUrl = fileApi.createFile(generatedImageBytes, "polished_" + frameId + ".png");
            
            if (StringUtils.hasText(frame.getPolishedImageUrl())) {
                frame.setPrevPolishedUrl(frame.getPolishedImageUrl());
            }
            frame.setPolishedImageUrl(newImageUrl);
            frame.setStatus("2"); // 已洗图
            frameMapper.updateById(frame);
        } catch (Exception e) {
            log.error("洗图失败", e);
            throw new RuntimeException("洗图失败: " + e.getMessage());
        }
    }

    @Override
    public void generateVideo(Long frameId, String execMode) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null) throw new RuntimeException("截帧不存在");
        
        if ("local".equalsIgnoreCase(execMode)) {
            try {
                com.fasterxml.jackson.databind.node.ObjectNode params = objectMapper.createObjectNode();
                params.put("frameId", frameId);
                params.put("startImageUrl", StringUtils.hasText(frame.getPolishedImageUrl()) ? frame.getPolishedImageUrl() : frame.getOriginalImageUrl());
                params.put("prompt", frame.getI2vPromptEn());
                localTaskService.enqueueTask("GEN_VIDEO", frame.getTaskId(), frameId, objectMapper.writeValueAsString(params));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        BizVideoReproduceTaskDO task = taskMapper.selectById(frame.getTaskId());
        List<String> allRefs = new ArrayList<>();
        if (task.getCharImages() != null) allRefs.addAll(task.getCharImages());
        if (task.getProductImages() != null) allRefs.addAll(task.getProductImages());

        byte[] videoBytes = geminiVideoService.generateVideoFromImage(
            StringUtils.hasText(frame.getPolishedImageUrl()) ? frame.getPolishedImageUrl() : frame.getOriginalImageUrl(), 
            frame.getI2vPromptEn(), allRefs);
        String videoUrl = fileApi.createFile(videoBytes, "gen_" + frameId + ".mp4");
        
        if (StringUtils.hasText(frame.getGeneratedVideoUrl())) {
            frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
        }
        frame.setGeneratedVideoUrl(videoUrl);
        frame.setStatus("3"); // 已生成视频
        frameMapper.updateById(frame);
    }

    @Override
    public void syncAudioToVideo(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null || !StringUtils.hasText(frame.getGeneratedVideoUrl()) || !StringUtils.hasText(frame.getAudioUrl())) {
             throw new RuntimeException("视频或音频缺失");
        }
        
        try {
            File videoFile = saveToTemp(frame.getGeneratedVideoUrl());
            File audioFile = saveToTemp(frame.getAudioUrl());
            File outputFile = File.createTempFile("sync_", ".mp4");
            
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath, "-y", "-i", videoFile.getAbsolutePath(), "-i", audioFile.getAbsolutePath(),
                    "-c:v", "copy", "-c:a", "aac", "-map", "0:v:0", "-map", "1:a:0", "-shortest", outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            consumeProcessOutput(process);
            process.waitFor();
            
            String syncedUrl = fileApi.createFile(FileUtil.readBytes(outputFile), "synced_" + frameId + ".mp4");
            frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
            frame.setGeneratedVideoUrl(syncedUrl);
            frameMapper.updateById(frame);
            
            FileUtil.del(videoFile);
            FileUtil.del(audioFile);
            FileUtil.del(outputFile);
        } catch (Exception e) {
            log.error("音画同步失败", e);
            throw new RuntimeException("音画同步失败: " + e.getMessage());
        }
    }

    @Override
    public void autoTrimAudio(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null || !StringUtils.hasText(frame.getAudioUrl())) return;
        
        try {
            File audioFile = saveToTemp(frame.getAudioUrl());
            File outputFile = File.createTempFile("trimmed_", ".mp3");
            
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-i", audioFile.getAbsolutePath(),
                "-af", "silenceremove=start_periods=1:start_silence=0.1:start_threshold=-45dB,areverse,silenceremove=start_periods=1:start_silence=0.1:start_threshold=-45dB,areverse",
                outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            consumeProcessOutput(process);
            process.waitFor();
            
            String url = fileApi.createFile(FileUtil.readBytes(outputFile), "trimmed_" + frameId + ".mp3");
            frame.setAudioUrl(url);
            frameMapper.updateById(frame);
            
            FileUtil.del(audioFile);
            FileUtil.del(outputFile);
        } catch (Exception e) {
            log.error("音频裁剪失败", e);
        }
    }

    @Override
    public void manualTrimAudio(Long frameId, Double start, Double end) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null || !StringUtils.hasText(frame.getAudioUrl())) return;

        try {
            File audioFile = saveToTemp(frame.getAudioUrl());
            File outputFile = File.createTempFile("m_trimmed_", ".mp3");
            
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-ss", String.format("%.3f", start), "-t", String.format("%.3f", end - start),
                "-i", audioFile.getAbsolutePath(), "-c", "copy", outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            consumeProcessOutput(process);
            process.waitFor();
            
            String url = fileApi.createFile(FileUtil.readBytes(outputFile), "m_trimmed_" + frameId + ".mp3");
            frame.setAudioUrl(url);
            frameMapper.updateById(frame);
            
            FileUtil.del(audioFile);
            FileUtil.del(outputFile);
        } catch (Exception e) {
            log.error("手动裁剪失败", e);
        }
    }

    @Override
    public void retryTask(Long taskId) {
        self.startFullWorkflow(taskId);
    }

    @Override
    public void generateAllVideos(Long taskId, String execMode) {
        List<BizVideoReproduceFrameDO> frames = getFrames(taskId);
        for (BizVideoReproduceFrameDO frame : frames) {
            if (!StringUtils.hasText(frame.getGeneratedVideoUrl())) {
                generateVideo(frame.getId(), execMode);
            }
        }
    }

    @Override
    public void washAllImages(Long taskId, String washMode, String customPrompt, List<String> refImages, String execMode) {
        List<BizVideoReproduceFrameDO> frames = getFrames(taskId);
        for (BizVideoReproduceFrameDO frame : frames) {
            washImage(frame.getId(), washMode, customPrompt, refImages, execMode);
        }
    }

    @Override
    public void undoWash(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null && StringUtils.hasText(frame.getPrevPolishedUrl())) {
            String temp = frame.getPolishedImageUrl();
            frame.setPolishedImageUrl(frame.getPrevPolishedUrl());
            frame.setPrevPolishedUrl(temp);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void undoVideo(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null && StringUtils.hasText(frame.getPrevVideoUrl())) {
            String temp = frame.getGeneratedVideoUrl();
            frame.setGeneratedVideoUrl(frame.getPrevVideoUrl());
            frame.setPrevVideoUrl(temp);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void clipVideo(Long frameId, List<Map<String, Double>> removeRanges) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null || !StringUtils.hasText(frame.getGeneratedVideoUrl())) {
            throw new RuntimeException("当前无生视频结果，无法剪贴");
        }

        try {
            File origVideo = saveToTemp(frame.getGeneratedVideoUrl());
            double totalDuration = getVideoDuration(origVideo.getAbsolutePath());
            List<double[]> keepRanges = calculateKeepRanges(totalDuration, removeRanges);
            if (keepRanges.isEmpty()) {
                FileUtil.del(origVideo);
                throw new RuntimeException("剪除所有区间后视频为空！");
            }

            List<File> segments = new ArrayList<>();
            for (int i = 0; i < keepRanges.size(); i++) {
                double[] r = keepRanges.get(i);
                double start = r[0];
                double dur = r[1] - r[0];
                File seg = File.createTempFile("seg_" + i, ".mp4");
                ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath, "-y", "-ss", String.format("%.3f", start), "-t", String.format("%.3f", dur),
                    "-i", origVideo.getAbsolutePath(), "-c:v", "libx264", "-crf", "18", "-c:a", "aac", seg.getAbsolutePath()
                );
                pb.redirectErrorStream(true);
                Process process = pb.start();
                consumeProcessOutput(process);
                process.waitFor();
                segments.add(seg);
            }

            File listFile = File.createTempFile("list", ".txt");
            StringBuilder sb = new StringBuilder();
            for (File s : segments) {
                sb.append("file '").append(s.getAbsolutePath().replace("\\", "/")).append("'\n");
            }
            FileUtil.writeUtf8String(sb.toString(), listFile);

            File finalVideo = File.createTempFile("final_clip", ".mp4");
            ProcessBuilder pbConcat = new ProcessBuilder(
                ffmpegPath, "-y", "-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(),
                "-c", "copy", finalVideo.getAbsolutePath()
            );
            pbConcat.redirectErrorStream(true);
            Process processConcat = pbConcat.start();
            consumeProcessOutput(processConcat);
            processConcat.waitFor();

            String url = fileApi.createFile(FileUtil.readBytes(finalVideo), "clip_" + frameId + ".mp4");
            frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
            frame.setGeneratedVideoUrl(url);
            frameMapper.updateById(frame);

            FileUtil.del(origVideo);
            for (File s : segments) FileUtil.del(s);
            FileUtil.del(listFile);
            FileUtil.del(finalVideo);
        } catch (Exception e) {
            log.error("剪辑失败", e);
            throw new RuntimeException("剪辑失败: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void mergeVideos(Long taskId, List<Long> frameIds) {
        BizVideoReproduceTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;

        List<BizVideoReproduceFrameDO> frames;
        if (frameIds != null && !frameIds.isEmpty()) {
            frames = new ArrayList<>();
            for (Long fid : frameIds) {
                BizVideoReproduceFrameDO f = frameMapper.selectById(fid);
                if (f != null && f.getTaskId().equals(taskId)) {
                    frames.add(f);
                }
            }
        } else {
            frames = getFrames(taskId);
        }

        List<File> videoFiles = new ArrayList<>();
        try {
            for (BizVideoReproduceFrameDO frame : frames) {
                if (StringUtils.hasText(frame.getGeneratedVideoUrl())) {
                    videoFiles.add(saveToTemp(frame.getGeneratedVideoUrl()));
                }
            }

            if (videoFiles.isEmpty()) return;

            File listFile = File.createTempFile("merge_list_", ".txt");
            StringBuilder sb = new StringBuilder();
            for (File f : videoFiles) {
                sb.append("file '").append(f.getAbsolutePath().replace("\\", "/")).append("'\n");
            }
            FileUtil.writeUtf8String(sb.toString(), listFile);

            File outputFile = File.createTempFile("combined_", ".mp4");
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(),
                "-c", "copy", outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            consumeProcessOutput(process);
            process.waitFor();

            String url = fileApi.createFile(FileUtil.readBytes(outputFile), "merged_" + taskId + ".mp4");
            task.setCombinedVideoUrl(url);
            taskMapper.updateById(task);

            FileUtil.del(listFile);
            FileUtil.del(outputFile);
            for (File f : videoFiles) FileUtil.del(f);
        } catch (Exception e) {
            log.error("合成全片失败", e);
            for (File f : videoFiles) FileUtil.del(f);
        }
    }

    @Override
    public void downloadAudio(Long frameId, jakarta.servlet.http.HttpServletResponse response) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null || !StringUtils.hasText(frame.getGeneratedVideoUrl())) {
            throw new RuntimeException("生成视频不存在，无法下载音频");
        }

        File tempVideo = null;
        File tempAudio = null;
        try {
            tempVideo = saveToTemp(frame.getGeneratedVideoUrl());
            tempAudio = File.createTempFile("audio_" + frameId + "_", ".mp3");

            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-i", tempVideo.getAbsolutePath(),
                "-vn", "-ar", "44100", "-ac", "2", "-ab", "192k", tempAudio.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            consumeProcessOutput(process);
            process.waitFor();

            response.reset();
            response.setContentType("audio/mpeg");
            response.setHeader("Content-Disposition", "attachment; filename=\"audio_" + frameId + ".mp3\"");
            response.setContentLength((int) tempAudio.length());
            
            try (java.io.InputStream is = new java.io.FileInputStream(tempAudio);
                 java.io.OutputStream os = response.getOutputStream()) {
                cn.hutool.core.io.IoUtil.copy(is, os);
                os.flush();
            }
        } catch (Exception e) {
            log.error("提取音频失败", e);
        } finally {
            FileUtil.del(tempVideo);
            FileUtil.del(tempAudio);
        }
    }

    private double getVideoDuration(String path) throws Exception {
        String ffprobePath = ffmpegPath.replace("ffmpeg", "ffprobe");
        ProcessBuilder pb = new ProcessBuilder(
            ffprobePath, "-v", "error", "-show_entries", "format=duration",
            "-of", "default=noprint_wrappers=1:nokey=1", path
        );
        Process process = pb.start();
        String out = cn.hutool.core.io.IoUtil.readUtf8(process.getInputStream()).trim();
        process.waitFor();
        return Double.parseDouble(out);
    }

    private List<double[]> calculateKeepRanges(double totalDur, List<Map<String, Double>> removeRanges) {
        List<double[]> removes = new ArrayList<>();
        if (removeRanges != null) {
            for (Map<String, Double> map : removeRanges) {
                if (map.containsKey("start") && map.containsKey("end")) {
                    double s = Math.max(0, map.get("start"));
                    double e = Math.min(totalDur, map.get("end"));
                    if (e > s) removes.add(new double[]{s, e});
                }
            }
        }
        removes.sort(java.util.Comparator.comparingDouble(a -> a[0]));
        List<double[]> mergedRemoves = new ArrayList<>();
        for (double[] r : removes) {
            if (mergedRemoves.isEmpty()) {
                mergedRemoves.add(r);
            } else {
                double[] last = mergedRemoves.get(mergedRemoves.size() - 1);
                if (r[0] <= last[1]) {
                    last[1] = Math.max(last[1], r[1]);
                } else {
                    mergedRemoves.add(r);
                }
            }
        }
        List<double[]> keeps = new ArrayList<>();
        double curr = 0;
        for (double[] r : mergedRemoves) {
            if (r[0] > curr) keeps.add(new double[]{curr, r[0]});
            curr = Math.max(curr, r[1]);
        }
        if (curr < totalDur) keeps.add(new double[]{curr, totalDur});
        return keeps;
    }

    @Override
    public void deleteFrame(Long frameId) {
        frameMapper.deleteById(frameId);
    }

    @Override
    public void deleteGeneratedVideo(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setGeneratedVideoUrl(null);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void deletePolishedImage(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setPolishedImageUrl(null);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void deleteOriginalImage(Long frameId) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setOriginalImageUrl(null);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void updatePrompts(Long frameId, String promptEn, String promptZh) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setI2vPromptEn(promptEn);
            frame.setI2vPromptZh(promptZh);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void recaptureFrame(Long frameId, Double timestamp) {
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame == null) return;
        BizVideoReproduceTaskDO task = taskMapper.selectById(frame.getTaskId());
        try {
            File tempVideo = saveToTemp(task.getOriginalVideoUrl());
            String outPath = tempVideo.getAbsolutePath() + "_" + timestamp + ".png";
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-ss", String.format("%.3f", timestamp),
                "-i", tempVideo.getAbsolutePath(), "-vframes", "1", "-q:v", "2", outPath
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            consumeProcessOutput(process);
            process.waitFor();
            
            byte[] bytes = FileUtil.readBytes(outPath);
            String url = fileApi.createFile(bytes, "recapture_" + frameId + ".png");
            frame.setOriginalImageUrl(url);
            frame.setTimestampSec(String.valueOf(timestamp));
            frameMapper.updateById(frame);
            
            FileUtil.del(tempVideo);
            FileUtil.del(outPath);
        } catch (Exception e) {
            log.error("重捕获帧失败", e);
        }
    }

    @Override
    public void uploadGeneratedVideo(Long frameId, MultipartFile videoFile) {
        String url = uploadFile(videoFile);
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setGeneratedVideoUrl(url);
            frameMapper.updateById(frame);
        }
    }

    @Override
    public void uploadOriginalImage(Long frameId, MultipartFile imageFile) {
        String url = uploadFile(imageFile);
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        if (frame != null) {
            frame.setOriginalImageUrl(url);
            frameMapper.updateById(frame);
        }
    }
    
    @Override
    @Async
    public void startFullWorkflow(Long taskId) {
        BizVideoReproduceTaskDO task = taskMapper.selectById(taskId);
        if (task == null) return;
        try {
            updateTaskStatus(task.getId(), "1", "正在使用Gemini分析视频...");
            String resultJson = geminiVideoService.generateVeo3Json(task.getOriginalVideoUrl(), 
                task.getProductConfigJson() != null ? cn.hutool.json.JSONUtil.toJsonStr(task.getProductConfigJson()) : "",
                task.getCharImages(), task.getProductImages(), "fast");

            self.continueFullWorkflowAfterAnalysis(task.getId(), resultJson);
        } catch (Exception e) {
            log.error("分析失败", e);
            updateTaskStatus(task.getId(), "9", "分析失败: " + e.getMessage());
        }
    }


    @Override
    public void continueFullWorkflowAfterAnalysis(Long taskId, String resultJson) {
        try {
            JsonNode root = objectMapper.readTree(resultJson);
            JsonNode globalLockNode = root.path("global_lock_card");

            BizVideoReproduceTaskDO task = taskMapper.selectById(taskId);
            task.setResultJson(resultJson);
            if (!globalLockNode.isMissingNode() && !globalLockNode.isNull()) {
                task.setGlobalLocks(objectMapper.convertValue(globalLockNode, new TypeReference<Map<String, Object>>() {}));
            }
            task.setStatus("2");
            taskMapper.updateById(task);

            updateTaskStatus(taskId, "2", "正在截取关键帧...");

            JsonNode guPrompts = root.path("gu_prompts");
            if (guPrompts.isArray()) {
                File tempVideo = saveToTemp(task.getOriginalVideoUrl());
                try {
                    int index = 0;
                    for (JsonNode gu : guPrompts) {
                        String guId = gu.path("gu_id").asText();
                        double timestamp = gu.path("reference_frame_info").path("timestamp_sec").asDouble();

                        // 截帧
                        String outPath = tempVideo.getAbsolutePath() + "_" + timestamp + ".png";
                        ProcessBuilder pb = new ProcessBuilder(
                            ffmpegPath, "-y", "-ss", String.format("%.3f", timestamp),
                            "-i", tempVideo.getAbsolutePath(), "-vframes", "1", "-q:v", "2", outPath
                        );
                        pb.redirectErrorStream(true);
                        Process process = pb.start();
                        consumeProcessOutput(process);
                        process.waitFor();

                        String frameUrl = fileApi.createFile(FileUtil.readBytes(outPath), "frame_" + guId + ".png");

                        BizVideoReproduceFrameDO frameDO = new BizVideoReproduceFrameDO();
                        frameDO.setTaskId(taskId);
                        frameDO.setGuId(guId);
                        frameDO.setFrameIndex(index++);
                        frameDO.setTimestampSec(String.valueOf(timestamp));
                        frameDO.setOriginalImageUrl(frameUrl);
                        frameDO.setStatus("1");
                        
                        // 组合提示词
                        frameDO.setI2vPromptEn(composeFinalI2vPrompt(task, frameDO, root));
                        frameDO.setI2vPromptZh(composeFinalI2vPromptZh(frameDO, root));
                        frameMapper.insert(frameDO);

                        FileUtil.del(outPath);
                    }
                } finally {
                    FileUtil.del(tempVideo);
                }
            }
            updateTaskStatus(taskId, "3", "分析完成");
        } catch (Exception e) {
            log.error("后续处理失败", e);
            updateTaskStatus(taskId, "9", "处理失败: " + e.getMessage());
        }
    }

    private String composeFinalI2vPrompt(BizVideoReproduceTaskDO task, BizVideoReproduceFrameDO frame, JsonNode root) {
        StringBuilder sb = new StringBuilder();
        JsonNode locks = root.path("global_lock_card");
        sb.append("### Global Consistency Locks (DO NOT CHANGE) ###\n");
        sb.append("Character Identity: ").append(locks.path("character_lock").asText("N/A")).append("\n");
        sb.append("Product Attributes: ").append(locks.path("product_lock").asText("N/A")).append("\n");
        sb.append("Visual Consistency: ").append(locks.path("visual_consistency_lock").asText("N/A")).append("\n");
        sb.append("Voice Consistency: ").append(locks.path("voice_lock").asText("N/A")).append("\n");
        sb.append("Packaging Policy: ").append(locks.path("no_packaging_lock").asText("N/A")).append("\n");
        sb.append("Audio-Visual Alignment: ").append(locks.path("audio_visual_mode_lock").asText("N/A")).append("\n");
        sb.append("Subtitle Policy: 不能出现任何字幕。").append("\n");
        sb.append("Fixed Tail: ").append(locks.path("tail_lock").asText("N/A")).append("\n\n");

        sb.append("### GU Specific Visual Dialogue & SFX ###\n");
        JsonNode guPrompts = root.path("gu_prompts");
        for (JsonNode gu : guPrompts) {
            if (gu.path("gu_id").asText().equals(frame.getGuId())) {
                sb.append(gu.path("i2v_prompt_for_model_en").path("visual_dialogue_sfx").asText());
                break;
            }
        }
        return sb.toString();
    }

    private String composeFinalI2vPromptZh(BizVideoReproduceFrameDO frame, JsonNode root) {
        StringBuilder sb = new StringBuilder();
        JsonNode locks = root.path("global_lock_card");
        sb.append("【全片统一锁】\n");
        sb.append("人物一致性: ").append(locks.path("character_lock").asText("N/A")).append("\n");
        sb.append("商品一致性: ").append(locks.path("product_lock").asText("N/A")).append("\n");
        sb.append("画面风格锁: ").append(locks.path("visual_consistency_lock").asText("N/A")).append("\n");
        sb.append("禁止出现字幕: 不能出现任何字幕。").append("\n\n");

        sb.append("【GU分镜描述】\n");
        JsonNode guPrompts = root.path("gu_prompts");
        for (JsonNode gu : guPrompts) {
            if (gu.path("gu_id").asText().equals(frame.getGuId())) {
                sb.append(gu.path("i2v_prompt_zh_check").path("visual_dialogue_sfx").asText());
                break;
            }
        }
        return sb.toString();
    }

    @Override
    public PageResult<BizVideoReproduceTaskDO> getTaskPage(BizVideoReproduceTaskPageReqVO pageReqVO) {
        return taskMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<BizVideoReproduceTaskDO>()
                .eqIfPresent(BizVideoReproduceTaskDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(BizVideoReproduceTaskDO::getId));
    }

    @Override
    public BizVideoReproduceTaskDO getTask(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<BizVideoReproduceFrameDO> getFrames(Long taskId) {
        return frameMapper.selectList(new LambdaQueryWrapper<BizVideoReproduceFrameDO>()
                .eq(BizVideoReproduceFrameDO::getTaskId, taskId)
                .orderByAsc(BizVideoReproduceFrameDO::getFrameIndex));
    }

    @Override
    public void bindAudio(Long frameId, MultipartFile audioFile) {
        String audioUrl = uploadFile(audioFile);
        BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
        frame.setAudioUrl(audioUrl);
        frameMapper.updateById(frame);
    }



    private String getTempDir(String subDir) {
        String path = Paths.get(System.getProperty("java.io.tmpdir"), "reproduce", subDir).toString();
        FileUtil.mkdir(path);
        return path;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
        frameMapper.delete(new LambdaQueryWrapper<BizVideoReproduceFrameDO>().eq(BizVideoReproduceFrameDO::getTaskId, id));
    }

}
