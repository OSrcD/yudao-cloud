package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 视频搬运截帧记录 DO
 */
@TableName(value = "biz_video_reproduce_frame", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizVideoReproduceFrameDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long taskId;
    private Integer frameIndex;
    private String guId;
    private String timestampSec;
    private String originalImageUrl;
    private String polishedImageUrl;
    private String i2vPromptEn;
    private String i2vPromptZh;
    private String washMode;
    private String washCustomPrompt;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> washRefImages;
    private String prevPolishedUrl;
    private String generatedVideoUrl;
    private String prevVideoUrl;
    private String status;
    private String aiImageId;
    private String aiVideoId;
    private String audioUrl;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> audioConfigJson;
}
