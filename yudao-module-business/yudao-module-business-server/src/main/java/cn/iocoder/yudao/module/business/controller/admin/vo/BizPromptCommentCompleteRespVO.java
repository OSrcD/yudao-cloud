package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 已评论 Response VO")
@Data
public class BizPromptCommentCompleteRespVO {

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

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // 扩展字段
    @Schema(description = "提示词评论内容")
    private String commentContent;

    @Schema(description = "小红书非吞评次数")
    private Integer xhsNormalCount;

    @Schema(description = "小红书吞评次数")
    private Integer xhsInterceptCount;

    @Schema(description = "账号名称")
    private String accountName;

    @Schema(description = "笔记链接")
    private String noteUrl;

}

