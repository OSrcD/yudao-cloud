package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 小红书官方号创建/修改 Request VO")
@Data
public class BizXhsOfficialAccountSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "App用户编号 (关联 member_user.id)", example = "1024")
    private Long userId;

    @Schema(description = "App用户手机号码/账号", example = "13800138000")
    private String mobile;

    @Schema(description = "作者小红书号", requiredMode = Schema.RequiredMode.REQUIRED, example = "xhs_official_668")
    @NotEmpty(message = "小红书号不能为空")
    private String accountId;

    @Schema(description = "作者昵称", example = "小红书官方客服")
    private String nickname;

    @Schema(description = "备注说明", example = "平台官方号，不评论")
    private String remark;

    @Schema(description = "状态: 0开启过滤(生效), 1关闭过滤(禁用)", example = "0")
    private Integer status;

}
