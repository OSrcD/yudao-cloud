package cn.iocoder.yudao.module.ai.controller.admin.image.vo.geekai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - AI 绘画生成（GeekAI） Request VO")
@Data
public class AiGeekAiImagineReqVO {

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国神龙")
    @NotEmpty(message = "提示词不能为空!")
    private String prompt;

    @Schema(description = "模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型编号不能为空")
    private Long modelId;

    @Schema(description = "图片宽度", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "图片宽度不能为空")
    private Integer width;

    @Schema(description = "图片高度", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "图片高度不能为空")
    private Integer height;

    @Schema(description = "比例", example = "1:1")
    private String aspectRatio;

    @Schema(description = "图片大小", example = "1K")
    private String imageSize;

    @Schema(description = "返回方式", example = "[\"IMAGE\"]")
    private List<String> responseModalities;

    @Schema(description = "安全设置")
    private List<SafetySetting> safetySettings;

    @Schema(description = "参考图列表", example = "[\"https://www.iocoder.cn/x.png\"]")
    private List<String> referImageUrls;

    @Schema(description = "拓展参数")
    private Map<String, String> options;

    @Data
    @Schema(description = "安全设置")
    public static class SafetySetting {
        @Schema(description = "类别", example = "HATE_SPEECH")
        private String category;
        @Schema(description = "阈值", example = "BLOCK_ONLY_HIGH")
        private String threshold;
    }

}
