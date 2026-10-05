package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 / App端 - 小红书评论记录保存/更新 Request VO")
@Data
public class BizXhsCommentRecordSaveReqVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "当前App登录账号ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "App账号ID不能为空")
    private Long appAccountId;

    @Schema(description = "当前App登录手机号", example = "13800138000")
    private String appMobile;

    @Schema(description = "评论人小红书账号ID", example = "xhs_123456")
    private String xhsUserId;

    @Schema(description = "评论人小红书名称/昵称", example = "红薯获客小助手")
    private String xhsUserName;

    @Schema(description = "小红书作品分享链接(纯URL)", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xhslink.cn/o/xyz")
    @NotBlank(message = "分享链接不能为空")
    private String shareLink;

    @Schema(description = "完整分享文本内容", example = "频繁被邀请合作... https://xhslink.cn/o/xyz 来【小红书】围观")
    private String shareContent;

    @Schema(description = "提取的小红书笔记ID", example = "64f9b2d3000000001201ab2c")
    private String noteId;

    @Schema(description = "小红书作品标题", example = "频繁被邀请合作是不是一件好事")
    private String noteTitle;

    @Schema(description = "发表的评论内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "干货满满，学到了！")
    @NotBlank(message = "评论内容不能为空")
    private String commentContent;

    @Schema(description = "评论状态: 0正常, 1被吞, 2折叠, 3异常", example = "0")
    private Integer commentStatus;

    @Schema(description = "检测状态: 0未检测, 1已检测, 2检测失败", example = "0")
    private Integer checkStatus;

    @Schema(description = "最近检测时间")
    private LocalDateTime checkTime;

    @Schema(description = "备注说明", example = "自动执行评论")
    private String remark;

}
