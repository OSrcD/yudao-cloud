package cn.iocoder.yudao.module.business.controller.app;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.service.XhsNoteCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 小红书笔记评论采集")
@RestController
@RequestMapping("/business/app/xhs-note-comment")
@Validated
@PermitAll
public class XhsNoteCommentAppController {

    @Resource
    private XhsNoteCommentService xhsNoteCommentService;

    @PostMapping("/save")
    @Operation(summary = "App 端单条采集评论保存/更新 (去重)")
    public CommonResult<Boolean> saveComment(@RequestBody XhsNoteCommentSaveReqVO reqVO) {
        return success(xhsNoteCommentService.saveOrUpdateXhsNoteComment(reqVO));
    }

    @PostMapping("/save-batch")
    @Operation(summary = "App 端批量采集评论保存/更新 (去重)")
    public CommonResult<Integer> saveCommentBatch(@RequestBody List<XhsNoteCommentSaveReqVO> reqVOList) {
        return success(xhsNoteCommentService.saveOrUpdateXhsNoteCommentBatch(reqVOList));
    }

    @GetMapping("/list-by-note")
    @Operation(summary = "App 端获取指定笔记的已采集评论")
    public CommonResult<List<XhsNoteCommentRespVO>> getCommentListByNoteId(@RequestParam("noteId") String noteId) {
        List<XhsNoteCommentDO> list = xhsNoteCommentService.getCommentListByNoteId(noteId);
        return success(BeanUtil.copyToList(list, XhsNoteCommentRespVO.class));
    }

}
