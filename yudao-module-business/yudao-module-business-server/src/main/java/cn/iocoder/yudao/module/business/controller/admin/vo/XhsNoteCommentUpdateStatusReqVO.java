package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 小红书笔记评论更新私信截流状态 Request VO")
@Data
public class XhsNoteCommentUpdateStatusReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "截流跟进状态：0-未触达，1-已发私信，2-已加微信，3-意向偏低/无效", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "截流状态不能为空")
    private Integer interceptStatus;

    @Schema(description = "跟进备注")
    private String remark;

}
