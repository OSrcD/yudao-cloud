package cn.iocoder.yudao.module.member.controller.app.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 会员 VIP 状态 Response VO")
@Data
public class AppMemberVipStatusRespVO {

    @Schema(description = "是否有效", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean active;

    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @Schema(description = "当前是否处于试用期", example = "true")
    private Boolean trial;

    @Schema(description = "是否已发放过试用", example = "true")
    private Boolean trialUsed;

}
