package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 小红书笔记评论保存 Request VO")
@Data
public class XhsNoteCommentSaveReqVO {

    @Schema(description = "主键ID", example = "1024")
    private Long id;

    @Schema(description = "笔记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6a6460a40000000010025efb")
    private String noteId;

    @Schema(description = "评论ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6a6462b0000000002b001527")
    private String commentId;

    @Schema(description = "父级评论ID")
    private String parentCommentId;

    @Schema(description = "回复的目标评论ID")
    private String targetCommentId;

    @Schema(description = "评论用户ID")
    private String userId;

    @Schema(description = "评论用户昵称")
    private String nickname;

    @Schema(description = "小红书号")
    private String redId;

    @Schema(description = "用户头像地址")
    private String avatar;

    @Schema(description = "评论正文内容")
    private String content;

    @Schema(description = "IP属地")
    private String ipLocation;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "子评论数")
    private Integer subCommentCount;

    @Schema(description = "是否笔记作者：0-否，1-是")
    private Integer isAuthor;

    @Schema(description = "评论配图链接(JSON数组)")
    private String pictures;

    @Schema(description = "评论时间")
    private String commentTime;

    @Schema(description = "截流跟进状态：0-未触达，1-已发私信，2-已加微信，3-意向偏低/无效")
    private Integer interceptStatus;

    @Schema(description = "跟进备注")
    private String remark;

    @Schema(description = "原始评论JSON")
    private String rawJson;

}
