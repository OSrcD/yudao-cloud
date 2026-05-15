package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 自媒体账号 DO
 */
@TableName("biz_media_account")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizMediaAccountDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String accountId;
    private String accountName;
    private Integer accountPlatform;
    private Integer accountType;
    private String accountUrl;
    private String phoneNumber;
    private Integer followerCount;
    private Integer status;
    private String remark;
    private Integer version;
}
