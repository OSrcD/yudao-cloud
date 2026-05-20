package cn.iocoder.yudao.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - AI 视频复刻配置 Response VO")
@Data
public class AppAiVideoReproduceConfigRespVO {

    @Schema(description = "默认洗图模型编号", example = "1")
    private Long defaultWashModelId;

    @Schema(description = "默认视频生成模型编号", example = "2")
    private Long defaultVideoModelId;

}
