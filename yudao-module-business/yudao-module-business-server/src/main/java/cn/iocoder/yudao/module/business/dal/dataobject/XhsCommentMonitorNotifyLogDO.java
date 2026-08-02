package cn.iocoder.yudao.module.business.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 小红书评论监控推送去重日志 DO
 */
@TableName("xhs_comment_monitor_notify_log")
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XhsCommentMonitorNotifyLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    /**
     * 评论 ID
     */
    private String commentId;

    /**
     * 命中关键词
     */
    private String keyword;

    /**
     * 推送类型：webhook / wecom / feishu
     */
    private String notifyType;

    /**
     * 推送地址快照
     */
    private String notifyUrl;

    /**
     * 推送状态：0-成功 1-失败
     */
    private Integer notifyStatus;

    /**
     * 第三方响应内容
     */
    private String notifyResp;

    private String creator;

    private LocalDateTime createTime;

}
