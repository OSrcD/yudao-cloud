package cn.iocoder.yudao.module.member.controller.admin.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - VIP 订单 Response VO")
@Data
public class MemberVipOrderRespVO {

    private Long id;
    private Long userId;
    private String userNickname;
    private String userMobile;
    private Long packageId;
    private String packageName;
    private Integer price;
    private Integer durationDays;
    private Boolean payStatus;
    private Long payOrderId;
    private LocalDateTime payTime;
    private String payChannelCode;
    private LocalDateTime createTime;

}
