package cn.iocoder.yudao.module.ai.controller.admin.decision;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.app.decision.vo.AppAiDecisionJevReqVO;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevDecisionResponse;
import cn.iocoder.yudao.module.ai.service.decision.AiDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 决策模型 (JEV)")
@RestController
@RequestMapping("/ai/decision")
public class AiDecisionController {

    @Resource
    private AiDecisionService decisionService;

    @PostMapping("/jev")
    @Operation(summary = "JEV 快速决策推理", description = "基于 Command Code / TypeSafe JEV 决策模型执行二分类(noul)、多分类(choice)、评分(score)推理")
    @PreAuthorize("@ss.hasPermission('ai:model:query')")
    public CommonResult<JevDecisionResponse> decide(@Valid @RequestBody AppAiDecisionJevReqVO reqVO) {
        return success(decisionService.executeJevDecision(getLoginUserId(), reqVO));
    }

}
