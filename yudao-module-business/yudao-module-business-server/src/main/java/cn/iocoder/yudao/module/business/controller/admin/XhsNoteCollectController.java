package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectUpdateMonitorReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;
import cn.iocoder.yudao.module.business.service.XhsNoteCollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 小红书笔记采集")
@RestController
@RequestMapping("/business/xhs-note-collect")
@Validated
public class XhsNoteCollectController {

    @Resource
    private XhsNoteCollectService xhsNoteCollectService;

    @PostMapping("/save")
    @Operation(summary = "单条采集笔记保存/更新 (去重)")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:create')")
    public CommonResult<Boolean> saveNote(@RequestBody XhsNoteCollectSaveReqVO reqVO) {
        return success(xhsNoteCollectService.saveOrUpdateXhsNote(reqVO));
    }

    @PostMapping("/save-batch")
    @Operation(summary = "批量采集笔记保存/更新 (去重)")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:create')")
    public CommonResult<Integer> saveNoteBatch(@RequestBody List<XhsNoteCollectSaveReqVO> reqVOList) {
        return success(xhsNoteCollectService.saveOrUpdateXhsNoteBatch(reqVOList));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采集笔记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:delete')")
    public CommonResult<Boolean> deleteNote(@RequestParam("id") Long id) {
        xhsNoteCollectService.deleteXhsNoteCollect(id);
        return success(true);
    }

    @DeleteMapping("/delete-batch")
    @Operation(summary = "批量删除采集笔记")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:delete')")
    public CommonResult<Boolean> deleteNoteBatch(@RequestParam("ids") List<Long> ids) {
        xhsNoteCollectService.deleteXhsNoteCollectBatch(ids);
        return success(true);
    }

    @PostMapping("/update-all-valid")
    @Operation(summary = "将全部笔记改为有效链接")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:update')")
    public CommonResult<Boolean> updateAllNotesValid() {
        xhsNoteCollectService.updateAllNotesValid();
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得采集笔记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:query')")
    public CommonResult<XhsNoteCollectDO> getNote(@RequestParam("id") Long id) {
        return success(xhsNoteCollectService.getXhsNoteCollect(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得采集笔记分页")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:query')")
    public CommonResult<PageResult<XhsNoteCollectDO>> getNotePage(@Valid XhsNoteCollectPageReqVO pageVO) {
        return success(xhsNoteCollectService.getXhsNoteCollectPage(pageVO));
    }

    @PostMapping("/update-monitor")
    @Operation(summary = "更新笔记监控状态（批量）")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:update')")
    public CommonResult<Boolean> updateMonitorStatus(@RequestBody @Valid XhsNoteCollectUpdateMonitorReqVO reqVO) {
        xhsNoteCollectService.updateMonitorStatus(reqVO.getIds(), reqVO.getIsMonitored());
        return success(true);
    }

    @GetMapping("/monitor-page")
    @Operation(summary = "获得监控笔记分页")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-collect:query')")
    public CommonResult<PageResult<XhsNoteCollectDO>> getMonitorPage(@Valid XhsNoteCollectPageReqVO pageVO) {
        return success(xhsNoteCollectService.getMonitoredPage(pageVO));
    }

}
