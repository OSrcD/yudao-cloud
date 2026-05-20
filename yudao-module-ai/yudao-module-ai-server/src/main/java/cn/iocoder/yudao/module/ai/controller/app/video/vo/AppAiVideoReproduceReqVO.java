package cn.iocoder.yudao.module.ai.controller.app.video.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "用户 APP - AI 视频复刻创建任务 Request VO")
@Data
public class AppAiVideoReproduceReqVO {

    @Schema(description = "原视频地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "原视频地址不能为空")
    private String videoUrl;

    @Schema(description = "商品配置JSON字符串")
    private String productConfigJson;

    @Schema(description = "执行模式 (local / api)")
    private String execMode;

    @Schema(description = "角色参考图")
    private List<String> charImageUrls;

    @Schema(description = "产品参考图")
    private List<String> productImageUrls;

}

