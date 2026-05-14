package cn.iocoder.yudao.module.ai.controller.admin.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI 视频生成 (Aihubmix) Request VO")
@Data
public class BizAiVideoAihubmixSubmitReqVO {

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "一只猫在钢琴上弹奏爵士乐")
    @NotEmpty(message = "提示词不能为空")
    private String prompt;

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型编号不能为空")
    private Long modelId;

    @Schema(description = "分辨率 (宽x高 或 720p/1080p/4k)", example = "1280x720")
    private String size;

    @Schema(description = "时长（秒）", example = "5")
    private String seconds;

    @Schema(description = "参考图片 URL 或 Base64", example = "https://www.iocoder.cn/1.png")
    private String inputReference;

}
