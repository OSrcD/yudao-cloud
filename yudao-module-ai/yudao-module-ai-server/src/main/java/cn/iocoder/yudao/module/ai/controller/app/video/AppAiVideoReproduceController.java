package cn.iocoder.yudao.module.ai.controller.app.video;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;

import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceCreateRespVO;
import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceReqVO;
import cn.iocoder.yudao.module.ai.service.video.BizAiVideoReproduceService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - AI 视频复刻")
@RestController
@RequestMapping("/ai/video/reproduce")
@Validated
public class AppAiVideoReproduceController {

    @Resource
    private BizAiVideoReproduceService aiVideoReproduceService;

    @PostMapping("/create-task")
    @Operation(summary = "提交视频复刻任务")
    public CommonResult<AppAiVideoReproduceCreateRespVO> createTask(@Valid @RequestBody AppAiVideoReproduceReqVO reqVO) {
        return success(aiVideoReproduceService.createTask(getLoginUserId(), reqVO));
    }

    @PostMapping("/update-paid")
    @Operation(summary = "更新视频复刻任务为已支付")
    @PermitAll
    public CommonResult<Boolean> updateTaskPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        aiVideoReproduceService.updateTaskPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
                notifyReqDTO.getPayOrderId());
        return success(true);
    }
    @PostMapping("/wash-frame")
    @Operation(summary = "触发单帧洗图")
    public CommonResult<Boolean> washFrame(@RequestParam("frameId") Long frameId,
                                         @RequestParam("modelId") Long modelId,
                                         @RequestParam("width") Integer width,
                                         @RequestParam("height") Integer height,
                                         @RequestParam(value = "useLocal", defaultValue = "false") Boolean useLocal,
                                         @RequestBody(required = false) java.util.Map<String, Object> localParams) {
        String customPrompt = localParams != null ? (String) localParams.get("customPrompt") : null;
        if (Boolean.TRUE.equals(useLocal)) {
            String washMode = localParams != null ? (String) localParams.get("washMode") : null;
            @SuppressWarnings("unchecked")
            java.util.List<String> refImages = localParams != null ? (java.util.List<String>) localParams.get("refImages") : null;
            aiVideoReproduceService.washFrameLocal(getLoginUserId(), frameId, washMode, customPrompt, refImages);
        } else {
            aiVideoReproduceService.washFrame(getLoginUserId(), frameId, modelId, width, height, customPrompt);
        }
        return success(true);
    }

    @PostMapping("/update-prompts")
    @Operation(summary = "更新分镜提示词")
    public CommonResult<Boolean> updatePrompts(@Valid @RequestBody cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceUpdatePromptsReqVO reqVO) {
        aiVideoReproduceService.updateFramePrompts(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/generate-video")
    @Operation(summary = "触发单帧视频生成")
    public CommonResult<Boolean> generateVideo(@RequestParam("frameId") Long frameId,
                                             @RequestParam("modelId") Long modelId,
                                             @RequestParam("width") Integer width,
                                             @RequestParam("height") Integer height,
                                             @RequestParam(value = "inputReference", required = false) String inputReference,
                                             @RequestParam(value = "useLocal", defaultValue = "false") Boolean useLocal) {
        if (Boolean.TRUE.equals(useLocal)) {
            aiVideoReproduceService.generateVideoLocal(getLoginUserId(), frameId, inputReference);
        } else {
            aiVideoReproduceService.generateVideo(getLoginUserId(), frameId, modelId, width, height, inputReference);
        }
        return success(true);
    }

    @GetMapping("/get-config")
    @Operation(summary = "获取视频复刻配置（如默认模型）")
    public CommonResult<cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceConfigRespVO> getReproduceConfig() {
        return success(aiVideoReproduceService.getReproduceConfig());
    }

    @GetMapping("/get-task-detail")
    @Operation(summary = "获取任务详情及分镜进度")

    public CommonResult<cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO> getTaskDetail(@RequestParam("taskId") Long taskId) {
        return success(aiVideoReproduceService.getTaskDetail(getLoginUserId(), taskId));
    }

    @GetMapping("/page")
    @Operation(summary = "获得视频复刻任务分页")
    public CommonResult<cn.iocoder.yudao.framework.common.pojo.PageResult<cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO>> getTaskPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return success(aiVideoReproduceService.getTaskPage(getLoginUserId(), pageNo, pageSize));
    }
}
