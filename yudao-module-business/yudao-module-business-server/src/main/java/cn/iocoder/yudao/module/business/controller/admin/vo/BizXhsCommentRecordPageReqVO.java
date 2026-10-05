package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 小红书评论记录与去重分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BizXhsCommentRecordPageReqVO extends PageParam {

    @Schema(description = "App账号ID")
    private Long appAccountId;

    @Schema(description = "App登录手机号")
    private String appMobile;

    @Schema(description = "评论人小红书账号ID")
    private String xhsUserId;

    @Schema(description = "评论人小红书名称")
    private String xhsUserName;

    @Schema(description = "小红书笔记ID")
    private String noteId;

    @Schema(description = "作品标题关键字")
    private String noteTitle;

    @Schema(description = "分享链接关键字")
    private String shareLink;

    @Schema(description = "评论内容模糊搜索")
    private String commentContent;

    @Schema(description = "评论状态: 0正常, 1被吞/消失, 2被折叠, 3异常")
    private Integer commentStatus;

    @Schema(description = "检测状态: 0未检测, 1已检测, 2检测失败")
    private Integer checkStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
