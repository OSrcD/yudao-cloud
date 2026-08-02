package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "评论监控推送配置 VO")
@Data
public class XhsCommentMonitorNotifyConfigVO {

    @Schema(description = "是否开启推送")
    private Boolean enabled;

    @Schema(description = "推送类型：webhook / wecom / feishu")
    private String type;

    @Schema(description = "Webhook 地址")
    private String url;

    @Schema(description = "签名密钥（预留）")
    private String secret;

}
