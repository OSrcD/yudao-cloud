package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskCompleteReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizLocalTaskDO;
import cn.iocoder.yudao.module.business.service.BizLocalTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 本地任务")
@RestController
@RequestMapping("/business/local-task")
@Validated
public class BizLocalTaskController {

    @Resource
    private BizLocalTaskService localTaskService;

    @PostMapping("/create")
    @Operation(summary = "创建本地任务")
    @PreAuthorize("@ss.hasPermission('business:local-task:create')")
    public CommonResult<Long> createLocalTask(@Valid @RequestBody BizLocalTaskSaveReqVO createReqVO) {
        return success(localTaskService.createLocalTask(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新本地任务")
    @PreAuthorize("@ss.hasPermission('business:local-task:update')")
    public CommonResult<Boolean> updateLocalTask(@Valid @RequestBody BizLocalTaskSaveReqVO updateReqVO) {
        localTaskService.updateLocalTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除本地任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:local-task:delete')")
    public CommonResult<Boolean> deleteLocalTask(@RequestParam("id") Long id) {
        localTaskService.deleteLocalTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得本地任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:local-task:query')")
    public CommonResult<BizLocalTaskDO> getLocalTask(@RequestParam("id") Long id) {
        return success(localTaskService.getLocalTask(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得本地任务分页")
    @PreAuthorize("@ss.hasPermission('business:local-task:query')")
    public CommonResult<PageResult<BizLocalTaskDO>> getLocalTaskPage(@Valid BizLocalTaskPageReqVO pageVO) {
        return success(localTaskService.getLocalTaskPage(pageVO));
    }

    @PostMapping("/poll")
    @Operation(summary = "拉取一个本地任务")
    @PermitAll
    public CommonResult<BizLocalTaskDO> pollTask() {
        return success(localTaskService.pollTask());
    }

    @PostMapping("/complete")
    @Operation(summary = "完成本地任务回调")
    @PermitAll
    public CommonResult<Boolean> completeTask(@Valid @RequestBody BizLocalTaskCompleteReqVO reqVO) {
        localTaskService.completeTask(reqVO.getTaskId(), reqVO.getSuccess(), reqVO.getResultData(), reqVO.getErrorMsg());
        return success(true);
    }

    @PostMapping("/partial-complete")
    @Operation(summary = "本地任务部分完成回调")
    @PermitAll
    public CommonResult<Boolean> partialCompleteTask(@Valid @RequestBody BizLocalTaskCompleteReqVO reqVO) {
        localTaskService.partialCompleteTask(reqVO.getTaskId(), reqVO.getResultData());
        return success(true);
    }

}
