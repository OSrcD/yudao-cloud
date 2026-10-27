package cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TypeSafe JEV 决策问题通用定义
 * <p>
 * 支持标准字段（type, instructions, criteria 等），同时通过 @JsonAnySetter/@JsonAnyGetter
 * 动态无损透传任意扩展字段（如 min, max, choices, fallback, weight 等）。
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JevQuestion {

    /**
     * 问题类型：
     * noul: 是/否布尔倾向判断，返回 0~1 的概率
     * choice: 单项选择分类，从 criteria 中挑选
     * score: 等级分值评估，从至少2级梯阶中打分
     */
    private String type;

    /**
     * 判定指令/提问提示词
     */
    @JsonAlias({"question", "prompt"})
    private String instructions;

    /**
     * 判定标准 criteria：
     * - noul: 可选 Map&lt;String, String&gt;，如 {"true": "描述...", "false": "描述..."}
     * - choice: 必选 Map&lt;String, String&gt;，如 {"billing": "收费问题", "shipping": "物流"}
     * - score: 必选 List&lt;String&gt;，从低到高，如 ["Calm", "Frustrated", "Very angry"]
     */
    private Object criteria;

    /**
     * 存储所有其他动态扩展字段（如 min, max, choices, options, description 等），保证完全通用且不丢字段
     */
    @Builder.Default
    @JsonIgnore
    private Map<String, Object> extra = new LinkedHashMap<>();

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        if (extra == null) {
            extra = new LinkedHashMap<>();
        }
        extra.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return extra;
    }

    public Object get(String key) {
        return extra != null ? extra.get(key) : null;
    }

    public JevQuestion put(String key, Object value) {
        setExtra(key, value);
        return this;
    }

    // ========== 快捷构建工具 ==========

    public static JevQuestion noul(String instructions) {
        return JevQuestion.builder().type("noul").instructions(instructions).build();
    }

    public static JevQuestion noul(String instructions, Map<String, String> criteria) {
        return JevQuestion.builder().type("noul").instructions(instructions).criteria(criteria).build();
    }

    public static JevQuestion choice(String instructions, Map<String, String> criteria) {
        return JevQuestion.builder().type("choice").instructions(instructions).criteria(criteria).build();
    }

    public static JevQuestion score(String instructions, List<String> levels) {
        return JevQuestion.builder().type("score").instructions(instructions).criteria(levels).build();
    }

}
