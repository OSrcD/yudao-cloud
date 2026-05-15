package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 本地任务创建/修改 Request VO")
@Data
public class BizLocalTaskSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "关联任务ID")
    private Long refTaskId;

    @Schema(description = "执行参数")
    private String execParams;

    @Schema(description = "执行结果")
    private String execResult;

    @Schema(description = "任务状态")
    private Integer status;

}

