package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 小红书笔记采集分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XhsNoteCollectPageReqVO extends PageParam {

    @Schema(description = "笔记ID", example = "6a09f6bc000000003502fd3d")
    private String noteId;

    @Schema(description = "搜索关键词", example = "小程序开发")
    private String keyword;

    @Schema(description = "笔记类型：1-图文，2-视频", example = "1")
    private Integer noteType;

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "小红书号")
    private String redId;

    @Schema(description = "笔记时间升序(true)/降序(false)", example = "false")
    private Boolean publishTimeAsc;

    @Schema(description = "排序字段", example = "likedCount")
    private String sortField;

    @Schema(description = "排序规则：asc升序，desc降序", example = "desc")
    private String sortOrder;

    @Schema(description = "多列组合排序，逗号分隔，如: publishTime:desc,likedCount:desc", example = "publishTime:desc,likedCount:desc")
    private String sortFields;

    @Schema(description = "是否未采集分享链接")
    private Boolean pcShareLinkEmpty;

    @Schema(description = "PC分享链接是否有效(包含😆)")
    private Boolean pcShareLinkValid;

    @Schema(description = "是否已采集评论", example = "true")
    private Boolean isCommentCollected;

    @Schema(description = "链接是否已失效", example = "false")
    private Boolean isLinkInvalid;
    private Boolean isMonitored;

    @Schema(description = "最小评论数")
    private Integer commentsCountMin;

    @Schema(description = "是否作为评论采集任务查询")
    private Boolean forCommentScrape;

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortFields() {
        return sortFields;
    }

    public void setSortFields(String sortFields) {
        this.sortFields = sortFields;
    }

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
