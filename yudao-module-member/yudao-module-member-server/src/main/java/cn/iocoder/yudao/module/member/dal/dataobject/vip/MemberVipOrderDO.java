package cn.iocoder.yudao.module.member.dal.dataobject.vip;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员 VIP 开通订单 DO
 */
@TableName("member_vip_order")
@KeySequence("member_vip_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberVipOrderDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 套餐编号
     */
    private Long packageId;
    /**
     * 套餐名快照
     */
    private String packageName;
    /**
     * 支付金额，单位：分
     */
    private Integer price;
    /**
     * 开通天数快照
     */
    private Integer durationDays;
    /**
     * 是否已支付
     */
    private Boolean payStatus;
    /**
     * 支付订单编号
     */
    private Long payOrderId;
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    /**
     * 支付渠道
     */
    private String payChannelCode;

}
