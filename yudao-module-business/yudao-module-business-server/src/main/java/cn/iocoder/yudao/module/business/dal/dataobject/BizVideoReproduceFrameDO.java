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

    /**
     * 分镜原始结构描述/裂变微动提示词
     */
    private String originalPrompt;
    /**
     * 宫格生图英文提示词
     */
    private String gridImagePromptEn;
    /**
     * 宫格生图中文提示词
     */
    private String gridImagePromptZh;
    /**
     * 视频生图英文提示词 (image_prompt_for_model_en)
     */
    private String imagePromptForModelEn;
    /**
     * 视频生图中文提示词 (image_prompt_zh_check)
     */
    private String imagePromptZhCheck;
    /**
     * 关联的引用商品原图列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> gridSourceImages;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> sourceImageIndices;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> singleImageSourceIndices;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> singleSourceImages;
    private String singleI2vPromptEn;
    private String singleI2vPromptZh;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> peopleSingleImageSourceIndices;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> peopleSingleSourceImages;
    private String peopleSingleImagePromptEn;
    private String peopleSingleImagePromptZh;
    private String peopleSingleI2vPromptEn;
    private String peopleSingleI2vPromptZh;
}

