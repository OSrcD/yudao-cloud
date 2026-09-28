package cn.iocoder.yudao.module.member.controller.app.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 会员 VIP 套餐 Response VO")
@Data
public class AppMemberVipPackageRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "套餐名", requiredMode = Schema.RequiredMode.REQUIRED, example = "月会员")
    private String name;

    @Schema(description = "价格，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "2900")
    private Integer price;

    @Schema(description = "时长天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer durationDays;

}
