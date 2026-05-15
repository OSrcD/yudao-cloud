package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompletePageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentCompleteDO;
import cn.iocoder.yudao.module.business.service.BizPromptCommentCompleteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 已评论")
@RestController
@RequestMapping("/business/prompt-comment-complete")
@Validated
public class BizPromptCommentCompleteController {

    @Resource
    private BizPromptCommentCompleteService promptCommentCompleteService;

    @PostMapping("/create")
    @Operation(summary = "创建已评论")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:create')")
    public CommonResult<Long> createPromptCommentComplete(@Valid @RequestBody BizPromptCommentCompleteSaveReqVO createReqVO) {
        return success(promptCommentCompleteService.createPromptCommentComplete(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新已评论")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:update')")
    public CommonResult<Boolean> updatePromptCommentComplete(@Valid @RequestBody BizPromptCommentCompleteSaveReqVO updateReqVO) {
        promptCommentCompleteService.updatePromptCommentComplete(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除已评论")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:delete')")
    public CommonResult<Boolean> deletePromptCommentComplete(@RequestParam("id") Long id) {
        promptCommentCompleteService.deletePromptCommentComplete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得已评论")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:query')")
    public CommonResult<BizPromptCommentCompleteDO> getPromptCommentComplete(@RequestParam("id") Long id) {
        return success(promptCommentCompleteService.getPromptCommentComplete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得已评论分页")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:query')")
    public CommonResult<PageResult<BizPromptCommentCompleteRespVO>> getPromptCommentCompletePage(@Valid BizPromptCommentCompletePageReqVO pageVO) {
        return success(promptCommentCompleteService.getPromptCommentCompletePage(pageVO));
    }

    @GetMapping("/check-page")
    @Operation(summary = "获得待检查已评论分页")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:query')")
    public CommonResult<PageResult<BizPromptCommentCompleteRespVO>> getCheckPageList(@Valid BizPromptCommentCompletePageReqVO pageVO) {
        return success(promptCommentCompleteService.getCheckPageList(pageVO));
    }

    @PutMapping("/update-check-result")
    @Operation(summary = "更新检测结果")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment-complete:update')")
    public CommonResult<Boolean> updateCheckResult(@Valid @RequestBody BizPromptCommentCompletePageReqVO reqVO) {
        return success(promptCommentCompleteService.updateCheckResult(reqVO));
    }

}

