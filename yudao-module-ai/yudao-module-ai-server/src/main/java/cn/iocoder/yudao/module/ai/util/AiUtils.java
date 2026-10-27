package cn.iocoder.yudao.module.ai.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springaicommunity.moonshot.MoonshotChatOptions;
import org.springaicommunity.qianfan.QianFanChatOptions;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;

import java.util.*;

/**
 * Spring AI 工具类
 *
 * @author 芋道源码
 */
public class AiUtils {

    public static final String TOOL_CONTEXT_LOGIN_USER = "LOGIN_USER";
    public static final String TOOL_CONTEXT_TENANT_ID = "TENANT_ID";

    public static ChatOptions buildChatOptions(AiPlatformEnum platform, String model, Double temperature, Integer maxTokens) {
        return buildChatOptions(platform, model, temperature, maxTokens, null, null);
    }

    public static ChatOptions buildChatOptions(AiPlatformEnum platform, String model, Double temperature, Integer maxTokens,
                                               List<ToolCallback> toolCallbacks, Map<String, Object> toolContext) {
        return buildChatOptions(platform, model, temperature, maxTokens, toolCallbacks, toolContext, null);
    }

    public static ChatOptions buildChatOptions(AiPlatformEnum platform, String model, Double temperature, Integer maxTokens,
                                               List<ToolCallback> toolCallbacks, Map<String, Object> toolContext, String responseFormat) {
        toolCallbacks = ObjUtil.defaultIfNull(toolCallbacks, Collections.emptyList());
        toolContext = ObjUtil.defaultIfNull(toolContext, Collections.emptyMap());
        // noinspection EnhancedSwitchMigration
        switch (platform) {
            case TONG_YI:
                return DashScopeChatOptions.builder().withModel(model).withTemperature(temperature).withMaxToken(maxTokens)
                        .withEnableThinking(true) // TODO 芋艿：默认都开启 thinking 模式，后续可以让用户配置
                        .withToolCallbacks(toolCallbacks).withToolContext(toolContext).build();
            case YI_YAN:
                return QianFanChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens).build();
            case DEEP_SEEK:
            case DOU_BAO: // 复用 DeepSeek 客户端
            case HUN_YUAN: // 复用 DeepSeek 客户端
            case SILICON_FLOW: // 复用 DeepSeek 客户端
            case XING_HUO: // 复用 DeepSeek 客户端
                return DeepSeekChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case ZHI_PU:
                return ZhiPuAiChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case MINI_MAX:
                return MiniMaxChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case MOONSHOT:
                return MoonshotChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case OPENAI:
            case GEMINI:
            case BAI_CHUAN: // 复用 OpenAI 客户端
            case GROK: // 复用 OpenAI 客户端
            case COMMAND_CODE: // 复用 OpenAI 客户端
                OpenAiChatOptions options = OpenAiChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
                if (StrUtil.isNotEmpty(responseFormat) && !StrUtil.containsIgnoreCase(model, "jev")) {
                    options.setResponseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT).build());
                }
                return options;
            case AZURE_OPENAI:
                return AzureOpenAiChatOptions.builder().deploymentName(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case ANTHROPIC:
                return AnthropicChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case OLLAMA:
                return OllamaChatOptions.builder().model(model).temperature(temperature).numPredict(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            default:
                throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
        }
    }

    public static Message buildMessage(String type, String content) {
        if (MessageType.USER.getValue().equals(type)) {
            return new UserMessage(content);
        }
        if (MessageType.ASSISTANT.getValue().equals(type)) {
            return new AssistantMessage(content);
        }
        if (MessageType.SYSTEM.getValue().equals(type)) {
            return new SystemMessage(content);
        }
        if (MessageType.TOOL.getValue().equals(type)) {
            throw new UnsupportedOperationException("暂不支持 tool 消息：" + content);
        }
        throw new IllegalArgumentException(StrUtil.format("未知消息类型({})", type));
    }

    public static Map<String, Object> buildCommonToolContext() {
        Map<String, Object> context = new HashMap<>();
        context.put(TOOL_CONTEXT_LOGIN_USER, SecurityFrameworkUtils.getLoginUser());
        context.put(TOOL_CONTEXT_TENANT_ID, TenantContextHolder.getTenantId());
        return context;
    }

    @SuppressWarnings("ConstantValue")
    public static String getChatResponseContent(ChatResponse response) {
        if (response == null
                || response.getResult() == null
                || response.getResult().getOutput() == null) {
            return null;
        }
        return response.getResult().getOutput().getText();
    }

    @SuppressWarnings("ConstantValue")
    public static String getChatResponseReasoningContent(ChatResponse response) {
        if (response == null
                || response.getResult() == null
                || response.getResult().getOutput() == null) {
            return null;
        }
        if (response.getResult().getOutput() instanceof DeepSeekAssistantMessage) {
            return ((DeepSeekAssistantMessage) (response.getResult().getOutput())).getReasoningContent();
        }
        return null;
    }

    private static final Pattern JEV_PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*([^}]+?)\\s*\\}\\}");

    /**
     * 针对 JEV 决策场景，仅对 systemMessage 中 state.my_business（或顶层 my_business）内部的占位符，
     * 从数据库 ai_chat_role 表的 business_info 字段中提取真实业务画像进行替换。
     *
     * @param systemMessage JEV 决策系统提示词（JSON 格式）
     * @param businessInfoJson 数据库 ai_chat_role 表中存储的 business_info 业务画像 JSON
     * @return 替换后的 JEV 系统提示词
     */
    public static String renderJevMyBusinessPlaceholders(String systemMessage, String businessInfoJson) {
        if (StrUtil.isBlank(systemMessage) || !systemMessage.contains("my_business")) {
            return systemMessage;
        }
        if (StrUtil.isBlank(businessInfoJson)) {
            return systemMessage;
        }

        try {
            // 1. 解析 business_info 为 Map
            Map<String, Object> bizMap = JsonUtils.parseObject(businessInfoJson, new TypeReference<Map<String, Object>>() {});
            if (CollUtil.isEmpty(bizMap)) {
                return systemMessage;
            }

            // 2. 解析 systemMessage 为 LinkedHashMap 保留原有顺序
            Map<String, Object> rootMap = JsonUtils.parseObject(systemMessage, new TypeReference<LinkedHashMap<String, Object>>() {});
            if (rootMap == null) {
                return systemMessage;
            }

            // 3. 定位 my_business 节点（严格只针对 my_business 内部，绝不触碰 questions/criteria，同时移除 business_profile）
            List<Map<String, Object>> targetMyBusinessList = new ArrayList<>();
            if (rootMap.get("state") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> stateMap = (Map<String, Object>) rootMap.get("state");
                stateMap.remove("business_profile");
                if (stateMap.get("my_business") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> mb = (Map<String, Object>) stateMap.get("my_business");
                    targetMyBusinessList.add(mb);
                }
            }
            if (rootMap.get("my_business") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mb = (Map<String, Object>) rootMap.get("my_business");
                if (!targetMyBusinessList.contains(mb)) {
                    targetMyBusinessList.add(mb);
                }
            }

            if (targetMyBusinessList.isEmpty()) {
                return systemMessage;
            }

            // 4. 解析业务画像键值（支持标准键与别名兼容）
            Map<String, String> resolvedValues = buildResolvedBusinessValues(bizMap);

            // 5. 遍历 my_business 节点，严格仅对 my_business 内部占位符执行替换
            boolean modified = false;
            for (Map<String, Object> myBusinessMap : targetMyBusinessList) {
                for (Map.Entry<String, Object> entry : myBusinessMap.entrySet()) {
                    String key = entry.getKey();
                    Object valObj = entry.getValue();
                    if (valObj instanceof String) {
                        String strVal = (String) valObj;
                        String replaced = replaceSinglePlaceholder(key, strVal, resolvedValues);
                        if (!Objects.equals(strVal, replaced)) {
                            entry.setValue(replaced);
                            modified = true;
                        }
                    }
                }
            }

            if (modified) {
                return JsonUtils.toJsonPrettyString(rootMap);
            }
        } catch (Exception ignored) {
            // 发生异常时容错降级返回原始系统提示词
        }
        return systemMessage;
    }

    private static Map<String, String> buildResolvedBusinessValues(Map<String, Object> bizMap) {
        Map<String, String> map = new LinkedHashMap<>();
        // 优先标准字段
        map.put("industry", getBizValue(bizMap, "industry"));
        map.put("identity", getBizValue(bizMap, "identity"));
        map.put("product", getBizValue(bizMap, "product", "business"));
        map.put("core_advantage", getBizValue(bizMap, "core_advantage", "coreAdvantage", "advantage", "advantages"));
        map.put("target_audience", getBizValue(bizMap, "target_audience", "targetAudience", "targetCustomer", "targetClients"));
        map.put("pain_points", getBizValue(bizMap, "pain_points", "painPoints", "customerPain"));
        map.put("intercept_goal", getBizValue(bizMap, "intercept_goal", "interceptGoal", "captureGoal", "goal"));

        // 把 bizMap 中的其他所有字段也放入 map（小写及下划线形式备用）
        bizMap.forEach((k, v) -> {
            if (v != null) {
                String valStr = v.toString().trim();
                map.putIfAbsent(k.toLowerCase(), valStr);
                map.putIfAbsent(StrUtil.toUnderlineCase(k).toLowerCase(), valStr);
            }
        });
        return map;
    }

    private static String getBizValue(Map<String, Object> bizMap, String... keys) {
        for (String key : keys) {
            if (bizMap.containsKey(key) && bizMap.get(key) != null) {
                String val = bizMap.get(key).toString().trim();
                if (StrUtil.isNotEmpty(val)) {
                    return val;
                }
            }
            // 尝试忽略大小写
            for (Map.Entry<String, Object> entry : bizMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
                    String val = entry.getValue().toString().trim();
                    if (StrUtil.isNotEmpty(val)) {
                        return val;
                    }
                }
            }
        }
        return "";
    }

    private static String replaceSinglePlaceholder(String fieldKey, String strVal, Map<String, String> resolvedValues) {
        if (StrUtil.isBlank(strVal)) {
            return strVal;
        }
        Matcher matcher = JEV_PLACEHOLDER_PATTERN.matcher(strVal);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            matcher.reset();
            while (matcher.find()) {
                String phKey = matcher.group(1).trim().toLowerCase();
                String replacement = resolvedValues.getOrDefault(phKey, "");
                if (StrUtil.isEmpty(replacement)) {
                    // 若占位符名字找不到，尝试用字段本身的 key 查找
                    replacement = resolvedValues.getOrDefault(fieldKey.toLowerCase(), "");
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        // 如果值本身没有花括号，但当前值为空而 resolvedValues 有值
        if (StrUtil.isBlank(strVal) && resolvedValues.containsKey(fieldKey.toLowerCase())) {
            return resolvedValues.get(fieldKey.toLowerCase());
        }
        return strVal;
    }

}