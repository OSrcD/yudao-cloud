package cn.iocoder.yudao.module.business.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentUpdateStatusReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCollectMapper;
import cn.iocoder.yudao.module.business.service.XhsNoteCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 小红书笔记评论采集与私信截流")
@RestController
@RequestMapping("/business/xhs-note-comment")
@Validated
public class XhsNoteCommentController {

    @Resource
    private XhsNoteCommentService xhsNoteCommentService;
    
    @Resource
    private XhsNoteCollectMapper xhsNoteCollectMapper;

    @PostMapping("/create")
    @Operation(summary = "创建小红书评论")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:create')")
    public CommonResult<Boolean> createXhsNoteComment(@Valid @RequestBody XhsNoteCommentSaveReqVO createReqVO) {
        return success(xhsNoteCommentService.saveOrUpdateXhsNoteComment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新小红书评论")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:update')")
    public CommonResult<Boolean> updateXhsNoteComment(@Valid @RequestBody XhsNoteCommentSaveReqVO updateReqVO) {
        return success(xhsNoteCommentService.saveOrUpdateXhsNoteComment(updateReqVO));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新评论私信截流跟进状态")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:update')")
    public CommonResult<Boolean> updateInterceptStatus(@Valid @RequestBody XhsNoteCommentUpdateStatusReqVO statusReqVO) {
        return success(xhsNoteCommentService.updateInterceptStatus(statusReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除小红书评论")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:delete')")
    public CommonResult<Boolean> deleteXhsNoteComment(@RequestParam("id") Long id) {
        xhsNoteCommentService.deleteXhsNoteComment(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得小红书评论分页")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:query')")
    public CommonResult<PageResult<XhsNoteCommentRespVO>> getXhsNoteCommentPage(@Valid XhsNoteCommentPageReqVO pageReqVO) {
        PageResult<XhsNoteCommentDO> pageResult = xhsNoteCommentService.getXhsNoteCommentPage(pageReqVO);
        if (pageResult.getList() == null || pageResult.getList().isEmpty()) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        PageResult<XhsNoteCommentRespVO> respPage = cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean(pageResult, XhsNoteCommentRespVO.class);
        
        List<String> noteIds = respPage.getList().stream().map(XhsNoteCommentRespVO::getNoteId).distinct().collect(Collectors.toList());
        List<XhsNoteCollectDO> noteList = xhsNoteCollectMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<XhsNoteCollectDO>()
                .in(XhsNoteCollectDO::getNoteId, noteIds));
        Map<String, XhsNoteCollectDO> noteMap = noteList.stream().collect(Collectors.toMap(XhsNoteCollectDO::getNoteId, n -> n, (a, b) -> a));
        
        respPage.getList().forEach(vo -> {
            XhsNoteCollectDO note = noteMap.get(vo.getNoteId());
            if (note != null) {
                vo.setNoteTitle(note.getTitle());
                vo.setNoteDesc(note.getNoteDesc());
                vo.setNotePublishTime(note.getPublishTime());
                vo.setNoteIsMonitored(note.getIsMonitored());
                vo.setNoteUrl(note.getNoteUrl());
                vo.setNoteCollectId(note.getId());
            }
        });
        return success(respPage);
    }

    @GetMapping("/list-by-note")
    @Operation(summary = "根据笔记ID获得所有评论列表")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:query')")
    public CommonResult<List<XhsNoteCommentRespVO>> getCommentListByNoteId(@RequestParam("noteId") String noteId) {
        List<XhsNoteCommentDO> list = xhsNoteCommentService.getCommentListByNoteId(noteId);
        return success(BeanUtil.copyToList(list, XhsNoteCommentRespVO.class));
    }

}
