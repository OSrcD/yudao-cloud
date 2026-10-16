package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 小红书官方号分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BizXhsOfficialAccountPageReqVO extends PageParam {

    @Schema(description = "App用户编号", example = "1024")
    private Long userId;

    @Schema(description = "App用户手机号码/账号", example = "13800138000")
    private String mobile;

    @Schema(description = "作者小红书号")
    private String accountId;

    @Schema(description = "作者昵称")
    private String nickname;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "状态: 0开启过滤(生效), 1关闭过滤(禁用)")
    private Integer status;

}
