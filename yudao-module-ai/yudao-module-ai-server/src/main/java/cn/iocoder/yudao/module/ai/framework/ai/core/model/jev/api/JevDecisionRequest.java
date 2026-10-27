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
 * TypeSafe JEV 决策请求通用对象
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JevDecisionRequest {

    public static final String DEFAULT_MODEL = "typesafe/jev";

    /**
     * 模型标识，默认为 typesafe/jev，支持 typesafe/jev-1.14.0, jev-preview 等
     */
    @Builder.Default
    private String model = DEFAULT_MODEL;

    /**
     * 上下文状态/输入（支持纯字符串或复杂 JSON 对象/Map，最大 64K tokens）
     */
    private Object state;

    /**
     * 问题映射，key 为问题唯一标识符，单次请求最多支持 100 个问题
     */
    private Map<String, JevQuestion> questions;

    /**
     * 顶层任意动态扩展参数，保证完全通用且不丢字段
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

}
