package cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api;

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
import java.util.Map;

/**
 * TypeSafe JEV 决策单一问题通用回答
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JevAnswer {

    /**
     * 问题类型：noul / choice / score
     */
    private String type;

    // ========== noul 题型结果 ==========
    /**
     * 是/Yes/True 的概率 (0.0 ~ 1.0)
     */
    private Double noul;

    // ========== choice 题型结果 ==========
    /**
     * 胜出的选项 key
     */
    private String choice;

    // ========== score 题型结果 ==========
    /**
     * 评分连续分值
     */
    private Double score;

    /**
     * 图例/等级标签定义
     */
    private Map<String, String> legend;

    // ========== 通用指标 ==========
    /**
     * 通用结果（noul 为 Double 概率；choice 为 String 或主要概率；score 为分值）
     */
    private Object result;

    /**
     * 置信度
     */
    private Double confidence;

    /**
     * 各选项概率分布 (key 为选项名，value 为对应概率)
     */
    private Map<String, Double> probabilities;

    /**
     * 捕获并保留所有未显式声明的动态响应字段
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

    /**
     * 获取数值型结果（兼容 noul、score 或 result）
     */
    public Double getNumericResult() {
        if (noul != null) {
            return noul;
        }
        if (score != null) {
            return score;
        }
        if (result instanceof Number) {
            return ((Number) result).doubleValue();
        }
        return null;
    }

}
