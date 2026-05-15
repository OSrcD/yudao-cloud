package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 查询未使用评论 Request VO")
@Data
public class QueryUnusedCommentReqVO {

    @Schema(description = "自媒体账号ID", required = true)
    private Long mediaAccountId;

    @Schema(description = "平台", required = true)
    private Integer platform;

}

