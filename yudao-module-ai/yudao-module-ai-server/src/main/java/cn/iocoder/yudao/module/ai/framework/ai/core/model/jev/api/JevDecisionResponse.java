package cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * TypeSafe JEV 决策响应
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JevDecisionResponse {

    /**
     * 模型标识
     */
    private String model;

    /**
     * 回答映射，key 为问题 ID
     */
    private Map<String, JevAnswer> answers;

    /**
     * 响应耗时（毫秒）
     */
    private Long responseTimeMs;

    /**
     * Token 消耗
     */
    private Usage usage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("input_tokens")
        private Integer inputTokens;

        @JsonProperty("output_tokens")
        private Integer outputTokens;
    }

}
