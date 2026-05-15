package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

/**
 * 提示词模板 DO
 */
@TableName("biz_prompt_template")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizPromptTemplateDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String template;
    private Integer templateType;
    private Integer status;
    private String remark;
}
