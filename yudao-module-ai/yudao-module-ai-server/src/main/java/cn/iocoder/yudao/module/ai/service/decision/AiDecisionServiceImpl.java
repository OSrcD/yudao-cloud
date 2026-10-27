package cn.iocoder.yudao.module.ai.service.decision;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.ai.controller.app.decision.vo.AppAiDecisionJevReqVO;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevDecisionRequest;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevDecisionResponse;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevQuestion;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.JEV_EXECUTE_ERROR;

/**
 * AI 决策模型 (System One / JEV) Service 实现类
 *
 * @author fansili
 */
@Service
@Validated
@Slf4j
public class AiDecisionServiceImpl implements AiDecisionService {

    @Resource
    private AiModelService modelService;

    @Override
    public JevDecisionResponse executeJevDecision(Long userId, AppAiDecisionJevReqVO reqVO) {
        // 1. 获取 JEV API 客户端
        JevApi jevApi = modelService.getJevApi(reqVO.getModelId(), reqVO.getCustomApiKey());

        // 2. 转换 questions（兼容 Map<String, JevQuestion> 或 List<JevQuestion>）
        Map<String, JevQuestion> questionsMap = parseQuestions(reqVO.getQuestions());

        // 3. 构造 JEV 请求体
        String modelName = StrUtil.isNotBlank(reqVO.getModel()) ? reqVO.getModel() : "typesafe/jev";
        JevDecisionRequest request = JevDecisionRequest.builder()
                .model(modelName)
                .state(reqVO.getState())
                .questions(questionsMap)
                .build();
        if (reqVO.getExtra() != null && !reqVO.getExtra().isEmpty()) {
            request.getExtra().putAll(reqVO.getExtra());
        }

        // 4. 执行决策请求
        try {
            log.info("[executeJevDecision][userId({}) 模型({}) 问题数({}) 开始执行决策]",
                    userId, modelName, questionsMap.size());
            JevDecisionResponse response = jevApi.decide(request, Boolean.TRUE.equals(reqVO.getZdr()));
            log.info("[executeJevDecision][userId({}) 决策执行完成，耗时约 {}ms]",
                    userId, response.getResponseTimeMs());
            return response;
        } catch (Exception e) {
            log.error("[executeJevDecision][userId({}) 决策执行异常]", userId, e);
            throw exception(JEV_EXECUTE_ERROR, e.getMessage());
        }
    }

    private Map<String, JevQuestion> parseQuestions(Object questionsObj) {
        if (questionsObj == null) {
            return Collections.emptyMap();
        }
        if (questionsObj instanceof Map) {
            String json = JsonUtils.toJsonString(questionsObj);
            return JsonUtils.parseObject(json, new TypeReference<Map<String, JevQuestion>>() {});
        }
        if (questionsObj instanceof List) {
            String json = JsonUtils.toJsonString(questionsObj);
            List<Map<String, Object>> list = JsonUtils.parseObject(json, new TypeReference<List<Map<String, Object>>>() {});
            if (list == null) {
                return Collections.emptyMap();
            }
            Map<String, JevQuestion> map = new LinkedHashMap<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> item = list.get(i);
                String id = item.get("id") != null ? item.get("id").toString() : "q_" + (i + 1);
                JevQuestion q = JsonUtils.parseObject(JsonUtils.toJsonString(item), JevQuestion.class);
                map.put(id, q);
            }
            return map;
        }
        return Collections.emptyMap();
    }

}
