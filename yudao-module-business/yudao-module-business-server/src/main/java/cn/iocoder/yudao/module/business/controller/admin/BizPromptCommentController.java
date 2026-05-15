package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.QueryUnusedCommentReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;
import cn.iocoder.yudao.module.business.service.BizPromptCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 提示词评论")
@RestController
@RequestMapping("/business/prompt-comment")
@Validated
public class BizPromptCommentController {

    @Resource
    private BizPromptCommentService promptCommentService;

    @PostMapping("/create")
    @Operation(summary = "创建提示词评论")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment:create')")
    public CommonResult<Long> createPromptComment(@Valid @RequestBody BizPromptCommentSaveReqVO createReqVO) {
        return success(promptCommentService.createPromptComment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新提示词评论")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment:update')")
    public CommonResult<Boolean> updatePromptComment(@Valid @RequestBody BizPromptCommentSaveReqVO updateReqVO) {
        promptCommentService.updatePromptComment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除提示词评论")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:prompt-comment:delete')")
    public CommonResult<Boolean> deletePromptComment(@RequestParam("id") Long id) {
        promptCommentService.deletePromptComment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得提示词评论")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment:query')")
    public CommonResult<BizPromptCommentDO> getPromptComment(@RequestParam("id") Long id) {
        return success(promptCommentService.getPromptComment(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得提示词评论分页")
    @PreAuthorize("@ss.hasPermission('business:prompt-comment:query')")
    public CommonResult<PageResult<BizPromptCommentDO>> getPromptCommentPage(@Valid BizPromptCommentPageReqVO pageVO) {
        return success(promptCommentService.getPromptCommentPage(pageVO));
    }

    @PostMapping("/unused-list")
    @Operation(summary = "查询未使用的提示词评论列表")
    public CommonResult<List<BizPromptCommentDO>> queryUnusedList(@Valid @RequestBody QueryUnusedCommentReqVO reqVO) {
        return success(promptCommentService.queryUnusedList(reqVO.getMediaAccountId(), reqVO.getPlatform()));
    }

    @PostMapping("/unused-list-by-group")
    @Operation(summary = "获取下一组可用的提示词评论")
    public CommonResult<List<BizPromptCommentDO>> queryUnusedListByGroup(@Valid @RequestBody QueryUnusedCommentReqVO reqVO) {
        return success(promptCommentService.getNextAvailableGroup(reqVO.getMediaAccountId(), reqVO.getPlatform()));
    }

}

