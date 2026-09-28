package cn.iocoder.yudao.module.ai.controller.app.model;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 用户 APP - AI 模型
 *
 * 【向下兼容旧版本客户端】：为兼容各端通过 /app-api/ai/model/simple-list 获取可用模型，提供该接口以防 404
 */
@Tag(name = "用户 APP - AI 模型")
@RestController
@RequestMapping("/ai/model")
@Validated
public class AppAiModelController {

    @Resource
    private AiModelService modelService;

    @GetMapping("/simple-list")
    @Operation(summary = "获得模型列表", description = "【向下兼容】：供 APP 移动端拉取适用模型列表")
    @Parameter(name = "type", description = "类型", required = true, example = "1")
    @Parameter(name = "platform", description = "平台", example = "deepseek")
    @Parameter(name = "clientType", description = "适用终端（APP/PC/ALL）", example = "APP")
    public CommonResult<List<AiModelRespVO>> getModelSimpleList(
            @RequestParam("type") Integer type,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "clientType", required = false, defaultValue = "APP") String clientType) {
        List<AiModelDO> list = modelService.getModelListByStatusAndType(
                CommonStatusEnum.ENABLE.getStatus(), type, platform, clientType);
        return success(convertList(list, model -> BeanUtils.toBean(model, AiModelRespVO.class)));
    }

}
