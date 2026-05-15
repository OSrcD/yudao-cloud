package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 已评论分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BizPromptCommentCompletePageReqVO extends PageParam {
    
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

