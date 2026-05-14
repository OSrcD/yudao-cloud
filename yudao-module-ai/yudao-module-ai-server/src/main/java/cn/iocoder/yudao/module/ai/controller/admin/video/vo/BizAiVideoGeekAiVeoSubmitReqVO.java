package cn.iocoder.yudao.module.ai.controller.admin.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 视频生成 Request VO")
@Data
public class BizAiVideoGeekAiVeoSubmitReqVO {

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "猫咪带着耳机听着歌走路")
    @NotEmpty(message = "提示词不能为空")
    private String prompt;

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型编号不能为空")
    private Long modelId;

    @Schema(description = "分辨率", example = "720x1280")
    private String size;

    @Schema(description = "时长（秒）", example = "8")
    private Integer seconds;

    @Schema(description = "参考图 URL 列表", example = "[\"https://www.iocoder.cn/1.png\"]")
    private List<String> inputReferences;

}
