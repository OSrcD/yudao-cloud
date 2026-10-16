package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

/**
 * 小红书不评论官方号名单 DO
 */
@TableName("biz_xhs_official_account")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizXhsOfficialAccountDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * App用户编号 (关联 member_user.id)
     */
    private Long userId;

    /**
     * App用户手机号码/账号
     */
    private String mobile;

    /**
     * 作者小红书号
     */
    private String accountId;

    /**
     * 作者昵称
     */
    private String nickname;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 状态: 0开启过滤(生效), 1关闭过滤(禁用)
     */
    private Integer status;

}
