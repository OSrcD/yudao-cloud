package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "App端 - 小红书作品评论查重 Request VO")
@Data
public class BizXhsCommentCheckDuplicateReqVO {

    @Schema(description = "App端账号ID/用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "App账号ID不能为空")
    private Long appAccountId;

    @Schema(description = "作品分享链接(纯URL)", example = "https://xhslink.cn/o/xyz")
    private String shareLink;

    @Schema(description = "小红书作品标题", example = "频繁被邀请合作是不是一件好事")
    private String noteTitle;

    @Schema(description = "小红书笔记ID(可选)", example = "64f9b2d3000000001201ab2c")
    private String noteId;

    @Schema(description = "查重对比模式: 0仅对比链接(默认), 1仅对比标题, 2标题和链接同时对比", example = "0")
    private Integer dedupMode;

}
