package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

/**
 * 小红书笔记采集 DO
 */
@TableName(value = "xhs_note_collect", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XhsNoteCollectDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 笔记ID
     */
    private String noteId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 笔记类型：1-图文，2-视频
     */
    private Integer noteType;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记描述
     */
    private String noteDesc;

    /**
     * 笔记链接
     */
    private String noteUrl;

    /**
     * Shell跳转命令
     */
    private String shellCmd;

    /**
     * 笔记时间
     */
    private String publishTime;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 小红书号
     */
    private String redId;

    /**
     * 点赞数
     */
    private Integer likedCount;

    /**
     * 收藏数
     */
    private Integer collectedCount;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 完整JSON
     */
    private String rawJson;

    /**
     * PC分享链接
     */
    private String pcShareLink;

    /**
     * 最后采集PC分享链接时间
     */
    private java.time.LocalDateTime lastPcShareCollectTime;

    /**
     * 是否已采集评论
     */
    private Boolean isCommentCollected;

    /**
     * 链接是否已失效
     */
    private Boolean isLinkInvalid;

    /**
     * 链接失效判定时间
     */
    private java.time.LocalDateTime linkInvalidTime;

    /**
     * 最后采集评论时间
     */
    private java.time.LocalDateTime lastCommentCollectTime;

    /**
     * 是否监控中
     */
    private Boolean isMonitored;

    public String getNoteDesc() {
        return noteDesc;
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = noteDesc;
    }

}
