package cn.iocoder.yudao.module.business.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizReproduceConfigDO;
import cn.iocoder.yudao.module.business.service.BizReproduceConfigService;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizReproduceConfigMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 搬运配置")
@RestController
@RequestMapping("/business/app/reproduce-config")
@Validated
public class BizReproduceConfigAppController {

    @Resource
    private BizReproduceConfigService reproduceConfigService;
    
    @Resource
    private BizReproduceConfigMapper reproduceConfigMapper;

    @PostMapping("/create")
    @Operation(summary = "创建我的搬运配置")
    public CommonResult<Long> createReproduceConfig(@Valid @RequestBody BizReproduceConfigSaveReqVO createReqVO) {
        return success(reproduceConfigService.createReproduceConfig(createReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除我的搬运配置")
    public CommonResult<Boolean> deleteReproduceConfig(@RequestParam("id") Long id) {
        BizReproduceConfigDO config = reproduceConfigService.getReproduceConfig(id);
        if (config != null && String.valueOf(SecurityFrameworkUtils.getLoginUserId()).equals(config.getCreator())) {
            reproduceConfigService.deleteReproduceConfig(id);
        }
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得我的搬运配置分页")
    public CommonResult<PageResult<BizReproduceConfigDO>> getMyReproduceConfigPage(@Valid BizReproduceConfigPageReqVO pageVO) {
        String userId = String.valueOf(SecurityFrameworkUtils.getLoginUserId());
        PageResult<BizReproduceConfigDO> pageResult = reproduceConfigMapper.selectPage(pageVO, new LambdaQueryWrapperX<BizReproduceConfigDO>()
                .eq(BizReproduceConfigDO::getCreator, userId)
                .likeIfPresent(BizReproduceConfigDO::getConfigName, pageVO.getConfigName())
                .orderByDesc(BizReproduceConfigDO::getId));
        return success(pageResult);
    }
}
