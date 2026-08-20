package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XhsNoteCollectMapper extends BaseMapperX<XhsNoteCollectDO> {

    default PageResult<XhsNoteCollectDO> selectPage(XhsNoteCollectPageReqVO reqVO) {
        LambdaQueryWrapperX<XhsNoteCollectDO> query = new LambdaQueryWrapperX<XhsNoteCollectDO>()
                .likeIfPresent(XhsNoteCollectDO::getNoteId, reqVO.getNoteId())
                .likeIfPresent(XhsNoteCollectDO::getKeyword, reqVO.getKeyword())
                .eqIfPresent(XhsNoteCollectDO::getNoteType, reqVO.getNoteType())
                .likeIfPresent(XhsNoteCollectDO::getTitle, reqVO.getTitle())
                .likeIfPresent(XhsNoteCollectDO::getUserName, reqVO.getUserName())
                .likeIfPresent(XhsNoteCollectDO::getUserId, reqVO.getUserId())
                .likeIfPresent(XhsNoteCollectDO::getRedId, reqVO.getRedId())
                .eqIfPresent(XhsNoteCollectDO::getIsCommentCollected, reqVO.getIsCommentCollected())
                .eqIfPresent(XhsNoteCollectDO::getIsLinkInvalid, reqVO.getIsLinkInvalid())
                .eqIfPresent(XhsNoteCollectDO::getIsMonitored, reqVO.getIsMonitored())
                .geIfPresent(XhsNoteCollectDO::getCommentsCount, reqVO.getCommentsCountMin())
                .betweenIfPresent(XhsNoteCollectDO::getCreateTime, reqVO.getCreateTime());

        if (Boolean.TRUE.equals(reqVO.getPcShareLinkEmpty())) {
            query.and(q -> q.isNull(XhsNoteCollectDO::getPcShareLink)
                            .or().eq(XhsNoteCollectDO::getPcShareLink, "")
                            .or().eq(XhsNoteCollectDO::getIsLinkInvalid, true));
        }

        if (Boolean.TRUE.equals(reqVO.getPcShareLinkValid())) {
            query.like(XhsNoteCollectDO::getPcShareLink, "😆");
        }

        if (reqVO.getIsCommentCollected() != null) {
            query.eq(XhsNoteCollectDO::getIsCommentCollected, reqVO.getIsCommentCollected());
        }

        if (reqVO.getIsMonitored() != null) {
            query.eq(XhsNoteCollectDO::getIsMonitored, reqVO.getIsMonitored());
        }

        if (Boolean.TRUE.equals(reqVO.getForCommentScrape())) {
            // 评论采集改走 token / noteUrl，不要求 PC 分享链接，也不限制评论数
            query.orderByAsc(XhsNoteCollectDO::getLastCommentCollectTime);
            query.orderByAsc(XhsNoteCollectDO::getId);
            return selectPage(reqVO, query);
        }

        boolean hasMultiSort = false;
        if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getSortFields())) {
            String[] items = reqVO.getSortFields().split(",");
            for (String item : items) {
                if (cn.hutool.core.util.StrUtil.isBlank(item)) continue;
                String[] pair = item.trim().split(":");
                String field = pair[0].trim();
                boolean isAsc = pair.length > 1 && "asc".equalsIgnoreCase(pair[1].trim());

                if ("publishTime".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCollectDO::getPublishTime);
                    hasMultiSort = true;
                } else if ("likedCount".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCollectDO::getLikedCount);
                    hasMultiSort = true;
                } else if ("collectedCount".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCollectDO::getCollectedCount);
                    hasMultiSort = true;
                } else if ("commentsCount".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCollectDO::getCommentsCount);
                    hasMultiSort = true;
                } else if ("lastCommentCollectTime".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCollectDO::getLastCommentCollectTime);
                    hasMultiSort = true;
                } else if ("lastPcShareCollectTime".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCollectDO::getLastPcShareCollectTime);
                    hasMultiSort = true;
                }
            }
            if (hasMultiSort) {
                query.orderByDesc(XhsNoteCollectDO::getId);
            }
        }

        if (!hasMultiSort) {
            boolean isAsc = false;
            if (reqVO.getSortOrder() != null) {
                isAsc = "asc".equalsIgnoreCase(reqVO.getSortOrder());
            } else if (reqVO.getPublishTimeAsc() != null) {
                isAsc = Boolean.TRUE.equals(reqVO.getPublishTimeAsc());
            }

            if ("likedCount".equals(reqVO.getSortField())) {
                query.orderBy(true, isAsc, XhsNoteCollectDO::getLikedCount);
            } else if ("collectedCount".equals(reqVO.getSortField())) {
                query.orderBy(true, isAsc, XhsNoteCollectDO::getCollectedCount);
            } else if ("commentsCount".equals(reqVO.getSortField())) {
                query.orderBy(true, isAsc, XhsNoteCollectDO::getCommentsCount);
            } else if ("lastCommentCollectTime".equals(reqVO.getSortField())) {
                query.orderBy(true, isAsc, XhsNoteCollectDO::getLastCommentCollectTime);
            } else if ("lastPcShareCollectTime".equals(reqVO.getSortField())) {
                query.orderBy(true, isAsc, XhsNoteCollectDO::getLastPcShareCollectTime);
            } else if ("publishTime".equals(reqVO.getSortField()) || reqVO.getPublishTimeAsc() != null) {
                query.orderBy(true, isAsc, XhsNoteCollectDO::getPublishTime);
            } else {
                query.orderByDesc(XhsNoteCollectDO::getId);
            }
        }

        System.out.println("====== DEBUG SORT FIELDS ======");
        System.out.println("SortFields value: " + reqVO.getSortFields());
        System.out.println("hasMultiSort: " + hasMultiSort);
        System.out.println("SQL Segment: " + query.getSqlSegment());
        System.out.println("===============================");

        return selectPage(reqVO, query);
    }

}
