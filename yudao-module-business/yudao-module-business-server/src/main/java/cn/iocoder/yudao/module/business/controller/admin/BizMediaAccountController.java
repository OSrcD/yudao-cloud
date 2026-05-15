package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMediaAccountDO;
import cn.iocoder.yudao.module.business.service.BizMediaAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 自媒体账号")
@RestController
@RequestMapping("/business/media-account")
@Validated
public class BizMediaAccountController {

    @Resource
    private BizMediaAccountService mediaAccountService;

    @PostMapping("/create")
    @Operation(summary = "创建自媒体账号")
    @PreAuthorize("@ss.hasPermission('business:media-account:create')")
    public CommonResult<Long> createMediaAccount(@Valid @RequestBody BizMediaAccountSaveReqVO createReqVO) {
        return success(mediaAccountService.createMediaAccount(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新自媒体账号")
    @PreAuthorize("@ss.hasPermission('business:media-account:update')")
    public CommonResult<Boolean> updateMediaAccount(@Valid @RequestBody BizMediaAccountSaveReqVO updateReqVO) {
        mediaAccountService.updateMediaAccount(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除自媒体账号")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:media-account:delete')")
    public CommonResult<Boolean> deleteMediaAccount(@RequestParam("id") Long id) {
        mediaAccountService.deleteMediaAccount(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得自媒体账号")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:media-account:query')")
    public CommonResult<BizMediaAccountDO> getMediaAccount(@RequestParam("id") Long id) {
        return success(mediaAccountService.getMediaAccount(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得自媒体账号分页")
    @PreAuthorize("@ss.hasPermission('business:media-account:query')")
    public CommonResult<PageResult<BizMediaAccountDO>> getMediaAccountPage(@Valid BizMediaAccountPageReqVO pageVO) {
        return success(mediaAccountService.getMediaAccountPage(pageVO));
    }

}

