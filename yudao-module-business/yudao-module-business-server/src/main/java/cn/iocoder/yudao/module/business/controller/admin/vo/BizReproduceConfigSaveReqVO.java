package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 搬运配置创建/修改 Request VO")
@Data
public class BizReproduceConfigSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置详情JSON")
    private Map<String, Object> productConfigJson;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "人物图片列表")
    private List<String> charImages;

    @Schema(description = "产品图片列表")
    private List<String> productImages;

}

