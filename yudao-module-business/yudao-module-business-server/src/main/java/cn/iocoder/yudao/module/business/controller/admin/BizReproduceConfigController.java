package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizReproduceConfigDO;
import cn.iocoder.yudao.module.business.service.BizReproduceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 搬运配置")
@RestController
@RequestMapping("/business/reproduce-config")
@Validated
public class BizReproduceConfigController {

    @Resource
    private BizReproduceConfigService reproduceConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建搬运配置")
    @PreAuthorize("@ss.hasPermission('business:reproduce-config:create')")
    public CommonResult<Long> createReproduceConfig(@Valid @RequestBody BizReproduceConfigSaveReqVO createReqVO) {
        return success(reproduceConfigService.createReproduceConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新搬运配置")
    @PreAuthorize("@ss.hasPermission('business:reproduce-config:update')")
    public CommonResult<Boolean> updateReproduceConfig(@Valid @RequestBody BizReproduceConfigSaveReqVO updateReqVO) {
        reproduceConfigService.updateReproduceConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除搬运配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:reproduce-config:delete')")
    public CommonResult<Boolean> deleteReproduceConfig(@RequestParam("id") Long id) {
        reproduceConfigService.deleteReproduceConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得搬运配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:reproduce-config:query')")
    public CommonResult<BizReproduceConfigDO> getReproduceConfig(@RequestParam("id") Long id) {
        return success(reproduceConfigService.getReproduceConfig(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得搬运配置分页")
    @PreAuthorize("@ss.hasPermission('business:reproduce-config:query')")
    public CommonResult<PageResult<BizReproduceConfigDO>> getReproduceConfigPage(@Valid BizReproduceConfigPageReqVO pageVO) {
        return success(reproduceConfigService.getReproduceConfigPage(pageVO));
    }

}

