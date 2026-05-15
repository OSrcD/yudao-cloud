package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 提示词评论完成记录 DO
 */
@TableName("biz_prompt_comment_complete")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizPromptCommentCompleteDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long commentId;
    private Long mediaAccountId;
    private String remark;
    private Integer version;
    private String xhsNoteInfo;
    private Integer checkStatus;
    private Integer commentStatus;

    // 扩展字段 (非数据库字段)
    @TableField(exist = false)
    private String commentContent;
    @TableField(exist = false)
    private String accountName;
}
