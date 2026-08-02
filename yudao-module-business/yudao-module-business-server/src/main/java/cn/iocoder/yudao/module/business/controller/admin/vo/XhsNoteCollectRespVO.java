package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 小红书笔记采集 Response VO")
@Data
public class XhsNoteCollectRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "笔记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6a09f6bc000000003502fd3d")
    private String noteId;

    @Schema(description = "搜索关键词", example = "小程序")
    private String keyword;

    @Schema(description = "笔记类型", example = "视频")
    private String noteType;

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "笔记链接")
    private String noteUrl;

    @Schema(description = "Shell跳转命令")
    private String shellCmd;

    @Schema(description = "笔记时间")
    private String publishTime;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "小红书号")
    private String redId;

    @Schema(description = "点赞数")
    private Integer likedCount;

    @Schema(description = "收藏数")
    private Integer collectedCount;

    @Schema(description = "评论数")
    private Integer commentsCount;

    @Schema(description = "完整JSON")
    private String rawJson;

    @Schema(description = "PC分享链接")
    private String pcShareLink;

    @Schema(description = "链接是否已失效")
    private Boolean isLinkInvalid;

    @Schema(description = "链接失效判定时间")
    private LocalDateTime linkInvalidTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后采集PC分享链接时间")
    private LocalDateTime lastPcShareCollectTime;

    @Schema(description = "最后采集评论时间")
    private LocalDateTime lastCommentCollectTime;

}
