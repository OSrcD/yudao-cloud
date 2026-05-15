package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.Map;

/**
 * 本地任务 DO
 */
@TableName(value = "biz_local_task", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizLocalTaskDO extends TenantBaseDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskType;
    private Long refTaskId;
    private Long refFrameId;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> execParams;
    private Integer status;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> resultData;
    private String errorMsg;
}
