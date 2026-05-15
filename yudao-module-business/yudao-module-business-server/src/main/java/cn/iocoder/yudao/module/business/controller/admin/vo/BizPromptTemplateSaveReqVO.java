package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 提示词模板创建/修改 Request VO")
@Data
public class BizPromptTemplateSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "模板内容")
    private String template;

    @Schema(description = "模板类型")
    private Integer templateType;

    @Schema(description = "备注")
    private String remark;

}

