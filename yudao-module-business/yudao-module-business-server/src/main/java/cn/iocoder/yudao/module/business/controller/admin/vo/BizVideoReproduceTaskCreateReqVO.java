package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "管理后台 - 视频搬运任务创建 Request VO")
@Data
public class BizVideoReproduceTaskCreateReqVO {

    @Schema(description = "视频文件")
    private MultipartFile videoFile;

    @Schema(description = "商品配置JSON")
    private String productConfigJson;

    @Schema(description = "人物参考图")
    private MultipartFile[] charImages;

    @Schema(description = "商品参考图")
    private MultipartFile[] productImages;

    @Schema(description = "执行模式")
    private String execMode;

    @Schema(description = "用户编号")
    private Long userId;

    @Schema(description = "用户类型")
    private Integer userType;

}
