package cn.iocoder.yudao.module.ai.controller.admin.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.AiChatConversationCreateMyReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.AiChatConversationPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.AiChatConversationRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;
import cn.iocoder.yudao.module.ai.service.chat.AiChatConversationService;
import cn.iocoder.yudao.module.ai.service.chat.AiChatMessageService;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.fhs.core.trans.anno.TransMethodResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 聊天对话")
@RestController
@RequestMapping("/ai/chat/conversation")
@Validated
public class AiChatConversationController {

    @Resource
    private AiChatConversationService chatConversationService;
    @Resource
    private AiChatMessageService chatMessageService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private MemberUserApi memberUserApi;

    @PostMapping("/create-my")
    @Operation(summary = "创建【我的】聊天对话")
    public CommonResult<Long> createChatConversationMy(@RequestBody @Valid AiChatConversationCreateMyReqVO createReqVO) {
        return success(chatConversationService.createChatConversationMy(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新【我的】聊天对话")
    public CommonResult<Boolean> updateChatConversationMy(@RequestBody @Valid AiChatConversationUpdateMyReqVO updateReqVO) {
        chatConversationService.updateChatConversationMy(updateReqVO, getLoginUserId());
        return success(true);
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得聊天对话列表", description = "管理端返回全部最近会话（含会员 App），便于审计价值截流等")
    @TransMethodResult
    public CommonResult<List<AiChatConversationRespVO>> getChatConversationMyList() {
        // 管理员：看全部（含 MEMBER App create-my）；不再按当前 admin userId 过滤
        List<AiChatConversationDO> list = chatConversationService.getChatConversationListAll(500);
        if (CollUtil.isEmpty(list)) {
            return success(Collections.emptyList());
        }
        Map<Long, Integer> messageCountMap = chatMessageService.getChatMessageCountMap(
                convertList(list, AiChatConversationDO::getId));
        return success(buildConversationRespList(list, messageCountMap));
    }

    @GetMapping("/get-my")
    @Operation(summary = "获得聊天对话", description = "管理端可查看任意用户会话")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    @TransMethodResult
    public CommonResult<AiChatConversationRespVO> getChatConversationMy(@RequestParam("id") Long id) {
        AiChatConversationDO conversation = chatConversationService.getChatConversation(id);
        if (conversation == null) {
            return success(null);
        }
        List<AiChatConversationRespVO> respList = buildConversationRespList(Collections.singletonList(conversation), null);
        return success(CollUtil.getFirst(respList));
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除聊天对话")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public CommonResult<Boolean> deleteChatConversationMy(@RequestParam("id") Long id) {
        chatConversationService.deleteChatConversationMy(id, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/delete-by-unpinned")
    @Operation(summary = "删除未置顶的聊天对话")
    public CommonResult<Boolean> deleteChatConversationMyByUnpinned() {
        chatConversationService.deleteChatConversationMyByUnpinned(getLoginUserId());
        return success(true);
    }

    // ========== 对话管理 ==========

    @GetMapping("/page")
    @Operation(summary = "获得对话分页", description = "用于【对话管理】菜单")
    @PreAuthorize("@ss.hasPermission('ai:chat-conversation:query')")
    @TransMethodResult
    public CommonResult<PageResult<AiChatConversationRespVO>> getChatConversationPage(AiChatConversationPageReqVO pageReqVO) {
        PageResult<AiChatConversationDO> pageResult = chatConversationService.getChatConversationPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }
        // 拼接关联数据
        Map<Long, Integer> messageCountMap = chatMessageService.getChatMessageCountMap(
                convertList(pageResult.getList(), AiChatConversationDO::getId));
        List<AiChatConversationRespVO> respList = buildConversationRespList(pageResult.getList(), messageCountMap);
        return success(new PageResult<>(respList, pageResult.getTotal()));
    }

    private List<AiChatConversationRespVO> buildConversationRespList(
            List<AiChatConversationDO> list, Map<Long, Integer> messageCountMap) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 1. 收集所有相关的 userId
        Set<Long> allUserIds = new HashSet<>();
        for (AiChatConversationDO conv : list) {
            if (conv.getUserId() != null) {
                allUserIds.add(conv.getUserId());
            }
        }

        // 2. 批量查询用户信息（同时查询 Admin 用户与 Member 会员用户，确保 App 会员与管理员都能准确匹配）
        Map<Long, AdminUserRespDTO> adminUserMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(allUserIds) && adminUserApi != null) {
            try {
                adminUserMap = adminUserApi.getUserMap(allUserIds);
            } catch (Exception ignored) {}
        }
        Map<Long, MemberUserRespDTO> memberUserMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(allUserIds) && memberUserApi != null) {
            try {
                memberUserMap = memberUserApi.getUserMap(allUserIds);
            } catch (Exception ignored) {}
        }

        // 3. 组装 VO 并绑定用户昵称与账号
        final Map<Long, AdminUserRespDTO> finalAdminMap = adminUserMap;
        final Map<Long, MemberUserRespDTO> finalMemberMap = memberUserMap;
        return BeanUtils.toBean(list, AiChatConversationRespVO.class, vo -> {
            if (messageCountMap != null) {
                vo.setMessageCount(messageCountMap.getOrDefault(vo.getId(), 0));
            }
            Long userId = vo.getUserId();
            if (userId == null) {
                return;
            }
            MemberUserRespDTO m = finalMemberMap.get(userId);
            AdminUserRespDTO a = finalAdminMap.get(userId);
            if (ObjUtil.equal(vo.getUserType(), 2) || (m != null && a == null)) {
                vo.setUserType(2);
                if (m != null) {
                    vo.setUserNickname(m.getNickname());
                    vo.setUserMobile(m.getMobile());
                    vo.setUserName(StrUtil.isNotBlank(m.getNickname()) ? m.getNickname() : m.getMobile());
                } else {
                    vo.setUserName("会员#" + userId);
                }
            } else if (a != null) {
                vo.setUserType(1);
                vo.setUserNickname(a.getNickname());
                vo.setUserMobile(a.getMobile());
                vo.setUserName(StrUtil.isNotBlank(a.getNickname()) ? a.getNickname() : a.getMobile());
            } else if (m != null) {
                vo.setUserType(2);
                vo.setUserNickname(m.getNickname());
                vo.setUserMobile(m.getMobile());
                vo.setUserName(StrUtil.isNotBlank(m.getNickname()) ? m.getNickname() : m.getMobile());
            } else {
                vo.setUserName("用户#" + userId);
            }
        });
    }

    @Operation(summary = "管理员删除对话")
    @DeleteMapping("/delete-by-admin")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:chat-conversation:delete')")
    public CommonResult<Boolean> deleteChatConversationByAdmin(@RequestParam("id") Long id) {
        chatConversationService.deleteChatConversationByAdmin(id);
        return success(true);
    }

}
