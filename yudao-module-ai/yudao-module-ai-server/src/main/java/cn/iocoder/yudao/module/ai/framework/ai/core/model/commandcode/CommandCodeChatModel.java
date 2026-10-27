package cn.iocoder.yudao.module.ai.framework.ai.core.model.commandcode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevDecisionRequest;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevDecisionResponse;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevQuestion;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Command Code 平台 {@link ChatModel} 实现类
 * <p>
 * 智能双引擎调度：
 * 1. 当请求模型包含 "jev" 或用户在对话中发送了 JEV JSON 数据时，自动转发至 JEV 决策模型 (POST /provider/v1/systemone)
 * 2. 其它常规模型调用则转发至 OpenAI 兼容接口（如 mimo、deepseek 等）
 *
 * @author fansili
 */
@Slf4j
@RequiredArgsConstructor
public class CommandCodeChatModel implements ChatModel {

    public static final String DEFAULT_BASE_URL = "https://api.commandcode.ai";

    private final ChatModel openAiChatModel;
    private final JevApi jevApi;

    @Override
    public ChatResponse call(Prompt prompt) {
        if (isJevRequest(prompt)) {
            return executeJev(prompt);
        }
        return openAiChatModel.call(prompt);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        if (isJevRequest(prompt)) {
            try {
                ChatResponse response = executeJev(prompt);
                return Flux.just(response);
            } catch (Exception e) {
                log.error("[CommandCodeChatModel][执行 JEV 流式响应异常]", e);
                return Flux.error(e);
            }
        }
        return openAiChatModel.stream(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return openAiChatModel != null ? openAiChatModel.getDefaultOptions() : null;
    }

    /**
     * 判断是否属于 JEV 决策请求
     */
    private boolean isJevRequest(Prompt prompt) {
        ChatOptions options = prompt.getOptions();
        if (options != null && StrUtil.containsIgnoreCase(options.getModel(), "jev")) {
            return true;
        }
        String userContent = getLatestUserContent(prompt);
        if (StrUtil.isNotBlank(userContent)) {
            String trimmed = userContent.trim();
            if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                return (trimmed.contains("\"questions\"") && trimmed.contains("\"state\""))
                        || (trimmed.contains("\"questions\"") && trimmed.contains("\"type\""));
            }
        }
        return false;
    }

    private ChatResponse executeJev(Prompt prompt) {
        String userContent = getLatestUserContent(prompt);
        JevDecisionRequest request = parseJevRequest(userContent, prompt);
        log.info("[CommandCodeChatModel][调用 JEV 决策模型] 组装完成的完整 JEV 请求报文:\n{}", JsonUtils.toJsonPrettyString(request));
        JevDecisionResponse response = jevApi.decide(request);
        String prettyJson = JsonUtils.toJsonPrettyString(response);
        AssistantMessage assistantMessage = new AssistantMessage(prettyJson);
        Generation generation = new Generation(assistantMessage);
        return new ChatResponse(List.of(generation));
    }

    private JevDecisionRequest parseJevRequest(String userContent, Prompt prompt) {
        String modelName = "typesafe/jev";
        if (prompt.getOptions() != null && StrUtil.isNotBlank(prompt.getOptions().getModel())) {
            modelName = prompt.getOptions().getModel();
        }

        // 1. 最高优先级：如果用户输入本身就是合法的 JEV JSON（包含 questions 或 state），原汁原味透传
        if (StrUtil.isNotBlank(userContent)) {
            Map<String, Object> userJsonMap = extractJsonMap(userContent);
            if (userJsonMap != null && (userJsonMap.containsKey("questions") || userJsonMap.containsKey("state"))) {
                String payloadModel = (String) userJsonMap.get("model");
                if (StrUtil.isNotBlank(payloadModel)) {
                    modelName = payloadModel;
                }
                Object state = userJsonMap.get("state");
                Object questionsObj = userJsonMap.get("questions");
                Map<String, JevQuestion> questionsMap = parseQuestions(questionsObj);

                JevDecisionRequest req = JevDecisionRequest.builder()
                        .model(modelName)
                        .state(state)
                        .questions(questionsMap)
                        .build();
                // 复制其他可能的扩展字段
                userJsonMap.forEach((k, v) -> {
                    if (!"model".equals(k) && !"state".equals(k) && !"questions".equals(k)) {
                        req.getExtra().put(k, v);
                    }
                });
                return req;
            }
        }

        // 2. 第二优先级：用户输入纯文本，结合角色的系统设定（System Message）智能装配
        String systemContent = getSystemContent(prompt);
        if (StrUtil.isNotBlank(systemContent)) {
            Map<String, Object> systemJsonMap = extractJsonMap(systemContent);
            if (systemJsonMap != null && systemJsonMap.containsKey("questions")) {
                // 从角色人设中提取通用考题
                Map<String, JevQuestion> questionsMap = parseQuestions(systemJsonMap.get("questions"));

                // 组装标准 state
                Map<String, Object> stateMap = new LinkedHashMap<>();
                if (systemJsonMap.get("state") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> originState = (Map<String, Object>) systemJsonMap.get("state");
                    stateMap.putAll(originState);
                }

                // 仅保留 my_business，移除 business_profile
                stateMap.remove("business_profile");
                if (!stateMap.containsKey("my_business") && systemJsonMap.containsKey("my_business")) {
                    stateMap.put("my_business", systemJsonMap.get("my_business"));
                }

                // 注入本次用户输入
                stateMap.put("user_input", userContent);

                return JevDecisionRequest.builder()
                        .model(modelName)
                        .state(stateMap)
                        .questions(questionsMap)
                        .build();
            }

            // 角色设定为普通文本时，构建通用背景与中立评估
            Map<String, Object> stateMap = new LinkedHashMap<>();
            stateMap.put("role_context", systemContent);
            stateMap.put("user_input", userContent);

            Map<String, JevQuestion> defaultQuestions = new LinkedHashMap<>();
            defaultQuestions.put("evaluation", JevQuestion.noul("评估待审输入内容是否符合角色设定的标准与要求？"));

            return JevDecisionRequest.builder()
                    .model(modelName)
                    .state(stateMap)
                    .questions(defaultQuestions)
                    .build();
        }

        // 3. 第三优先级：用户只输入普通文本且无角色设定，进行通用中立质量评估
        Map<String, JevQuestion> defaultQuestions = new LinkedHashMap<>();
        defaultQuestions.put("validity", JevQuestion.noul("根据输入内容，评估该信息的真实性与合规性？"));
        return JevDecisionRequest.builder()
                .model(modelName)
                .state(userContent)
                .questions(defaultQuestions)
                .build();
    }

    private String getSystemContent(Prompt prompt) {
        if (prompt == null || CollUtil.isEmpty(prompt.getInstructions())) {
            return null;
        }
        for (Message message : prompt.getInstructions()) {
            if (message.getMessageType() == MessageType.SYSTEM && StrUtil.isNotBlank(message.getText())) {
                return message.getText();
            }
        }
        return null;
    }

    private Map<String, Object> extractJsonMap(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        String trimmed = text.trim();
        // 兼容 markdown 代码块 ```json ... ```
        if (trimmed.contains("```")) {
            int start = trimmed.indexOf("```");
            int end = trimmed.lastIndexOf("```");
            if (end > start) {
                String sub = trimmed.substring(start + 3, end).trim();
                if (sub.startsWith("json")) {
                    sub = sub.substring(4).trim();
                }
                trimmed = sub;
            }
        }
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            String jsonStr = trimmed.substring(firstBrace, lastBrace + 1);
            try {
                return JsonUtils.parseObject(jsonStr, new TypeReference<Map<String, Object>>() {});
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String getLatestUserContent(Prompt prompt) {
        if (prompt == null || CollUtil.isEmpty(prompt.getInstructions())) {
            return null;
        }
        // 1. 优先寻找包含 JEV 结构标识（如 "questions"）的用户输入，防止被知识库/搜索追加内容干扰
        for (int i = prompt.getInstructions().size() - 1; i >= 0; i--) {
            Message message = prompt.getInstructions().get(i);
            if (message.getMessageType() == MessageType.USER && message.getText() != null) {
                if (message.getText().contains("\"questions\"")) {
                    return message.getText();
                }
            }
        }
        // 2. 否则查找最新一条非系统包装的真实用户消息
        for (int i = prompt.getInstructions().size() - 1; i >= 0; i--) {
            Message message = prompt.getInstructions().get(i);
            if (message.getMessageType() == MessageType.USER && message.getText() != null) {
                String text = message.getText();
                if (!text.startsWith("<Reference>") && !text.startsWith("<WebSearch>")) {
                    return text;
                }
            }
        }
        return prompt.getInstructions().get(prompt.getInstructions().size() - 1).getText();
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
