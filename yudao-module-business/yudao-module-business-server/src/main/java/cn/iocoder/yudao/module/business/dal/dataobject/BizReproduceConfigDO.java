package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 搬运配置 DO
 */
@TableName(value = "biz_reproduce_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizReproduceConfigDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String configName;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> productConfigJson;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> charImages;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> productImages;
    private String remark;
}
