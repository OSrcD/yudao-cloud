package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

/**
 * 小红书笔记评论 DO (私信截流)
 */
@TableName(value = "xhs_note_comment", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XhsNoteCommentDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 笔记ID
     */
    private String noteId;

    /**
     * 评论ID
     */
    private String commentId;

    /**
     * 父级评论ID
     */
    private String parentCommentId;

    /**
     * 回复的目标评论ID
     */
    private String targetCommentId;

    /**
     * 评论发表者用户ID
     */
    private String userId;

    /**
     * 评论发表者昵称
     */
    private String nickname;

    /**
     * 小红书号
     */
    private String redId;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 评论正文
     */
    private String content;

    /**
     * IP属地
     */
    private String ipLocation;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 子评论数
     */
    private Integer subCommentCount;

    /**
     * 是否笔记作者评论：0-否，1-是
     */
    private Integer isAuthor;

    /**
     * 评论配图链接(JSON数组)
     */
    private String pictures;

    /**
     * 评论发布时间
     */
    private String commentTime;

    /**
     * 截流跟进状态：0-未触达，1-已发私信，2-已加微信，3-意向偏低/无效
     */
    private Integer interceptStatus;

    /**
     * 跟进备注
     */
    private String remark;

    /**
     * 原始评论JSON
     */
    private String rawJson;

}
