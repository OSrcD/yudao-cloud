package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 提示词评论分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BizPromptCommentPageReqVO extends PageParam {

    @Schema(description = "提示词ID")
    private Long promptId;

    @Schema(description = "操作组ID")
    private Long operateGroupId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "评论内容")
    private String commentContent;

}

