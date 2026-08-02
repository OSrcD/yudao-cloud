package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 小红书笔记监控状态更新 Request VO")
@Data
public class XhsNoteCollectUpdateMonitorReqVO {

    @NotEmpty(message = "笔记ID列表不能为空")
    private List<Long> ids;

    private Boolean isMonitored;
}
