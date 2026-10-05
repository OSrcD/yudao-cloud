package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 小红书评论记录与去重分析 DO
 */
@TableName("biz_xhs_comment_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizXhsCommentRecordDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 当前App登录账号ID (MemberUser ID)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appAccountId;

    /**
     * 当前App登录手机号
     */
    private String appMobile;

    /**
     * 评论人小红书账号ID
     */
    private String xhsUserId;

    /**
     * 评论人小红书名称/昵称
     */
    private String xhsUserName;

    /**
     * 小红书作品分享链接(纯URL)
     */
    private String shareLink;

    /**
     * 完整分享文本内容
     */
    private String shareContent;

    /**
     * 小红书笔记ID(从链接解析提取)
     */
    private String noteId;

    /**
     * 小红书作品标题
     */
    private String noteTitle;

    /**
     * 发表的评论内容
     */
    private String commentContent;

    /**
     * 评论状态: 0正常, 1被吞/消失, 2被折叠, 3其他异常
     */
    private Integer commentStatus;

    /**
     * 检测状态: 0未检测, 1已检测, 2检测失败
     */
    private Integer checkStatus;

    /**
     * 最近一次检测状态时间
     */
    private LocalDateTime checkTime;

    /**
     * 备注说明
     */
    private String remark;

}
