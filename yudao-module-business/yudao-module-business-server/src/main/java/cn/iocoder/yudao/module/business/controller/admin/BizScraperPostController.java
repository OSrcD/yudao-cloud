package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizScraperPostDO;
import cn.iocoder.yudao.module.business.service.BizScraperPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 采集帖子")
@RestController
@RequestMapping("/business/scraper-post")
@Validated
public class BizScraperPostController {

    @Resource
    private BizScraperPostService scraperPostService;

    @PostMapping("/create")
    @Operation(summary = "创建采集帖子")
    @PreAuthorize("@ss.hasPermission('business:scraper-post:create')")
    public CommonResult<Long> createScraperPost(@Valid @RequestBody BizScraperPostSaveReqVO createReqVO) {
        return success(scraperPostService.createScraperPost(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新采集帖子")
    @PreAuthorize("@ss.hasPermission('business:scraper-post:update')")
    public CommonResult<Boolean> updateScraperPost(@Valid @RequestBody BizScraperPostSaveReqVO updateReqVO) {
        scraperPostService.updateScraperPost(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除采集帖子")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('business:scraper-post:delete')")
    public CommonResult<Boolean> deleteScraperPost(@RequestParam("id") Long id) {
        scraperPostService.deleteScraperPost(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得采集帖子")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('business:scraper-post:query')")
    public CommonResult<BizScraperPostDO> getScraperPost(@RequestParam("id") Long id) {
        return success(scraperPostService.getScraperPost(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得采集帖子分页")
    @PreAuthorize("@ss.hasPermission('business:scraper-post:query')")
    public CommonResult<PageResult<BizScraperPostDO>> getScraperPostPage(@Valid BizScraperPostPageReqVO pageVO) {
        return success(scraperPostService.getScraperPostPage(pageVO));
    }

    @PutMapping("/update-media")
    @Operation(summary = "更新媒体文件")
    @PreAuthorize("@ss.hasPermission('business:scraper-post:update')")
    public CommonResult<Boolean> updateScraperMedia(@RequestParam("id") Long id, @RequestBody Map<String, String> data) {
        scraperPostService.updateMedia(id, data.get("images"), data.get("videos"));
        return success(true);
    }

    @PutMapping("/update-restyle")
    @Operation(summary = "更新重写信息")
    @PreAuthorize("@ss.hasPermission('business:scraper-post:update')")
    public CommonResult<Boolean> updateScraperRestyle(@RequestParam("id") Long id, @RequestBody String restyleInfo) {
        scraperPostService.updateRestyleInfo(id, restyleInfo);
        return success(true);
    }

}

