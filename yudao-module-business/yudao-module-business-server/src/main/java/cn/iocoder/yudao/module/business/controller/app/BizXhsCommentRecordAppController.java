package cn.iocoder.yudao.module.business.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentCheckDuplicateReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordSaveReqVO;
import cn.iocoder.yudao.module.business.service.BizXhsCommentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 小红书评论记录与查重")
@RestController
@RequestMapping("/business/app/xhs-comment-record")
@Validated
@PermitAll
public class BizXhsCommentRecordAppController {

    @Resource
    private BizXhsCommentRecordService xhsCommentRecordService;

    @PostMapping("/check-duplicate")
    @Operation(summary = "App端作品评论查重 (POST)")
    public CommonResult<Boolean> checkDuplicatePost(@Valid @RequestBody BizXhsCommentCheckDuplicateReqVO reqVO) {
        return success(xhsCommentRecordService.checkDuplicate(
                reqVO.getAppAccountId(), reqVO.getShareLink(), reqVO.getNoteId(), reqVO.getNoteTitle(), reqVO.getDedupMode()));
    }

    @GetMapping("/check-duplicate")
    @Operation(summary = "App端作品评论查重 (GET)")
    public CommonResult<Boolean> checkDuplicateGet(
            @Parameter(name = "appAccountId", description = "App端账号ID", required = true)
            @RequestParam("appAccountId") Long appAccountId,
            @Parameter(name = "shareLink", description = "作品分享链接(可选)")
            @RequestParam(value = "shareLink", required = false) String shareLink,
            @Parameter(name = "noteId", description = "小红书笔记ID(可选)")
            @RequestParam(value = "noteId", required = false) String noteId,
            @Parameter(name = "noteTitle", description = "小红书作品标题(可选)")
            @RequestParam(value = "noteTitle", required = false) String noteTitle,
            @Parameter(name = "dedupMode", description = "查重模式: 0仅对比链接, 1仅对比标题, 2同时对比")
            @RequestParam(value = "dedupMode", required = false, defaultValue = "0") Integer dedupMode) {
        return success(xhsCommentRecordService.checkDuplicate(appAccountId, shareLink, noteId, noteTitle, dedupMode));
    }

    @PostMapping("/create")
    @Operation(summary = "App端评论成功入库")
    public CommonResult<Long> createCommentRecord(@Valid @RequestBody BizXhsCommentRecordSaveReqVO createReqVO) {
        return success(xhsCommentRecordService.createCommentRecord(createReqVO));
    }

}
