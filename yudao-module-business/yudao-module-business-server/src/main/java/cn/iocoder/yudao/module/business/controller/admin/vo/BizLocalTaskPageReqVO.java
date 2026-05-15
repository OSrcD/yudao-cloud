package cn.iocoder.yudao.module.business.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 本地任务分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BizLocalTaskPageReqVO extends PageParam {

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "任务状态")
    private Integer status;

}

