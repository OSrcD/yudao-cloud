package cn.iocoder.yudao.module.member.controller.admin.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - VIP 套餐创建/更新 Request VO")
@Data
public class MemberVipPackageSaveReqVO {

    @Schema(description = "编号，更新时必填", example = "1")
    private Long id;

    @Schema(description = "套餐名", requiredMode = Schema.RequiredMode.REQUIRED, example = "月会员")
    @NotEmpty(message = "套餐名不能为空")
    private String name;

    @Schema(description = "价格，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "2900")
    @NotNull(message = "价格不能为空")
    private Integer price;

    @Schema(description = "时长天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    @NotNull(message = "时长天数不能为空")
    private Integer durationDays;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序", example = "1")
    private Integer sort;

}
