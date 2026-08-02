package cn.iocoder.yudao.module.business.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectSaveReqVO;
import cn.iocoder.yudao.module.business.service.XhsNoteCollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import jakarta.annotation.security.PermitAll;

@Tag(name = "用户 APP - 小红书笔记采集")
@RestController
@RequestMapping("/business/app/xhs-note-collect")
@Validated
@PermitAll
public class XhsNoteCollectAppController {

    @Resource
    private XhsNoteCollectService xhsNoteCollectService;

    @PostMapping("/save")
    @Operation(summary = "App 端单条采集笔记保存/更新 (去重)")
    public CommonResult<Boolean> saveNote(@RequestBody XhsNoteCollectSaveReqVO reqVO) {
        return success(xhsNoteCollectService.saveOrUpdateXhsNote(reqVO));
    }

    @PostMapping("/save-batch")
    @Operation(summary = "App 端批量采集笔记保存/更新 (去重)")
    public CommonResult<Integer> saveNoteBatch(@RequestBody List<XhsNoteCollectSaveReqVO> reqVOList) {
        return success(xhsNoteCollectService.saveOrUpdateXhsNoteBatch(reqVOList));
    }

    @GetMapping("/page")
    @Operation(summary = "App 端获取已采集笔记列表")
    public CommonResult<cn.iocoder.yudao.framework.common.pojo.PageResult<cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO>> getNotePage(cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectPageReqVO pageReqVO) {
        return success(xhsNoteCollectService.getXhsNoteCollectPage(pageReqVO));
    }

    @PostMapping("/update-comment-status")
    @Operation(summary = "App 端更新评论采集状态")
    public CommonResult<Boolean> updateCommentStatus(@RequestBody cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectUpdateCommentStatusReqVO reqVO) {
        xhsNoteCollectService.updateCommentStatus(reqVO.getNoteId());
        return success(true);
    }

    @PostMapping("/update-link-invalid")
    @Operation(summary = "App 端更新链接失效状态")
    public CommonResult<Boolean> updateLinkInvalid(@RequestBody cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectUpdateCommentStatusReqVO reqVO) {
        xhsNoteCollectService.updateLinkInvalid(reqVO.getNoteId());
        return success(true);
    }

}
