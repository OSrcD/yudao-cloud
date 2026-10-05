package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsCommentRecordDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizXhsCommentRecordMapper extends BaseMapperX<BizXhsCommentRecordDO> {

    default PageResult<BizXhsCommentRecordDO> selectPage(BizXhsCommentRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizXhsCommentRecordDO>()
                .eqIfPresent(BizXhsCommentRecordDO::getAppAccountId, reqVO.getAppAccountId())
                .likeIfPresent(BizXhsCommentRecordDO::getAppMobile, reqVO.getAppMobile())
                .eqIfPresent(BizXhsCommentRecordDO::getXhsUserId, reqVO.getXhsUserId())
                .likeIfPresent(BizXhsCommentRecordDO::getXhsUserName, reqVO.getXhsUserName())
                .eqIfPresent(BizXhsCommentRecordDO::getNoteId, reqVO.getNoteId())
                .likeIfPresent(BizXhsCommentRecordDO::getNoteTitle, reqVO.getNoteTitle())
                .likeIfPresent(BizXhsCommentRecordDO::getShareLink, reqVO.getShareLink())
                .likeIfPresent(BizXhsCommentRecordDO::getCommentContent, reqVO.getCommentContent())
                .eqIfPresent(BizXhsCommentRecordDO::getCommentStatus, reqVO.getCommentStatus())
                .eqIfPresent(BizXhsCommentRecordDO::getCheckStatus, reqVO.getCheckStatus())
                .betweenIfPresent(BizXhsCommentRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BizXhsCommentRecordDO::getId));
    }

    /**
     * 判断当前App账号下是否已存在该作品记录
     * @param dedupMode 0仅对比链接(默认), 1仅对比标题, 2标题和链接同时对比
     */
    default boolean existsByAccount(Long appAccountId, String shareLink, String noteId, String noteTitle, Integer dedupMode) {
        if (appAccountId == null) {
            return false;
        }
        int mode = dedupMode != null ? dedupMode : 0;
        LambdaQueryWrapper<BizXhsCommentRecordDO> query = new LambdaQueryWrapper<BizXhsCommentRecordDO>()
                .eq(BizXhsCommentRecordDO::getAppAccountId, appAccountId);

        boolean hasLinkCondition = false;
        boolean hasTitleCondition = false;

        // 1. 仅对比标题
        if (mode == 1) {
            if (StrUtil.isBlank(noteTitle)) {
                return false;
            }
            query.eq(BizXhsCommentRecordDO::getNoteTitle, noteTitle.trim());
            return selectCount(query) > 0;
        }

        // 2. 标题和链接同时对比 (mode == 2)
        if (mode == 2) {
            if (StrUtil.isNotBlank(noteTitle)) {
                query.eq(BizXhsCommentRecordDO::getNoteTitle, noteTitle.trim());
                hasTitleCondition = true;
            }
            if (StrUtil.isNotBlank(noteId) && StrUtil.isNotBlank(shareLink)) {
                query.and(w -> w.eq(BizXhsCommentRecordDO::getNoteId, noteId.trim())
                        .or()
                        .eq(BizXhsCommentRecordDO::getShareLink, shareLink.trim()));
                hasLinkCondition = true;
            } else if (StrUtil.isNotBlank(noteId)) {
                query.eq(BizXhsCommentRecordDO::getNoteId, noteId.trim());
                hasLinkCondition = true;
            } else if (StrUtil.isNotBlank(shareLink)) {
                query.eq(BizXhsCommentRecordDO::getShareLink, shareLink.trim());
                hasLinkCondition = true;
            }
            if (!hasTitleCondition && !hasLinkCondition) {
                return false;
            }
            return selectCount(query) > 0;
        }

        // 0. 仅对比链接(默认模式)
        if (StrUtil.isNotBlank(noteId) && StrUtil.isNotBlank(shareLink)) {
            query.and(w -> w.eq(BizXhsCommentRecordDO::getNoteId, noteId.trim())
                    .or()
                    .eq(BizXhsCommentRecordDO::getShareLink, shareLink.trim()));
        } else if (StrUtil.isNotBlank(noteId)) {
            query.eq(BizXhsCommentRecordDO::getNoteId, noteId.trim());
        } else if (StrUtil.isNotBlank(shareLink)) {
            query.eq(BizXhsCommentRecordDO::getShareLink, shareLink.trim());
        } else {
            return false;
        }

        return selectCount(query) > 0;
    }

}
