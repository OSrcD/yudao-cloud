package cn.iocoder.yudao.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "用户 App - 视频复刻任务详情 Response VO")
@Data
public class AppAiVideoReproduceTaskDetailRespVO {

    @Schema(description = "任务 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "原视频链接")
    private String originalVideoUrl;

    @Schema(description = "配置 JSON")
    private Map<String, Object> productConfigJson;

    @Schema(description = "最终解析结果 JSON")
    private String resultJson;

    @Schema(description = "任务状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "合成后视频链接")
    private String combinedVideoUrl;

    @Schema(description = "累计消耗积分")
    private Integer totalPointUsed;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "分镜帧列表")
    private List<AppAiVideoReproduceFrameRespVO> frames;

    @Data
    public static class AppAiVideoReproduceFrameRespVO {
        @Schema(description = "帧 ID")
        private Long id;

        @Schema(description = "关联的 AI 图片 ID (洗图产物)")
        private Long aiImageId;

        @Schema(description = "关联的 AI 视频 ID (生成的视频)")
        private Long aiVideoId;

        @Schema(description = "原始截帧图 url")
        private String originalImageUrl;

        @Schema(description = "原始提示词")
        private String originalPrompt;

        @Schema(description = "改写后的提示词")
        private String rewrittenPrompt;

        @Schema(description = "锁定的元素 JSON")
        private Map<String, Object> lockedElements;

        @Schema(description = "当前处理步骤状态")
        private String stepStatus;

        @Schema(description = "生图参数 JSON")
        private Map<String, Object> imageParams;

        @Schema(description = "生视频参数 JSON")
        private Map<String, Object> videoParams;

        @Schema(description = "最终产出的图片/视频 URL")
        private String outputUrl;

        @Schema(description = "宫格图英文提示词")
        private String gridImagePromptEn;

        @Schema(description = "宫格图中文提示词")
        private String gridImagePromptZh;

        @Schema(description = "关联的引用商品原图列表")
        private List<String> gridSourceImages;

        @Schema(description = "视频生图英文提示词")
        private String imagePromptForModelEn;

        @Schema(description = "视频生图中文提示词")
        private String imagePromptZhCheck;

        @Schema(description = "生视频英文提示词（实际用于生成视频）")
        private String i2vPromptEn;

        @Schema(description = "生视频中文提示词（仅供用户核对展示）")
        private String i2vPromptZh;

        @Schema(description = "分镜单元标识符")
        private String guId;

        @Schema(description = "洗图后的宫格图 URL")
        private String polishedImageUrl;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}

