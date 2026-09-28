package cn.iocoder.yudao.module.ai.controller.app.model;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.chatRole.AiChatRolePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.chatRole.AiChatRoleRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiChatRoleDO;
import cn.iocoder.yudao.module.ai.service.model.AiChatRoleService;
import com.fhs.core.trans.anno.TransMethodResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.chatRole.AiChatRoleSaveMyReqVO;
import org.springframework.web.bind.annotation.*;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 App - AI 聊天角色（对齐管理端 my-page，供会员拉角色列表与自定义专属角色）
 */
@Tag(name = "用户 APP - AI 聊天角色")
@RestController
@RequestMapping("/ai/chat-role")
@Validated
public class AppAiChatRoleController {

    @Resource
    private AiChatRoleService chatRoleService;
    @Resource
    private AiModelService modelService;

    @GetMapping("/my-page")
    @Operation(summary = "获得【我的】聊天角色分页")
    @TransMethodResult
    public CommonResult<PageResult<AiChatRoleRespVO>> getChatRoleMyPage(@Valid AiChatRolePageReqVO pageReqVO) {
        // APP 端固定只获取适用于 APP 移动端的角色（或全部通用角色）
        if (cn.hutool.core.util.StrUtil.isBlank(pageReqVO.getClientType())) {
            pageReqVO.setClientType("APP");
        }
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRoleMyPage(pageReqVO, getLoginUserId());
        PageResult<AiChatRoleRespVO> voPage = BeanUtils.toBean(pageResult, AiChatRoleRespVO.class);
        // 保护官方预设角色的核心提示词与模板角色的预设提示词：对外隐藏 systemMessage，防止终端直接读取商业机密提示词
        if (CollUtil.isNotEmpty(voPage.getList())) {
            voPage.getList().forEach(vo -> {
                if (Boolean.TRUE.equals(vo.getPublicStatus()) || (vo.getTemplateRoleId() != null && vo.getTemplateRoleId() > 0)) {
                    vo.setSystemMessage(null);
                }
            });
        }
        return success(voPage);
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天角色详情（公开角色或我的角色）")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @TransMethodResult
    public CommonResult<AiChatRoleRespVO> getChatRole(@RequestParam("id") Long id) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
        if (chatRole == null) {
            return success(null);
        }
        // 仅允许：公开角色，或当前用户自己的角色
        boolean ok = Boolean.TRUE.equals(chatRole.getPublicStatus())
                || ObjUtil.equal(chatRole.getUserId(), getLoginUserId());
        if (!ok) {
            return success(null);
        }
        // APP 端只能获取非 PC 专用的角色
        if ("PC".equalsIgnoreCase(chatRole.getClientType())) {
            return success(null);
        }
        AiChatRoleRespVO vo = BeanUtils.toBean(chatRole, AiChatRoleRespVO.class);
        // 保护官方预设角色的核心提示词与模板角色的预设提示词：对外隐藏 systemMessage
        if (Boolean.TRUE.equals(vo.getPublicStatus()) || (vo.getTemplateRoleId() != null && vo.getTemplateRoleId() > 0)) {
            vo.setSystemMessage(null);
        }
        return success(vo);
    }

    @PostMapping("/create-my")
    @Operation(summary = "创建【我的】聊天角色")
    public CommonResult<Long> createChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO createReqVO) {
        if (cn.hutool.core.util.StrUtil.isBlank(createReqVO.getClientType())) {
            createReqVO.setClientType("APP");
        }
        return success(chatRoleService.createChatRoleMy(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新【我的】聊天角色")
    public CommonResult<Boolean> updateChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO updateReqVO) {
        if (cn.hutool.core.util.StrUtil.isBlank(updateReqVO.getClientType())) {
            updateReqVO.setClientType("APP");
        }
        chatRoleService.updateChatRoleMy(updateReqVO, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除【我的】聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteChatRoleMy(@RequestParam("id") Long id) {
        chatRoleService.deleteChatRoleMy(id, getLoginUserId());
        return success(true);
    }

    @GetMapping("/category-list")
    @Operation(summary = "获得聊天角色的分类列表")
    public CommonResult<List<String>> getChatRoleCategoryList() {
        return success(chatRoleService.getChatRoleCategoryList());
    }

    @GetMapping("/model-list")
    @Operation(summary = "获得 APP 端可用 AI 聊天模型列表")
    public CommonResult<List<AiModelRespVO>> getAppChatModelList() {
        List<AiModelDO> list = modelService.getModelListByStatusAndType(
                CommonStatusEnum.ENABLE.getStatus(), 1, null, "APP");
        return success(convertList(list, model -> new AiModelRespVO().setId(model.getId())
                .setName(model.getName()).setModel(model.getModel()).setPlatform(model.getPlatform())
                .setClientType(model.getClientType()).setMaxContexts(model.getMaxContexts())
                .setSort(model.getSort())
                .setIsDefault(Boolean.TRUE.equals(model.getIsDefault()) || (model.getSort() != null && model.getSort() == 1))));
    }

}
