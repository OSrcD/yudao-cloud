package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizXhsOfficialAccountConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsOfficialAccountDO;
import cn.iocoder.yudao.module.business.service.BizXhsOfficialAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 小红书不评论官方号名单")
@RestController
@RequestMapping("/business/xhs-official-account")
@Validated
public class BizXhsOfficialAccountController {

    @Resource
    private BizXhsOfficialAccountService officialAccountService;

    @PostMapping("/create")
    @Operation(summary = "创建官方号记录")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:create')")
    public CommonResult<Long> createOfficialAccount(@Valid @RequestBody BizXhsOfficialAccountSaveReqVO createReqVO) {
        return success(officialAccountService.createOfficialAccount(createReqVO));
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建/导入官方号")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:create')")
    public CommonResult<Integer> batchCreateOfficialAccount(@Valid @RequestBody List<BizXhsOfficialAccountSaveReqVO> list) {
        return success(officialAccountService.batchCreateOfficialAccount(list));
    }

    @PutMapping("/update")
    @Operation(summary = "更新官方号记录")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:update')")
    public CommonResult<Boolean> updateOfficialAccount(@Valid @RequestBody BizXhsOfficialAccountSaveReqVO updateReqVO) {
        officialAccountService.updateOfficialAccount(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除官方号记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:delete')")
    public CommonResult<Boolean> deleteOfficialAccount(@RequestParam("id") Long id) {
        officialAccountService.deleteOfficialAccount(id);
        return success(true);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空全部官方号名单")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:delete')")
    public CommonResult<Boolean> clearOfficialAccounts() {
        officialAccountService.clearOfficialAccounts();
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得官方号记录详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:query')")
    public CommonResult<BizXhsOfficialAccountRespVO> getOfficialAccount(@RequestParam("id") Long id) {
        BizXhsOfficialAccountDO officialAccount = officialAccountService.getOfficialAccount(id);
        return success(BizXhsOfficialAccountConvert.INSTANCE.convert(officialAccount));
    }

    @GetMapping("/page")
    @Operation(summary = "获得官方号分页列表")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:query')")
    public CommonResult<PageResult<BizXhsOfficialAccountRespVO>> getOfficialAccountPage(@Valid BizXhsOfficialAccountPageReqVO pageReqVO) {
        PageResult<BizXhsOfficialAccountDO> pageResult = officialAccountService.getOfficialAccountPage(pageReqVO);
        return success(BizXhsOfficialAccountConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得所有生效的官方号列表")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:query')")
    public CommonResult<List<BizXhsOfficialAccountRespVO>> getOfficialAccountList(@RequestParam(value = "status", required = false) Integer status) {
        List<BizXhsOfficialAccountDO> list = officialAccountService.getOfficialAccountList(status);
        return success(BizXhsOfficialAccountConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出小红书官方号名单 Excel")
    @PreAuthorize("@ss.hasPermission('business:xhs-official-account:export')")
    public void exportOfficialAccountExcel(@Valid BizXhsOfficialAccountPageReqVO pageReqVO,
                                           HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<BizXhsOfficialAccountDO> list = officialAccountService.getOfficialAccountPage(pageReqVO).getList();
        List<BizXhsOfficialAccountRespVO> respList = BizXhsOfficialAccountConvert.INSTANCE.convertList(list);
        ExcelUtils.write(response, "小红书不评论官方号名单.xls", "数据", BizXhsOfficialAccountRespVO.class, respList);
    }

}
