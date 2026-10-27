package cn.iocoder.yudao.module.ai.service.decision;

import cn.iocoder.yudao.module.ai.controller.app.decision.vo.AppAiDecisionJevReqVO;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevDecisionResponse;

/**
 * AI 决策模型 (System One / JEV) Service 接口
 *
 * @author fansili
 */
public interface AiDecisionService {

    /**
     * 执行 JEV 决策推理
     *
     * @param userId 用户编号
     * @param reqVO  决策请求
     * @return 决策推理结果
     */
    JevDecisionResponse executeJevDecision(Long userId, AppAiDecisionJevReqVO reqVO);

}
