package cn.iocoder.yudao.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "用户 APP - 提交视频复刻任务 Response VO")
@Data
@Accessors(chain = true)
public class AppAiVideoReproduceCreateRespVO {

    @Schema(description = "视频复刻任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "支付订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long payOrderId;

}
