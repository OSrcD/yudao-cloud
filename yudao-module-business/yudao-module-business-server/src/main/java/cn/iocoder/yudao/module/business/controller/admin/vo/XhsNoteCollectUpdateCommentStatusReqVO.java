package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 更新评论采集状态 Request VO")
@Data
public class XhsNoteCollectUpdateCommentStatusReqVO {

    @Schema(description = "笔记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6a09f6bc000000003502fd3d")
    @NotNull(message = "笔记ID不能为空")
    private String noteId;

}
