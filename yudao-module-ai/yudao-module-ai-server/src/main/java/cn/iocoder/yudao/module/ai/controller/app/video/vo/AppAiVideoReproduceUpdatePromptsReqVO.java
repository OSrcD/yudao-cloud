package cn.iocoder.yudao.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 APP - AI 视频复刻修改提示词 Request VO")
@Data
public class AppAiVideoReproduceUpdatePromptsReqVO {

    @Schema(description = "分镜 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "分镜 ID 不能为空")
    private Long frameId;

    @Schema(description = "分镜结构描述 (originalPrompt)")
    private String originalPrompt;

    @Schema(description = "宫格图像处理提示词 (gridImagePromptEn)")
    private String gridImagePromptEn;

    @Schema(description = "宫格图像处理提示词 - 中文 (gridImagePromptZh)")
    private String gridImagePromptZh;

    @Schema(description = "视频生图提示词 (imagePromptForModelEn)")
    private String imagePromptForModelEn;

    @Schema(description = "视频生图提示词 - 中文 (imagePromptZhCheck)")
    private String imagePromptZhCheck;

    @Schema(description = "第二阶段：视频重塑提示词 (i2vPromptEn)")
    private String i2vPromptEn;

    @Schema(description = "第二阶段：视频重塑提示词 - 中文 (i2vPromptZh)")
    private String i2vPromptZh;

    @Schema(description = "单图：视频重塑提示词 (singleI2vPromptEn)")
    private String singleI2vPromptEn;

    @Schema(description = "单图：视频重塑提示词 - 中文 (singleI2vPromptZh)")
    private String singleI2vPromptZh;

    @Schema(description = "人物单图：生图提示词 (peopleSingleImagePromptEn)")
    private String peopleSingleImagePromptEn;

    @Schema(description = "人物单图：生图提示词 - 中文 (peopleSingleImagePromptZh)")
    private String peopleSingleImagePromptZh;

    @Schema(description = "人物单图：视频重塑提示词 (peopleSingleI2vPromptEn)")
    private String peopleSingleI2vPromptEn;

    @Schema(description = "人物单图：视频重塑提示词 - 中文 (peopleSingleI2vPromptZh)")
    private String peopleSingleI2vPromptZh;
}
