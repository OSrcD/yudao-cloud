package cn.iocoder.yudao.module.business.controller.admin.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 小红书评论记录 Response VO")
@Data
public class BizXhsCommentRecordRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "App账号ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appAccountId;

    @Schema(description = "App登录手机号", example = "13800138000")
    private String appMobile;

    @Schema(description = "评论人小红书账号ID", example = "xhs_123456")
    private String xhsUserId;

    @Schema(description = "评论人小红书名称", example = "红薯获客小助手")
    private String xhsUserName;

    @Schema(description = "小红书作品分享链接(纯URL)", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xhslink.cn/o/xyz")
    private String shareLink;

    @Schema(description = "完整分享文本内容", example = "频繁被邀请合作... https://xhslink.cn/o/xyz 来【小红书】围观")
    private String shareContent;

    @Schema(description = "提取的小红书笔记ID", example = "64f9b2d3000000001201ab2c")
    private String noteId;

    @Schema(description = "小红书作品标题", example = "频繁被邀请合作是不是一件好事")
    private String noteTitle;

    @Schema(description = "发表的评论内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "干货满满，学到了！")
    private String commentContent;

    @Schema(description = "评论状态: 0正常, 1被吞, 2折叠, 3异常", example = "0")
    private Integer commentStatus;

    @Schema(description = "检测状态: 0未检测, 1已检测, 2检测失败", example = "0")
    private Integer checkStatus;

    @Schema(description = "最近检测时间")
    private LocalDateTime checkTime;

    @Schema(description = "备注说明", example = "自动执行评论")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
