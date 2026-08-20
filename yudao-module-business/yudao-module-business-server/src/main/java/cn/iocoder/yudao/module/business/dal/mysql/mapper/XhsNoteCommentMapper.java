package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XhsNoteCommentMapper extends BaseMapperX<XhsNoteCommentDO> {

    default PageResult<XhsNoteCommentDO> selectPage(XhsNoteCommentPageReqVO reqVO) {
        LambdaQueryWrapperX<XhsNoteCommentDO> query = new LambdaQueryWrapperX<XhsNoteCommentDO>()
                .eqIfPresent(XhsNoteCommentDO::getNoteId, reqVO.getNoteId())
                .eqIfPresent(XhsNoteCommentDO::getCommentId, reqVO.getCommentId())
                .eqIfPresent(XhsNoteCommentDO::getUserId, reqVO.getUserId())
                .likeIfPresent(XhsNoteCommentDO::getNickname, reqVO.getNickname())
                .likeIfPresent(XhsNoteCommentDO::getRedId, reqVO.getRedId())
                .likeIfPresent(XhsNoteCommentDO::getContent, reqVO.getContent())
                .likeIfPresent(XhsNoteCommentDO::getIpLocation, reqVO.getIpLocation())
                .eqIfPresent(XhsNoteCommentDO::getInterceptStatus, reqVO.getInterceptStatus())
                .eqIfPresent(XhsNoteCommentDO::getIsAuthor, reqVO.getIsAuthor());

        boolean hasMultiSort = false;
        if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getSortFields())) {
            String[] items = reqVO.getSortFields().split(",");
            for (String item : items) {
                if (cn.hutool.core.util.StrUtil.isBlank(item)) continue;
                String[] pair = item.trim().split(":");
                String field = pair[0].trim();
                boolean isAsc = pair.length > 1 && "asc".equalsIgnoreCase(pair[1].trim());

                if ("commentTime".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCommentDO::getCommentTime);
                    hasMultiSort = true;
                } else if ("createTime".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCommentDO::getCreateTime);
                    hasMultiSort = true;
                }
            }
        }
        if (!hasMultiSort) {
            query.orderByDesc(XhsNoteCommentDO::getId);
        }
        return selectPage(reqVO, query);
    }

    /**
     * 评论监控分页查询：
     * - 限定在已监控笔记范围内（monitoredNoteIds）
     * - 若 keywords 非空，则只返回内容命中任意关键词的评论
     * - 支持临时搜索（keyword 匹配内容或昵称，nickname 精确模糊，noteId 精确）
     */
    default PageResult<XhsNoteCommentDO> selectMonitorPage(XhsCommentMonitorPageReqVO reqVO,
                                                            List<String> monitoredNoteIds,
                                                            List<String> keywords) {
        if (monitoredNoteIds == null || monitoredNoteIds.isEmpty()) {
            return PageResult.empty();
        }

        LambdaQueryWrapperX<XhsNoteCommentDO> query = new LambdaQueryWrapperX<XhsNoteCommentDO>()
                .in(XhsNoteCommentDO::getNoteId, monitoredNoteIds);

        // 持久化关键词过滤（命中任意一个即显示）
        if (keywords != null && !keywords.isEmpty()) {
            query.and(q -> {
                for (int i = 0; i < keywords.size(); i++) {
                    String kw = keywords.get(i);
                    if (i == 0) {
                        q.like(XhsNoteCommentDO::getContent, kw);
                    } else {
                        q.or().like(XhsNoteCommentDO::getContent, kw);
                    }
                }
            });
        }

        // 临时搜索
        if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getNoteId())) {
            query.eq(XhsNoteCommentDO::getNoteId, reqVO.getNoteId());
        }
        if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getNickname())) {
            query.like(XhsNoteCommentDO::getNickname, reqVO.getNickname());
        }
        if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getKeyword())) {
            query.and(q -> q.like(XhsNoteCommentDO::getContent, reqVO.getKeyword())
                    .or().like(XhsNoteCommentDO::getNickname, reqVO.getKeyword()));
        }

        // 排序
        boolean hasSort = false;
        if (cn.hutool.core.util.StrUtil.isNotBlank(reqVO.getSortFields())) {
            String[] items = reqVO.getSortFields().split(",");
            for (String item : items) {
                if (cn.hutool.core.util.StrUtil.isBlank(item)) continue;
                String[] pair = item.trim().split(":");
                String field = pair[0].trim();
                boolean isAsc = pair.length > 1 && "asc".equalsIgnoreCase(pair[1].trim());
                if ("commentTime".equals(field)) {
                    query.orderBy(true, isAsc, XhsNoteCommentDO::getCommentTime);
                    hasSort = true;
                }
            }
        }
        if (!hasSort) {
            query.orderByDesc(XhsNoteCommentDO::getCommentTime);
        }

        return selectPage(reqVO, query);
    }

    /**
     * 查询指定笔记在指定时间之后新增的评论（供定时任务扫描使用）
     */
    default List<XhsNoteCommentDO> selectRecentByNoteIds(List<String> noteIds, java.time.LocalDateTime since) {
        if (noteIds == null || noteIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapper<XhsNoteCommentDO>()
                .in(XhsNoteCommentDO::getNoteId, noteIds)
                .ge(XhsNoteCommentDO::getCreateTime, since));
    }

}
