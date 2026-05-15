package cn.iocoder.yudao.module.ai.controller.app.video;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;

import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceReqVO;
import cn.iocoder.yudao.module.ai.service.video.BizAiVideoReproduceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public CommonResult<Long> createTask(@Valid @RequestBody AppAiVideoReproduceReqVO reqVO) {
//        Long taskId = aiVideoReproduceService.createTask(getLoginUserId(), reqVO);
        Long taskId = aiVideoReproduceService.createTask(285L, reqVO);
        return success(taskId);
    }
    @PostMapping("/wash-frame")
    @Operation(summary = "触发单帧洗图")
    public CommonResult<Boolean> washFrame(@RequestParam("frameId") Long frameId,
                                         @RequestParam("modelId") Long modelId,
                                         @RequestParam("width") Integer width,
                                         @RequestParam("height") Integer height) {
        aiVideoReproduceService.washFrame(getLoginUserId(), frameId, modelId, width, height);
        return success(true);
    }

    @PostMapping("/generate-video")
    @Operation(summary = "触发单帧视频生成")
    public CommonResult<Boolean> generateVideo(@RequestParam("frameId") Long frameId,
                                             @RequestParam("modelId") Long modelId,
                                             @RequestParam("width") Integer width,
                                             @RequestParam("height") Integer height) {
        aiVideoReproduceService.generateVideo(getLoginUserId(), frameId, modelId, width, height);
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
}
