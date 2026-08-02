package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 评论监控分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XhsCommentMonitorPageReqVO extends PageParam {

    @Schema(description = "临时搜索关键词（匹配评论内容或昵称）")
    private String keyword;

    @Schema(description = "评论者昵称")
    private String nickname;

    @Schema(description = "笔记ID")
    private String noteId;

    @Schema(description = "排序字段，如 commentTime:desc")
    private String sortFields;

}
