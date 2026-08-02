package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 小红书笔记评论分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XhsNoteCommentPageReqVO extends PageParam {

    @Schema(description = "笔记ID")
    private String noteId;

    @Schema(description = "评论ID")
    private String commentId;

    @Schema(description = "评论用户ID")
    private String userId;

    @Schema(description = "评论用户昵称")
    private String nickname;

    @Schema(description = "小红书号")
    private String redId;

    @Schema(description = "评论关键字模糊查询")
    private String content;

    @Schema(description = "IP属地")
    private String ipLocation;

    @Schema(description = "截流跟进状态：0-未触达，1-已发私信，2-已加微信，3-意向偏低/无效")
    private Integer interceptStatus;

    @Schema(description = "是否作者：0-否，1-是")
    private Integer isAuthor;

    @Schema(description = "组合排序字段，例如：commentTime:desc")
    private String sortFields;

}
