package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 采集帖子创建/修改 Request VO")
@Data
public class BizScraperPostSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "图片列表")
    private List<Object> images;

    @Schema(description = "视频列表")
    private List<Object> videos;

    @Schema(description = "帖子ID")
    private String postId;

    @Schema(description = "备注")
    private String remark;

}

