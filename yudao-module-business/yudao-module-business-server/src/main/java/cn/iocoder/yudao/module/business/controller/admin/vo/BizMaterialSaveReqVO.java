package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 素材创建/修改 Request VO")
@Data
public class BizMaterialSaveReqVO {

    @Schema(description = "素材ID")
    private Long id;

    @Schema(description = "素材名称")
    private String materialName;

    @Schema(description = "素材地址")
    private String materialUrl;

    @Schema(description = "文件类型（0图片 1视频）")
    private String fileType;

    @Schema(description = "备注")
    private String remark;

}

