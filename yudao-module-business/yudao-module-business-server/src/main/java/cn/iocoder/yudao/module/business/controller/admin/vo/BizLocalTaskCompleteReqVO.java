package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 本地任务完成 Request VO")
@Data
public class BizLocalTaskCompleteReqVO {

    @Schema(description = "本地任务编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long taskId;

    @Schema(description = "是否成功", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean success;

    @Schema(description = "结果数据")
    private String resultData;

    @Schema(description = "错误信息")
    private String errorMsg;

}
