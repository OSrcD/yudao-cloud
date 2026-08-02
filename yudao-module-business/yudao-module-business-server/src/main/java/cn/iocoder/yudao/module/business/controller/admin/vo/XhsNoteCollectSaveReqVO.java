package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 小红书笔记采集保存/修改 Request VO")
@Data
public class XhsNoteCollectSaveReqVO {

    @Schema(description = "主键ID", example = "1024")
    private Long id;

    @Schema(description = "笔记ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6a09f6bc000000003502fd3d")
    private String noteId;

    @Schema(description = "搜索关键词", example = "小程序")
    private String keyword;

    @Schema(description = "笔记类型：1-图文，2-视频", example = "1")
    private Integer noteType;

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "笔记描述")
    private String desc;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
