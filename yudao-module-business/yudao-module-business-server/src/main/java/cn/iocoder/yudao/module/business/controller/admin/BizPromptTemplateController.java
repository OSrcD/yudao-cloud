package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptTemplateDO;
import cn.iocoder.yudao.module.business.service.BizPromptTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 提示词模板")
@RestController
@RequestMapping("/business/prompt-template")
@Validated
public class BizPromptTemplateController {

    @Resource
    private BizPromptTemplateService promptTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建提示词模板")
    @PreAuthorize("@ss.hasPermission('business:prompt-template:create')")
    public CommonResult<Long> createPromptTemplate(@Valid @RequestBody BizPromptTemplateSaveReqVO createReqVO) {
        return success(promptTemplateService.createPromptTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新提示词模板")
    @PreAuthorize("@ss.hasPermission('business:prompt-template:update')")
    public CommonResult<Boolean> updatePromptTemplate(@Valid @RequestBody BizPromptTemplateSaveReqVO updateReqVO) {
        promptTemplateService.updatePromptTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除提示词模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:prompt-template:delete')")
    public CommonResult<Boolean> deletePromptTemplate(@RequestParam("id") Long id) {
        promptTemplateService.deletePromptTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得提示词模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:prompt-template:query')")
    public CommonResult<BizPromptTemplateDO> getPromptTemplate(@RequestParam("id") Long id) {
        return success(promptTemplateService.getPromptTemplate(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得提示词模板分页")
    @PreAuthorize("@ss.hasPermission('business:prompt-template:query')")
    public CommonResult<PageResult<BizPromptTemplateDO>> getPromptTemplatePage(@Valid BizPromptTemplatePageReqVO pageVO) {
        return success(promptTemplateService.getPromptTemplatePage(pageVO));
    }

}

