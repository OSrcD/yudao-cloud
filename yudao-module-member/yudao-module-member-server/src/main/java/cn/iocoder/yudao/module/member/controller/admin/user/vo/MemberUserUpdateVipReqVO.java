package cn.iocoder.yudao.module.member.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员用户修改 VIP Request VO")
@Data
public class MemberUserUpdateVipReqVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23788")
    @NotNull(message = "用户编号不能为空")
    private Long id;

    @Schema(description = "操作类型：1=设置到期时间 2=延长天数 3=清空 VIP", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "操作类型不能为空")
    @Range(min = 1, max = 3, message = "操作类型不正确")
    private Integer changeType;

    @Schema(description = "VIP 到期时间（changeType=1 时必填）")
    private LocalDateTime vipExpireTime;

    @Schema(description = "延长天数（changeType=2 时必填）", example = "30")
    private Integer durationDays;

    @Schema(description = "是否已使用试用（不传则不改）", example = "true")
    private Boolean vipTrialUsed;

    @Schema(description = "修改原因", example = "运营补偿")
    private String reason;

}
