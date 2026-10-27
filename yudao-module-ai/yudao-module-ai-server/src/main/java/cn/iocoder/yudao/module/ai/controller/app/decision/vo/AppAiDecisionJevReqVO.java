package cn.iocoder.yudao.module.ai.controller.app.decision.vo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Schema(description = "用户 App - JEV 决策请求 VO")
@Data
public class AppAiDecisionJevReqVO {

    @Schema(description = "AI 模型编号（若未提供则使用系统默认 Command Code 密钥）", example = "10")
    private Long modelId;

    @Schema(description = "自定义 API 密钥（可为空，覆盖系统配置）", example = "sk-...")
    private String customApiKey;

    @Schema(description = "决策模型标识，默认 typesafe/jev，支持 jev-preview 等", example = "jev-preview")
    private String model;

    @Schema(description = "待决策的状态上下文（支持纯文本或复杂 JSON 对象/Map，上限 64K token）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态上下文不能为空")
    private Object state;

    @Schema(description = "决策问题集合（支持 Map<String, JevQuestion> 或 List<JevQuestion>，上限 100 题）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "决策问题不能为空")
    private Object questions;

    @Schema(description = "是否开启零数据保留 (Zero Data Retention)", example = "true")
    private Boolean zdr;

    /**
     * 顶层任意未知/扩展请求字段
     */
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
