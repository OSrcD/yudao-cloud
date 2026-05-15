package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 媒体账号创建/修改 Request VO")
@Data
public class BizMediaAccountSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "账号类型")
    private String accountType;

    @Schema(description = "登录凭证/Cookie")
    private String accountCookie;

    @Schema(description = "备注")
    private String remark;

}

