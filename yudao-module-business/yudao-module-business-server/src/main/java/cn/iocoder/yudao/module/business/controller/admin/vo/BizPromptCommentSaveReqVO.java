package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 提示词评论创建/修改 Request VO")
@Data
public class BizPromptCommentSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "提示词ID")
    private Long promptId;

    @Schema(description = "操作组ID")
    private Long operateGroupId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "评论内容")
    private String commentContent;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "版本")
    private Integer version;

    @Schema(description = "小红书拦截数")
    private Integer xhsInterceptCount;

    @Schema(description = "小红书正常数")
    private Integer xhsNormalCount;

}

