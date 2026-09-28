package cn.iocoder.yudao.module.member.controller.admin.vip;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackagePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackageRespVO;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackageSaveReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipPackageDO;
import cn.iocoder.yudao.module.member.service.vip.MemberVipPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员 VIP 套餐")
@RestController
@RequestMapping("/member/vip-package")
@Validated
public class MemberVipPackageController {

    @Resource
    private MemberVipPackageService vipPackageService;

    @PostMapping("/create")
    @Operation(summary = "创建 VIP 套餐")
    @PreAuthorize("@ss.hasPermission('member:vip-package:create')")
    public CommonResult<Long> createPackage(@Valid @RequestBody MemberVipPackageSaveReqVO reqVO) {
        return success(vipPackageService.createPackage(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 VIP 套餐")
    @PreAuthorize("@ss.hasPermission('member:vip-package:update')")
    public CommonResult<Boolean> updatePackage(@Valid @RequestBody MemberVipPackageSaveReqVO reqVO) {
        vipPackageService.updatePackage(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 VIP 套餐")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:vip-package:delete')")
    public CommonResult<Boolean> deletePackage(@RequestParam("id") Long id) {
        vipPackageService.deletePackage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 VIP 套餐")
    @PreAuthorize("@ss.hasPermission('member:vip-package:query')")
    public CommonResult<MemberVipPackageRespVO> getPackage(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(vipPackageService.getPackage(id), MemberVipPackageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 VIP 套餐分页")
    @PreAuthorize("@ss.hasPermission('member:vip-package:query')")
    public CommonResult<PageResult<MemberVipPackageRespVO>> getPackagePage(@Valid MemberVipPackagePageReqVO pageReqVO) {
        PageResult<MemberVipPackageDO> page = vipPackageService.getPackagePage(pageReqVO);
        return success(BeanUtils.toBean(page, MemberVipPackageRespVO.class));
    }

}
