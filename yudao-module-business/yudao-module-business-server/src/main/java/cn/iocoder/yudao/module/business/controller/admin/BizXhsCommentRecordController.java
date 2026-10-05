package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentAnalysisRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsCommentRecordDO;
import cn.iocoder.yudao.module.business.service.BizXhsCommentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 小红书评论记录与去重分析")
@RestController
@RequestMapping("/business/xhs-comment-record")
@Validated
public class BizXhsCommentRecordController {

    @Resource
    private BizXhsCommentRecordService xhsCommentRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建小红书评论记录")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-record:create')")
    public CommonResult<Long> createCommentRecord(@Valid @RequestBody BizXhsCommentRecordSaveReqVO createReqVO) {
        return success(xhsCommentRecordService.createCommentRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新小红书评论记录")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-record:update')")
    public CommonResult<Boolean> updateCommentRecord(@Valid @RequestBody BizXhsCommentRecordSaveReqVO updateReqVO) {
        xhsCommentRecordService.updateCommentRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除小红书评论记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-record:delete')")
    public CommonResult<Boolean> deleteCommentRecord(@RequestParam("id") Long id) {
        xhsCommentRecordService.deleteCommentRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得小红书评论记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-record:query')")
    public CommonResult<BizXhsCommentRecordDO> getCommentRecord(@RequestParam("id") Long id) {
        return success(xhsCommentRecordService.getCommentRecord(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得小红书评论记录分页")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-record:query')")
    public CommonResult<PageResult<BizXhsCommentRecordRespVO>> getCommentRecordPage(@Valid BizXhsCommentRecordPageReqVO pageReqVO) {
        return success(xhsCommentRecordService.getCommentRecordPage(pageReqVO));
    }

    @GetMapping("/analysis-preview")
    @Operation(summary = "获得评论数据分析统计预览 (吞评/折叠/正常率统计)")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-record:query')")
    public CommonResult<BizXhsCommentAnalysisRespVO> getCommentAnalysisPreview() {
        return success(xhsCommentRecordService.getCommentAnalysisPreview());
    }

}
