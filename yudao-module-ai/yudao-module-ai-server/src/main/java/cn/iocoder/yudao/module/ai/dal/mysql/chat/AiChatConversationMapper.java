package cn.iocoder.yudao.module.ai.dal.mysql.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.conversation.AiChatConversationPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 聊天对话 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiChatConversationMapper extends BaseMapperX<AiChatConversationDO> {

    default List<AiChatConversationDO> selectListByUserId(Long userId) {
        return selectList(AiChatConversationDO::getUserId, userId,
                AiChatConversationDO::getUserType, cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserType());
    }

    default List<AiChatConversationDO> selectListByUserIdAndPinned(Long userId, boolean pinned) {
        return selectList(new LambdaQueryWrapperX<AiChatConversationDO>()
                .eq(AiChatConversationDO::getUserId, userId)
                .eq(AiChatConversationDO::getUserType, cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserType())
                .eq(AiChatConversationDO::getPinned, pinned));
    }

    /**
     * 管理端：最近全部会话（含管理员 + 会员 App），不按 userType 过滤
     */
    default List<AiChatConversationDO> selectListRecent(int limit) {
        return selectList(new LambdaQueryWrapperX<AiChatConversationDO>()
                .orderByDesc(AiChatConversationDO::getId)
                .last("LIMIT " + Math.max(1, Math.min(limit, 1000))));
    }

    default PageResult<AiChatConversationDO> selectChatConversationPage(AiChatConversationPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<AiChatConversationDO>()
                .eqIfPresent(AiChatConversationDO::getUserId, pageReqVO.getUserId())
                .likeIfPresent(AiChatConversationDO::getTitle, pageReqVO.getTitle())
                .betweenIfPresent(AiChatConversationDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(AiChatConversationDO::getId));
    }

}
