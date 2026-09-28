package cn.iocoder.yudao.module.member.dal.dataobject.vip;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员 VIP 套餐 DO
 */
@TableName("member_vip_package")
@KeySequence("member_vip_package_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberVipPackageDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 套餐名
     */
    private String name;
    /**
     * 价格，单位：分
     */
    private Integer price;
    /**
     * 时长天数
     */
    private Integer durationDays;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 排序，越小越靠前
     */
    private Integer sort;

}
