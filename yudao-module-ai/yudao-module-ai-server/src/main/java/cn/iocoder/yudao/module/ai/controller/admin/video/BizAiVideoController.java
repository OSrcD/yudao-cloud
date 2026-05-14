package cn.iocoder.yudao.module.ai.controller.admin.video;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoGeekAiVeoSubmitReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.video.BizAiVideoDO;
import cn.iocoder.yudao.module.ai.service.video.BizAiVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 视频")
@RestController
@RequestMapping("/ai/video")
@Validated
@Slf4j
public class BizAiVideoController {

    @Resource
    private BizAiVideoService videoService;

    @PostMapping("/submitGeekAiVeo")
    @Operation(summary = "提交GeekAiVeo视频生成任务")
    public CommonResult<Long> submitGeekAiVeo(@Valid @RequestBody BizAiVideoGeekAiVeoSubmitReqVO submitReqVO) {
        return success(videoService.submitGeekAiVeoVideo(getLoginUserId(), submitReqVO));
    }

    @GetMapping("/syncGeekAiVeoVideo")
    @Operation(summary = "同步GeekAi视频生成进展")
    @Parameter(name = "id", description = "编号", required = false)
    public CommonResult<Boolean> syncGeekAiVeoVideo(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            videoService.syncGeekAiVeoVideo(id);
            return success(true);
        }
        videoService.syncGeekAiVeoVideo();
        return success(true);
    }

    @PostMapping("/notify")
    @Operation(summary = "视频生成进展回调")
    public String notifyVideo(@RequestBody String notifyData) {
        log.info("[notifyVideo][回调数据: {}]", notifyData);
        videoService.notifyVideo(notifyData);
        return "success";
    }

    @GetMapping("/page")
    @Operation(summary = "获得视频分页")
    @PreAuthorize("@ss.hasPermission('ai:video:query')")
    public CommonResult<PageResult<BizAiVideoRespVO>> getVideoPage(@Valid BizAiVideoPageReqVO pageReqVO) {
        PageResult<BizAiVideoDO> pageResult = videoService.getVideoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, BizAiVideoRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除视频")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:video:delete')")
    public CommonResult<Boolean> deleteVideo(@RequestParam("id") Long id) {
        videoService.deleteVideo(id);
        return success(true);
    }

}
