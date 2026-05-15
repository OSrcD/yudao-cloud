package cn.iocoder.yudao.module.ai.service.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoAihubmixSubmitReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoGeekAiVeoSubmitReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.video.BizAiVideoDO;
import cn.iocoder.yudao.module.ai.dal.mysql.video.BizAiVideoMapper;
import cn.iocoder.yudao.module.ai.enums.image.AiImageStatusEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.aihubmix.api.video.AihubmixVideoApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.geekai.api.video.GeekAiVideoVeoApi;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.IMAGE_NOT_EXISTS;

/**
 * AI 视频 Service 实现类
 *
 * @author CuiMa
 */
@Service
@Slf4j
public class BizAiVideoServiceImpl implements BizAiVideoService {

    @Resource
    private BizAiVideoMapper videoMapper;

    @Resource
    private AiModelService modelService;

    @Resource
    private FileApi fileApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitGeekAiVeoVideo(Long userId, BizAiVideoGeekAiVeoSubmitReqVO submitReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(submitReqVO.getModelId());

        // 2. 解析宽高
        int width = 0;
        int height = 0;
        if (StrUtil.isNotEmpty(submitReqVO.getSize())) {
            String[] sizeParts = submitReqVO.getSize().split("x");
            if (sizeParts.length == 2) {
                width = Integer.parseInt(sizeParts[0]);
                height = Integer.parseInt(sizeParts[1]);
            }
        }

        // 3. 保存数据库
        BizAiVideoDO video = BeanUtils.toBean(submitReqVO, BizAiVideoDO.class).setUserId(userId)
                .setUserType(cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserType())
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(model.getPlatform()).setModel(model.getName())
                .setWidth(width).setHeight(height);
        videoMapper.insert(video);

        // 4. 异步调用接口提交任务
        getSelf().executeSubmitVideo(video.getId(), submitReqVO);

        return video.getId();
    }

    @Async
    public void executeSubmitVideo(Long id, BizAiVideoGeekAiVeoSubmitReqVO submitReqVO) {
        BizAiVideoDO video = videoMapper.selectById(id);
        if (video == null) {
            return;
        }
        try {
            // 1. 校验模型
            AiModelDO model = modelService.validateModel(submitReqVO.getModelId());
            GeekAiVideoVeoApi videoApi = modelService.getGeekAiVideoVeoApi(model.getId());

            // 2.1 下载参考图
            List<byte[]> inputReferences = new ArrayList<>();
            if (CollUtil.isNotEmpty(submitReqVO.getInputReferences())) {
                for (String url : submitReqVO.getInputReferences()) {
                    inputReferences.add(HttpUtil.downloadBytes(url));
                }
            }

            // 2.2 提交任务
            GeekAiVideoVeoApi.VideoSubmitRequest request = GeekAiVideoVeoApi.VideoSubmitRequest.builder()
                    .model(model.getModel())
                    .prompt(submitReqVO.getPrompt())
                    .size(submitReqVO.getSize())
                    .seconds(submitReqVO.getSeconds())
                    .inputReferences(inputReferences)
                    .build();
            GeekAiVideoVeoApi.VideoResponse response = videoApi.submitTask(request);

            // 2.3 更新 taskId
            videoMapper.updateById(new BizAiVideoDO().setId(video.getId()).setTaskId(response.getId()));
        } catch (Exception e) {
            log.error("[executeSubmitVideo][video({}) 提交失败]", video.getId(), e);
            videoMapper.updateById(new BizAiVideoDO().setId(video.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(e.getMessage()).setFinishTime(LocalDateTime.now()));
        }
    }

    @Override
    public Long submitAihubmixVideo(Long userId, BizAiVideoAihubmixSubmitReqVO submitReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(submitReqVO.getModelId());

        // 2. 解析宽高
        int width = 0;
        int height = 0;
        if (StrUtil.isNotEmpty(submitReqVO.getSize())) {
            String[] sizeParts = submitReqVO.getSize().split("x");
            if (sizeParts.length == 2) {
                width = Integer.parseInt(sizeParts[0]);
                height = Integer.parseInt(sizeParts[1]);
            }
        }

        // 3. 插入数据库
        BizAiVideoDO video = BeanUtils.toBean(submitReqVO, BizAiVideoDO.class)
                .setUserId(userId)
                .setUserType(cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserType())
                .setPlatform(model.getPlatform())
                .setModel(model.getModel())
                .setWidth(width)
                .setHeight(height)
                .setDuration(StrUtil.isNotEmpty(submitReqVO.getSeconds()) ? Integer.parseInt(submitReqVO.getSeconds()) : null)
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        videoMapper.insert(video);

        // 3. 异步提交任务
        getSelf().executeSubmitAihubmixVideo(video, submitReqVO);
        return video.getId();
    }

    @Async
    public void executeSubmitAihubmixVideo(BizAiVideoDO video, BizAiVideoAihubmixSubmitReqVO submitReqVO) {
        try {
            // 1. 准备请求参数
            AihubmixVideoApi aihubmixVideoApi = modelService.getAihubmixVideoApi(submitReqVO.getModelId());
            AiModelDO modelDO = modelService.getModel(submitReqVO.getModelId());
            AihubmixVideoApi.VideoSubmitRequest request = new AihubmixVideoApi.VideoSubmitRequest()
                    .setModel(modelDO.getModel())
                    .setPrompt(submitReqVO.getPrompt())
                    .setSize(submitReqVO.getSize())
                    .setSeconds(submitReqVO.getSeconds());
            if (StrUtil.isNotEmpty(submitReqVO.getInputReference())) {
                request.setInputReference(submitReqVO.getInputReference());
            }

            // 2. 提交任务
            AihubmixVideoApi.VideoResponse response = aihubmixVideoApi.submitTask(request);
            if (response == null || StrUtil.isEmpty(response.getId())) {
                throw new RuntimeException("提交视频生成任务失败：响应为空");
            }

            // 3. 更新任务 ID
            videoMapper.updateById(new BizAiVideoDO().setId(video.getId()).setTaskId(response.getId()));

            // 4. 立即同步一次，保证状态更新
            syncAihubmixVideo(video.getId());
        } catch (Exception e) {
            log.error("[executeSubmitAihubmixVideo][video({}) 提交失败]", video.getId(), e);
            videoMapper.updateById(new BizAiVideoDO().setId(video.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus()).setErrorMessage(ExceptionUtil.getRootCauseMessage(e)));
        }
    }

    @Override
    public void syncAihubmixVideo(Long id) {
        BizAiVideoDO video = videoMapper.selectById(id);
        if (video == null || StrUtil.isEmpty(video.getTaskId())) {
            return;
        }
        if (AiImageStatusEnum.SUCCESS.getStatus().equals(video.getStatus())
                || AiImageStatusEnum.FAIL.getStatus().equals(video.getStatus())) {
            return;
        }

        // 异步同步
        getSelf().executeSyncAihubmixVideo(video);
    }

    @Override
    public Integer syncAihubmixVideo() {
        // 1. 查询进行中的任务
        List<BizAiVideoDO> videos = videoMapper.selectListByStatusAndPlatform(
                AiImageStatusEnum.IN_PROGRESS.getStatus(), AiPlatformEnum.Aihubmix.getPlatform());
        if (CollUtil.isEmpty(videos)) {
            return 0;
        }

        // 2. 逐个同步
        videos.forEach(video -> syncAihubmixVideo(video.getId()));
        return videos.size();
    }

    @Async
    public void executeSyncAihubmixVideo(BizAiVideoDO video) {
        // 1. 获取 API 客户端
        AihubmixVideoApi aihubmixVideoApi = modelService.getAihubmixVideoApi(video.getModelId());

        // 2. 轮询状态
        for (int i = 0; i < 360; i++) {
            try {
                // 2.1 查询数据库最新状态，避免重复同步
                BizAiVideoDO latestVideo = videoMapper.selectById(video.getId());
                if (latestVideo == null || !AiImageStatusEnum.IN_PROGRESS.getStatus().equals(latestVideo.getStatus())) {
                    return;
                }

                // 2.2 查询 API 状态
                AihubmixVideoApi.VideoResponse response = aihubmixVideoApi.getTask(video.getTaskId());
                if (response == null) {
                    return;
                }

                // 2.3 处理状态
                if ("completed".equals(response.getStatus())) {
                    // 下载视频
                    byte[] videoContent = aihubmixVideoApi.downloadVideo(video.getTaskId());
                    // 上传到文件服务
                    String videoUrl = fileApi.createFile(videoContent, video.getTaskId() + ".mp4");
                    // 更新数据库
                    videoMapper.updateById(new BizAiVideoDO().setId(video.getId())
                            .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                            .setVideoUrl(videoUrl).setFinishTime(LocalDateTime.now()));
                    return;
                } else if ("failed".equals(response.getStatus())) {
                    videoMapper.updateById(new BizAiVideoDO().setId(video.getId())
                            .setStatus(AiImageStatusEnum.FAIL.getStatus())
                            .setErrorMessage(response.getError() != null ? response.getError().getMessage() : "生成失败")
                            .setFinishTime(LocalDateTime.now()));
                    return;
                }

                // 2.4 等待下一次轮询
                Thread.sleep(10000L);
            } catch (Exception e) {
                log.error("[executeSyncAihubmixVideo][video({}) 第 ({}) 次同步失败]", video.getId(), i + 1, e);
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    @Override
    public void syncGeekAiVeoVideo(Long id) {
        getSelf().executeSyncVideo(id);
    }

    @Async
    public void executeSyncVideo(Long id) {
        BizAiVideoDO video = videoMapper.selectById(id);
        if (video == null || StrUtil.isEmpty(video.getTaskId())) {
            return;
        }
        try {
            GeekAiVideoVeoApi videoApi = modelService.getGeekAiVideoVeoApi(video.getModelId());
            GeekAiVideoVeoApi.VideoTaskResponse response = videoApi.getTask(video.getTaskId());
            if (response == null) {
                return;
            }

            // 更新状态
            BizAiVideoDO updateDO = new BizAiVideoDO().setId(video.getId());
            if ("completed".equals(response.getStatus())) {
                // 下载视频并转存
                byte[] videoContent = HttpUtil.downloadBytes(response.getUrl());
                String videoUrl = fileApi.createFile(videoContent);
                updateDO.setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                        .setVideoUrl(videoUrl)
                        .setFinishTime(LocalDateTime.now());
            } else if ("failed".equals(response.getStatus())) {
                updateDO.setStatus(AiImageStatusEnum.FAIL.getStatus())
                        .setErrorMessage(response.getFailReason())
                        .setFinishTime(LocalDateTime.now());
            } else {
                return;
            }
            videoMapper.updateById(updateDO);
        } catch (Exception e) {
            log.error("[executeSyncVideo][video({}) 同步失败]", id, e);
        }
    }

    @Override
    public Integer syncGeekAiVeoVideo() {
        // 1. 获取进行中的任务
        List<BizAiVideoDO> videos = videoMapper.selectListByStatusAndPlatform(
                AiImageStatusEnum.IN_PROGRESS.getStatus(), null);
        if (CollUtil.isEmpty(videos)) {
            return 0;
        }

        // 2. 逐个同步进展
        for (BizAiVideoDO video : videos) {
            syncGeekAiVeoVideo(video.getId());
        }
        return videos.size();
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private BizAiVideoServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

    @Override
    public void notifyVideo(String notifyData) {
        log.info("[notifyVideo][回调数据: {}]", notifyData);
        // TODO: 实现具体的解析逻辑
    }

    @Override
    public PageResult<BizAiVideoDO> getVideoPage(BizAiVideoPageReqVO pageReqVO) {
        return videoMapper.selectPage(pageReqVO);
    }

    @Override
    public void deleteVideo(Long id) {
        if (videoMapper.selectById(id) == null) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        videoMapper.deleteById(id);
    }
}
