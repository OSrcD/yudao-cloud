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
        return selectListByUserId(userId, 100);
    }

    /**
     * 【向下兼容安全查询】：按用户查询会话，按时间倒序排序并加入安全上限（默认 100 条，最大 500 条）
     * 避免因历史产生上万条会话时全表全量加载导致 OOM、数据库卡顿与移动端网络 60s 超时
     */
    default List<AiChatConversationDO> selectListByUserId(Long userId, Integer limit) {
        Integer userType = null;
        cn.iocoder.yudao.framework.security.core.LoginUser loginUser = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null && loginUser.getUserType() != null) {
            userType = loginUser.getUserType();
        } else {
            userType = cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserType();
        }
        LambdaQueryWrapperX<AiChatConversationDO> wrapper = new LambdaQueryWrapperX<AiChatConversationDO>()
                .eq(AiChatConversationDO::getUserId, userId)
                .orderByDesc(AiChatConversationDO::getId);
        if (userType != null) {
            wrapper.eq(AiChatConversationDO::getUserType, userType);
        }
        int maxLimit = (limit != null && limit > 0) ? Math.min(limit, 500) : 100;
        wrapper.last("LIMIT " + maxLimit);
        return selectList(wrapper);
    }

    default List<AiChatConversationDO> selectListByUserIdAndPinned(Long userId, boolean pinned) {
        Integer userType = null;
        cn.iocoder.yudao.framework.security.core.LoginUser loginUser = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser();
        if (loginUser != null && loginUser.getUserType() != null) {
            userType = loginUser.getUserType();
        } else {
            userType = cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserType();
        }
        LambdaQueryWrapperX<AiChatConversationDO> wrapper = new LambdaQueryWrapperX<AiChatConversationDO>()
                .eq(AiChatConversationDO::getUserId, userId)
                .eq(AiChatConversationDO::getPinned, pinned);
        if (userType != null) {
            wrapper.eq(AiChatConversationDO::getUserType, userType);
        }
        return selectList(wrapper);
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
