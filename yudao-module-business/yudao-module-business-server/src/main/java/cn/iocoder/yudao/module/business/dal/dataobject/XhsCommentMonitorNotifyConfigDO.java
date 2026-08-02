package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 小红书评论监控推送配置 DO
 */
@TableName("xhs_comment_monitor_notify_config")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XhsCommentMonitorNotifyConfigDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 是否开启推送
     */
    private Boolean enabled;

    /**
     * 推送类型：webhook / wecom / feishu
     */
    private String notifyType;

    /**
     * Webhook 地址
     */
    private String notifyUrl;

    /**
     * 签名密钥（预留）
     */
    private String secret;

}
