package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMaterialDO;
import cn.iocoder.yudao.module.business.service.BizMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 素材库")
@RestController
@RequestMapping("/business/material")
@Validated
public class BizMaterialController {

    @Resource
    private BizMaterialService materialService;

    @PostMapping("/create")
    @Operation(summary = "创建素材")
    @PreAuthorize("@ss.hasPermission('business:material:create')")
    public CommonResult<Long> createMaterial(@Valid @RequestBody BizMaterialSaveReqVO createReqVO) {
        if (createReqVO.getUserId() == null) {
            createReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        }
        if (createReqVO.getUserType() == null && SecurityFrameworkUtils.getLoginUser() != null) {
            createReqVO.setUserType(SecurityFrameworkUtils.getLoginUser().getUserType());
        }
        return success(materialService.createMaterial(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材")
    @PreAuthorize("@ss.hasPermission('business:material:update')")
    public CommonResult<Boolean> updateMaterial(@Valid @RequestBody BizMaterialSaveReqVO updateReqVO) {
        materialService.updateMaterial(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:material:delete')")
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        materialService.deleteMaterial(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得素材")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:material:query')")
    public CommonResult<BizMaterialDO> getMaterial(@RequestParam("id") Long id) {
        return success(materialService.getMaterial(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得素材分页")
    @PreAuthorize("@ss.hasPermission('business:material:query')")
    public CommonResult<PageResult<BizMaterialDO>> getMaterialPage(@Valid BizMaterialPageReqVO pageVO) {
        return success(materialService.getMaterialPage(pageVO));
    }

}

