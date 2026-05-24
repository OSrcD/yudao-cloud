package cn.iocoder.yudao.module.business.api.video.dto;

import lombok.Data;

@Data
public class BizVideoReproduceFrameDTO {
    private Long id;
    private Long taskId;
    private String i2vPromptZh;
    private String i2vPromptEn;
    private Integer frameIndex;
    private String guId;
    private String timestampSec;
    private String originalImageUrl;
    private String polishedImageUrl;
    private String generatedVideoUrl;
    private String resultVideoUrl;
    private String status;
    private String aiImageId;
    private String aiVideoId;
    
    private String gridImagePromptEn;
    private String gridImagePromptZh;
    private String imagePromptForModelEn;
    private String imagePromptZhCheck;
    private String originalPrompt;
    private java.util.List<String> gridSourceImages;
    
    private java.util.List<Integer> singleImageSourceIndices;
    private java.util.List<String> singleSourceImages;
    private String singleI2vPromptEn;
    private String singleI2vPromptZh;
    
    private java.util.List<Integer> peopleSingleImageSourceIndices;
    private java.util.List<String> peopleSingleSourceImages;
    private String peopleSingleImagePromptEn;
    private String peopleSingleImagePromptZh;
    private String peopleSingleI2vPromptEn;
    private String peopleSingleI2vPromptZh;
}
