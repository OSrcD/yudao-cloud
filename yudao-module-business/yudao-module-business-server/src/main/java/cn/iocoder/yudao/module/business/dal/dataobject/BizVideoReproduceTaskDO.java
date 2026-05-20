package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 视频搬运任务 DO
 */
@TableName(value = "biz_video_reproduce_task", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizVideoReproduceTaskDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String originalVideoUrl;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> productConfigJson;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> charImages;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> productImages;
    private String resultJson;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> globalLocks;
    private String status;
    private String errorMsg;
    private Long conversationId;
    private Long payOrderId;
    private Boolean payStatus;
    private LocalDateTime payTime;
    private String combinedVideoUrl;
    private String remark;
    /**
     * 执行模式
     */
    private String execMode;
}

