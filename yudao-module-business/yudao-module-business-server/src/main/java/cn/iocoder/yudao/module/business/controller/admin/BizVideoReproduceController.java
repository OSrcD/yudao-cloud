package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskCreateReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceFrameDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import cn.iocoder.yudao.module.business.service.BizVideoReproduceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 视频复刻任务")
@RestController
@RequestMapping("/business/video-reproduce")
@Validated
public class BizVideoReproduceController {

    @Resource
    private BizVideoReproduceService videoReproduceService;

    @GetMapping("/page")
    @Operation(summary = "获得视频复刻任务分页")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:query')")
    public CommonResult<PageResult<BizVideoReproduceTaskDO>> getTaskPage(@Validated BizVideoReproduceTaskPageReqVO pageVO) {
        return success(videoReproduceService.getTaskPage(pageVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得视频复刻任务详情")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:query')")
    public CommonResult<BizVideoReproduceTaskDO> getTask(@RequestParam("id") Long id) {
        return success(videoReproduceService.getTask(id));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除视频复刻任务")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:delete')")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        videoReproduceService.deleteTask(id);
        return success(true);
    }

    @PostMapping("/create")
    @Operation(summary = "创建视频复刻任务")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:create')")
    public CommonResult<Long> createTask(@ModelAttribute BizVideoReproduceTaskCreateReqVO createReqVO) {
        return success(videoReproduceService.createAndStartTask(createReqVO).getId());
    }

    @GetMapping("/frames/{taskId}")
    @Operation(summary = "获得任务截帧列表")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:query')")
    public CommonResult<List<BizVideoReproduceFrameDO>> getFrames(@PathVariable("taskId") Long taskId) {
        return success(videoReproduceService.getFrames(taskId));
    }

    @PostMapping("/wash-image/{frameId}")
    @Operation(summary = "洗图")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> washImage(@PathVariable("frameId") Long frameId,
                                         @RequestParam(value = "washMode", defaultValue = "original") String washMode,
                                         @RequestParam(value = "customPrompt", required = false) String customPrompt,
                                         @RequestBody(required = false) List<String> refImages,
                                         @RequestParam(value = "execMode", defaultValue = "api") String execMode) {
        videoReproduceService.washImage(frameId, washMode, customPrompt, refImages, execMode);
        return success(true);
    }

    @PostMapping("/generate-video/{frameId}")
    @Operation(summary = "生成视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> generateVideo(@PathVariable("frameId") Long frameId,
                                             @RequestParam(value = "execMode", defaultValue = "api") String execMode) {
        videoReproduceService.generateVideo(frameId, execMode);
        return success(true);
    }

    @PostMapping("/bind-audio/{frameId}")
    @Operation(summary = "绑定音频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> bindAudio(@PathVariable("frameId") Long frameId,
                                         @RequestPart("audio") MultipartFile audio) {
        videoReproduceService.bindAudio(frameId, audio);
        return success(true);
    }

    @PostMapping("/sync-audio/{frameId}")
    @Operation(summary = "音画同步")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> syncAudioToVideo(@PathVariable("frameId") Long frameId) {
        videoReproduceService.syncAudioToVideo(frameId);
        return success(true);
    }

    @PostMapping("/retry/{taskId}")
    @Operation(summary = "重试任务")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> retryTask(@PathVariable("taskId") Long taskId) {
        videoReproduceService.retryTask(taskId);
        return success(true);
    }

    @PostMapping("/generate-all/{taskId}")
    @Operation(summary = "一键生成全部视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> generateAllVideos(@PathVariable("taskId") Long taskId,
                                                 @RequestParam(value = "execMode", defaultValue = "api") String execMode) {
        videoReproduceService.generateAllVideos(taskId, execMode);
        return success(true);
    }

    @PostMapping("/wash-all-images/{taskId}")
    @Operation(summary = "一键全部洗图")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> washAllImages(@PathVariable("taskId") Long taskId,
                                             @RequestParam(value = "washMode", defaultValue = "original") String washMode,
                                             @RequestParam(value = "customPrompt", required = false) String customPrompt,
                                             @RequestBody(required = false) List<String> refImages,
                                             @RequestParam(value = "execMode", defaultValue = "api") String execMode) {
        videoReproduceService.washAllImages(taskId, washMode, customPrompt, refImages, execMode);
        return success(true);
    }

    @PostMapping("/undo-wash/{frameId}")
    @Operation(summary = "撤回洗图")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> undoWash(@PathVariable("frameId") Long frameId) {
        videoReproduceService.undoWash(frameId);
        return success(true);
    }

    @PostMapping("/undo-video/{frameId}")
    @Operation(summary = "撤回视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> undoVideo(@PathVariable("frameId") Long frameId) {
        videoReproduceService.undoVideo(frameId);
        return success(true);
    }

    @PostMapping("/clip-video/{frameId}")
    @Operation(summary = "剪辑视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> clipVideo(@PathVariable("frameId") Long frameId,
                                         @RequestBody List<Map<String, Double>> removeRanges) {
        videoReproduceService.clipVideo(frameId, removeRanges);
        return success(true);
    }

    @PostMapping("/merge-videos/{taskId}")
    @Operation(summary = "合成全片视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> mergeVideos(@PathVariable("taskId") Long taskId,
                                           @RequestBody Map<String, List<Long>> data) {
        videoReproduceService.mergeVideos(taskId, data.get("frameIds"));
        return success(true);
    }

    @GetMapping("/download-audio/{frameId}")
    @Operation(summary = "提取并下载视频音频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:query')")
    public void downloadAudio(@PathVariable("frameId") Long frameId, HttpServletResponse response) {
        videoReproduceService.downloadAudio(frameId, response);
    }

    @DeleteMapping("/frame/{frameId}")
    @Operation(summary = "删除截帧记录")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:delete')")
    public CommonResult<Boolean> deleteFrame(@PathVariable("frameId") Long frameId) {
        videoReproduceService.deleteFrame(frameId);
        return success(true);
    }

    @DeleteMapping("/frame/video/{frameId}")
    @Operation(summary = "删除生成的视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:delete')")
    public CommonResult<Boolean> deleteGeneratedVideo(@PathVariable("frameId") Long frameId) {
        videoReproduceService.deleteGeneratedVideo(frameId);
        return success(true);
    }

    @DeleteMapping("/frame/wash/{frameId}")
    @Operation(summary = "删除洗图图片")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:delete')")
    public CommonResult<Boolean> deletePolishedImage(@PathVariable("frameId") Long frameId) {
        videoReproduceService.deletePolishedImage(frameId);
        return success(true);
    }

    @DeleteMapping("/frame/image/{frameId}")
    @Operation(summary = "删除原始图片")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:delete')")
    public CommonResult<Boolean> deleteOriginalImage(@PathVariable("frameId") Long frameId) {
        videoReproduceService.deleteOriginalImage(frameId);
        return success(true);
    }

    @PostMapping("/auto-trim-audio/{frameId}")
    @Operation(summary = "自动裁剪音频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> autoTrimAudio(@PathVariable("frameId") Long frameId) {
        videoReproduceService.autoTrimAudio(frameId);
        return success(true);
    }

    @PostMapping("/manual-trim-audio/{frameId}")
    @Operation(summary = "手动裁剪音频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> manualTrimAudio(@PathVariable("frameId") Long frameId,
                                               @RequestParam("start") Double start,
                                               @RequestParam("end") Double end) {
        videoReproduceService.manualTrimAudio(frameId, start, end);
        return success(true);
    }

    @PostMapping("/update-prompts/{frameId}")
    @Operation(summary = "更新提示词")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> updatePrompts(@PathVariable("frameId") Long frameId,
                                             @RequestBody Map<String, String> prompts) {
        videoReproduceService.updatePrompts(frameId, prompts.get("promptEn"), prompts.get("promptZh"));
        return success(true);
    }

    @PostMapping("/recapture-frame/{frameId}")
    @Operation(summary = "手动重新截帧")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> recaptureFrame(@PathVariable("frameId") Long frameId,
                                              @RequestParam("timestamp") Double timestamp) {
        videoReproduceService.recaptureFrame(frameId, timestamp);
        return success(true);
    }

    @PostMapping("/upload-video/{frameId}")
    @Operation(summary = "手动上传生成的视频")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> uploadGeneratedVideo(@PathVariable("frameId") Long frameId,
                                                    @RequestPart("video") MultipartFile video) {
        videoReproduceService.uploadGeneratedVideo(frameId, video);
        return success(true);
    }

    @PostMapping("/upload-image/{frameId}")
    @Operation(summary = "手动上传原始图片")
    @PreAuthorize("@ss.hasPermission('business:video-reproduce:update')")
    public CommonResult<Boolean> uploadOriginalImage(@PathVariable("frameId") Long frameId,
                                                   @RequestPart("image") MultipartFile image) {
        videoReproduceService.uploadOriginalImage(frameId, image);
        return success(true);
    }

}

