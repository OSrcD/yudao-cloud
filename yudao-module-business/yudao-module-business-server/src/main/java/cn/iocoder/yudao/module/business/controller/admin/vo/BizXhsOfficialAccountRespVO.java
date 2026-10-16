package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 小红书官方号 Response VO")
@Data
public class BizXhsOfficialAccountRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @JsonSerialize(using = ToStringSerializer.class)
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "App用户编号", example = "1024")
    @ExcelProperty("App用户编号")
    private Long userId;

    @Schema(description = "App用户手机号码/账号", example = "13800138000")
    @ExcelProperty("App用户手机")
    private String mobile;

    @Schema(description = "作者小红书号", requiredMode = Schema.RequiredMode.REQUIRED, example = "xhs_official_668")
    @ExcelProperty("作者小红书号")
    private String accountId;

    @Schema(description = "作者昵称", example = "小红书官方客服")
    @ExcelProperty("作者昵称")
    private String nickname;

    @Schema(description = "备注说明", example = "平台官方号，不评论")
    @ExcelProperty("备注说明")
    private String remark;

    @Schema(description = "状态: 0开启过滤(生效), 1关闭过滤(禁用)", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("状态(0生效 1禁用)")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
