package cn.iocoder.yudao.module.member.controller.app.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 创建 VIP 开通订单 Request VO")
@Data
public class AppMemberVipOrderCreateReqVO {

    @Schema(description = "套餐编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "套餐编号不能为空")
    private Long packageId;

}
