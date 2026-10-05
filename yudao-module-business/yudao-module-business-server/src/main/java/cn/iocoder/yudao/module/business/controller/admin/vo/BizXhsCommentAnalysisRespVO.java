package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 小红书评论数据分析统计预览 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizXhsCommentAnalysisRespVO {

    @Schema(description = "总评论数", example = "100")
    private Long totalCount;

    @Schema(description = "未知/待检测数", example = "50")
    private Long unknownCount;

    @Schema(description = "正常评论数", example = "85")
    private Long normalCount;

    @Schema(description = "吞评/消失数", example = "10")
    private Long swallowedCount;

    @Schema(description = "折叠评论数", example = "5")
    private Long foldedCount;

    @Schema(description = "待检测/未检测数", example = "0")
    private Long pendingCheckCount;

    @Schema(description = "正常率 (百分比)", example = "85.00")
    private BigDecimal normalRate;

    @Schema(description = "吞评率 (百分比)", example = "10.00")
    private BigDecimal swallowedRate;

    @Schema(description = "折叠率 (百分比)", example = "5.00")
    private BigDecimal foldedRate;

}
