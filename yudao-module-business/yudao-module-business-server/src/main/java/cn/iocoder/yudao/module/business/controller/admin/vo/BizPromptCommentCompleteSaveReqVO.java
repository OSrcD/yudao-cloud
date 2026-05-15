package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 已评论创建/修改 Request VO")
@Data
public class BizPromptCommentCompleteSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "自媒体账号ID")
    private Long mediaAccountId;

    @Schema(description = "小红书笔记信息")
    private String xhsNoteInfo;

    @Schema(description = "检测状态")
    private Integer checkStatus;

    @Schema(description = "评论状态")
    private Integer commentStatus;

}
