package cn.iocoder.yudao.module.member.controller.admin.vip.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - VIP 套餐 Response VO")
@Data
public class MemberVipPackageRespVO {

    private Long id;
    private String name;
    private Integer price;
    private Integer durationDays;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;

}
