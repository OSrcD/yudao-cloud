package cn.iocoder.yudao.module.ai.controller.admin.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - AI 视频 Response VO")
@Data
public class BizAiVideoRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "猫咪")
    private String prompt;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "GeekAI")
    private String platform;
    @Schema(description = "模型编号", example = "1")
    private Long modelId;
    @Schema(description = "模型", requiredMode = Schema.RequiredMode.REQUIRED, example = "veo_3_1-fast")
    private String model;

    @Schema(description = "宽度", requiredMode = Schema.RequiredMode.REQUIRED, example = "720")
    private Integer width;
    @Schema(description = "高度", requiredMode = Schema.RequiredMode.REQUIRED, example = "1280")
    private Integer height;
    @Schema(description = "时长（秒）", example = "8")
    private Integer duration;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
    @Schema(description = "完成时间")
    private LocalDateTime finishTime;
    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "视频地址")
    private String videoUrl;
    @Schema(description = "预览图地址")
    private String previewUrl;

    @Schema(description = "配置选项")
    private Map<String, Object> options;

    @Schema(description = "任务编号")
    private String taskId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
