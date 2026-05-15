package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 提示词评论 DO
 */
@TableName("biz_prompt_comment")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizPromptCommentDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long promptId;
    private Long operateGroupId;
    private String title;
    private String commentContent;
    private String remark;
    private Integer version;
    private Integer xhsInterceptCount;
    private Integer xhsNormalCount;
}
