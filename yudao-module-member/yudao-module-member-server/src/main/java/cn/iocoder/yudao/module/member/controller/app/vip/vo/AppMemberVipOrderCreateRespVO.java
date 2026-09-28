package cn.iocoder.yudao.module.member.controller.app.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 创建 VIP 开通订单 Response VO")
@Data
public class AppMemberVipOrderCreateRespVO {

    @Schema(description = "业务订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "支付订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long payOrderId;

}
