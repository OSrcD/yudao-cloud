package cn.iocoder.yudao.module.ai.service.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.chatRole.AiChatRolePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.chatRole.AiChatRoleSaveMyReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.chatRole.AiChatRoleSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiChatRoleDO;
import cn.iocoder.yudao.module.ai.dal.mysql.model.AiChatRoleMapper;
import cn.iocoder.yudao.module.ai.service.knowledge.AiKnowledgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CHAT_ROLE_DISABLE;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CHAT_ROLE_NOT_EXISTS;

/**
 * AI 聊天角色 Service 实现类
 *
 * @author fansili
 */
@Service
@Slf4j
public class AiChatRoleServiceImpl implements AiChatRoleService {

    @Resource
    private AiChatRoleMapper chatRoleMapper;

    @Resource
    private AiKnowledgeService knowledgeService;
    @Resource
    private AiToolService toolService;

    /**
     * APP 截流角色自动追加的标准 JSON 输出格式规范
     */
    public static final String APP_INTERCEPT_OUTPUT_FORMAT_SUFFIX = "\n\n" +
            "最终输出格式\n\n" +
            "只输出合法 JSON。\n\n" +
            "不要输出 Markdown。\n" +
            "不要输出代码块。\n" +
            "不要输出分析过程。\n" +
            "不要输出多个版本。\n" +
            "不要输出额外解释。\n\n" +
            "严格按照以下格式输出：\n\n" +
            "{\n" +
            "\"comment\": \"最终生成的评论内容\"\n" +
            "}\n\n" +
            "其中：\n\n" +
            "comment：\n\n" +
            "只填写最终准备发布到评论区的评论内容。\n\n" +
            "必须生成一条不为空的评论\n\n" +
            "不要在 comment 中添加“【评论】”“评论：”等额外标记。";

    private String formatAppSystemMessage(String systemMessage, String clientType) {
        if (StrUtil.isBlank(systemMessage)) {
            return systemMessage;
        }
        if (StrUtil.isBlank(clientType) || "APP".equalsIgnoreCase(clientType)) {
            if (!systemMessage.contains("最终输出格式") && !systemMessage.contains("\"comment\": \"最终生成的评论内容\"")) {
                return systemMessage.trim() + APP_INTERCEPT_OUTPUT_FORMAT_SUFFIX;
            }
        }
        return systemMessage;
    }


    @Override
    public Long createChatRole(AiChatRoleSaveReqVO createReqVO) {
        // 校验文档
        validateDocuments(createReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(createReqVO.getToolIds());

        // 保存角色
        AiChatRoleDO chatRole = BeanUtils.toBean(createReqVO, AiChatRoleDO.class);
        if (StrUtil.isBlank(chatRole.getClientType())) {
            chatRole.setClientType("PC");
        }
        if (createReqVO.getTemplateRoleId() != null && createReqVO.getTemplateRoleId() > 0) {
            AiChatRoleDO templateRole = chatRoleMapper.selectById(createReqVO.getTemplateRoleId());
            if (templateRole != null && StrUtil.isNotBlank(templateRole.getSystemMessage())) {
                chatRole.setSystemMessage(templateRole.getSystemMessage());
            }
        }
        if ("APP".equalsIgnoreCase(chatRole.getClientType())) {
            chatRole.setSystemMessage(formatAppSystemMessage(chatRole.getSystemMessage(), chatRole.getClientType()));
        }
        chatRoleMapper.insert(chatRole);
        return chatRole.getId();
    }

    @Override
    public Long createChatRoleMy(AiChatRoleSaveMyReqVO createReqVO, Long userId) {
        // 校验文档
        validateDocuments(createReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(createReqVO.getToolIds());

        // 保存角色
        AiChatRoleDO chatRole = BeanUtils.toBean(createReqVO, AiChatRoleDO.class).setUserId(userId)
                .setStatus(CommonStatusEnum.ENABLE.getStatus()).setPublicStatus(false);
        if (StrUtil.isBlank(chatRole.getClientType())) {
            chatRole.setClientType("APP");
        }
        // 如果是基于官方模板新增的角色：直接引用官方模板提示词内容（系统提示词中保留占位符，不替换变量）
        if (createReqVO.getTemplateRoleId() != null && createReqVO.getTemplateRoleId() > 0) {
            AiChatRoleDO templateRole = chatRoleMapper.selectById(createReqVO.getTemplateRoleId());
            if (templateRole != null && StrUtil.isNotBlank(templateRole.getSystemMessage())) {
                chatRole.setSystemMessage(templateRole.getSystemMessage());
            }
        }
        // APP 端自定义角色自动补齐输出格式（用户无感知）
        chatRole.setSystemMessage(formatAppSystemMessage(chatRole.getSystemMessage(), chatRole.getClientType()));
        chatRoleMapper.insert(chatRole);
        return chatRole.getId();
    }

    @Override
    public void updateChatRole(AiChatRoleSaveReqVO updateReqVO) {
        // 校验存在
        validateChatRoleExists(updateReqVO.getId());
        // 校验文档
        validateDocuments(updateReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(updateReqVO.getToolIds());

        // 更新角色
        AiChatRoleDO updateObj = BeanUtils.toBean(updateReqVO, AiChatRoleDO.class);
        Long templateRoleId = updateReqVO.getTemplateRoleId();
        if (templateRoleId != null && templateRoleId > 0) {
            AiChatRoleDO templateRole = chatRoleMapper.selectById(templateRoleId);
            if (templateRole != null && StrUtil.isNotBlank(templateRole.getSystemMessage())) {
                updateObj.setSystemMessage(templateRole.getSystemMessage());
            }
        }
        if ("APP".equalsIgnoreCase(updateObj.getClientType())) {
            updateObj.setSystemMessage(formatAppSystemMessage(updateObj.getSystemMessage(), updateObj.getClientType()));
        }
        chatRoleMapper.updateById(updateObj);
    }

    @Override
    public void updateChatRoleMy(AiChatRoleSaveMyReqVO updateReqVO, Long userId) {
        // 校验存在
        AiChatRoleDO chatRole = validateChatRoleExists(updateReqVO.getId());
        if (ObjectUtil.notEqual(chatRole.getUserId(), userId)) {
            throw exception(CHAT_ROLE_NOT_EXISTS);
        }
        // 校验文档
        validateDocuments(updateReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(updateReqVO.getToolIds());

        // 更新
        AiChatRoleDO updateObj = BeanUtils.toBean(updateReqVO, AiChatRoleDO.class);
        String clientType = StrUtil.isNotBlank(updateObj.getClientType()) ? updateObj.getClientType() : chatRole.getClientType();
        // 如果是基于官方模板的角色：直接引用官方模板提示词内容（系统提示词中保留占位符，不替换变量）
        Long templateRoleId = updateReqVO.getTemplateRoleId() != null ? updateReqVO.getTemplateRoleId() : chatRole.getTemplateRoleId();
        if (templateRoleId != null && templateRoleId > 0) {
            AiChatRoleDO templateRole = chatRoleMapper.selectById(templateRoleId);
            if (templateRole != null && StrUtil.isNotBlank(templateRole.getSystemMessage())) {
                updateObj.setSystemMessage(templateRole.getSystemMessage());
            }
        }
        // APP 端自定义角色自动补齐输出格式（用户无感知）
        updateObj.setSystemMessage(formatAppSystemMessage(updateObj.getSystemMessage(), clientType));
        chatRoleMapper.updateById(updateObj);
    }

    /**
     * 校验知识库是否存在
     *
     * @param knowledgeIds 知识库编号列表
     */
    private void validateDocuments(List<Long> knowledgeIds) {
        if (CollUtil.isEmpty(knowledgeIds)) {
            return;
        }
        // 校验文档是否存在
        knowledgeIds.forEach(knowledgeService::validateKnowledgeExists);
    }

    /**
     * 校验工具是否存在
     *
     * @param toolIds 工具编号列表
     */
    private void validateTools(List<Long> toolIds) {
        if (CollUtil.isEmpty(toolIds)) {
            return;
        }
        // 遍历校验每个工具是否存在
        toolIds.forEach(toolService::validateToolExists);
    }

    @Override
    public void deleteChatRole(Long id) {
        // 校验存在
        validateChatRoleExists(id);
        // 删除
        chatRoleMapper.deleteById(id);
    }

    @Override
    public void deleteChatRoleMy(Long id, Long userId) {
        // 校验存在
        AiChatRoleDO chatRole = validateChatRoleExists(id);
        if (ObjectUtil.notEqual(chatRole.getUserId(), userId)) {
            throw exception(CHAT_ROLE_NOT_EXISTS);
        }
        // 删除
        chatRoleMapper.deleteById(id);
    }

    private AiChatRoleDO validateChatRoleExists(Long id) {
        AiChatRoleDO chatRole = chatRoleMapper.selectById(id);
        if (chatRole == null) {
            throw exception(CHAT_ROLE_NOT_EXISTS);
        }
        return chatRole;
    }

    @Override
    public AiChatRoleDO getChatRole(Long id) {
        return chatRoleMapper.selectById(id);
    }

    @Override
    public List<AiChatRoleDO> getChatRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return chatRoleMapper.selectByIds(ids);
    }

    @Override
    public AiChatRoleDO validateChatRole(Long id) {
        AiChatRoleDO chatRole = validateChatRoleExists(id);
        if (CommonStatusEnum.isDisable(chatRole.getStatus())) {
            throw exception(CHAT_ROLE_DISABLE, chatRole.getName());
        }
        return chatRole;
    }

    @Override
    public PageResult<AiChatRoleDO> getChatRolePage(AiChatRolePageReqVO pageReqVO) {
        return chatRoleMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<AiChatRoleDO> getChatRoleMyPage(AiChatRolePageReqVO pageReqVO, Long userId) {
        return chatRoleMapper.selectPageByMy(pageReqVO, userId);
    }

    @Override
    public List<String> getChatRoleCategoryList() {
        List<AiChatRoleDO> list = chatRoleMapper.selectListGroupByCategory(CommonStatusEnum.ENABLE.getStatus());
        return convertList(list, AiChatRoleDO::getCategory,
                role -> role != null && StrUtil.isNotBlank(role.getCategory()));
    }

    @Override
    public List<AiChatRoleDO> getChatRoleListByName(String name) {
        return chatRoleMapper.selectListByName(name);
    }

}
