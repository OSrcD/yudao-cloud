package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorNotifyConfigVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorKeywordDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.service.XhsCommentMonitorNotifyService;
import cn.iocoder.yudao.module.business.service.XhsCommentMonitorService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 小红书评论监控")
@RestController
@RequestMapping("/business/xhs-comment-monitor")
@Validated
public class XhsCommentMonitorController {

    @Resource
    private XhsCommentMonitorService commentMonitorService;

    @Resource
    private XhsCommentMonitorNotifyService notifyService;

    // ========== 评论监控分页 ==========

    @GetMapping("/page")
    @Operation(summary = "获取评论监控分页")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:query')")
    public CommonResult<PageResult<XhsNoteCommentDO>> getMonitorPage(@Valid XhsCommentMonitorPageReqVO reqVO) {
        return success(commentMonitorService.getCommentMonitorPage(reqVO));
    }

    // ========== 关键词管理 ==========

    @GetMapping("/keywords")
    @Operation(summary = "获取全部监控关键词")
    @PreAuthorize("@ss.hasPermission('business:xhs-note-comment:query')")
    public CommonResult<List<XhsCommentMonitorKeywordDO>> getKeywords() {
        return success(commentMonitorService.getKeywords());
    }

    @PostMapping("/keyword")
    @Operation(summary = "新增监控关键词")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-monitor:keyword')")
    public CommonResult<XhsCommentMonitorKeywordDO> addKeyword(@RequestBody Map<String, String> body) {
        String keyword = body.get("keyword");
        return success(commentMonitorService.addKeyword(keyword));
    }

    @DeleteMapping("/keyword")
    @Operation(summary = "删除监控关键词")
    @Parameter(name = "id", description = "关键词ID", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-monitor:keyword')")
    public CommonResult<Boolean> deleteKeyword(@RequestParam("id") Long id) {
        commentMonitorService.deleteKeyword(id);
        return success(true);
    }

    // ========== 推送配置 ==========

    @GetMapping("/notify-config")
    @Operation(summary = "获取推送配置")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-monitor:notify')")
    public CommonResult<XhsCommentMonitorNotifyConfigVO> getNotifyConfig() {
        return success(notifyService.getNotifyConfig());
    }

    @PutMapping("/notify-config")
    @Operation(summary = "保存推送配置")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-monitor:notify')")
    public CommonResult<Boolean> saveNotifyConfig(@RequestBody XhsCommentMonitorNotifyConfigVO config) {
        notifyService.saveNotifyConfig(config);
        return success(true);
    }

    @PostMapping("/notify-test")
    @Operation(summary = "发送测试推送")
    @PreAuthorize("@ss.hasPermission('business:xhs-comment-monitor:notify')")
    public CommonResult<Boolean> notifyTest(@RequestBody XhsCommentMonitorNotifyConfigVO config) {
        boolean result = notifyService.sendTest(config);
        return success(result);
    }

}
