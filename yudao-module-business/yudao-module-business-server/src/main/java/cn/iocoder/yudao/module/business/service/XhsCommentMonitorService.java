package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorKeywordDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;

import java.util.List;

/**
 * 小红书评论监控服务
 */
public interface XhsCommentMonitorService {

    /**
     * 获取评论监控分页（已监控笔记范围 + 关键词过滤 + 临时搜索）
     */
    PageResult<XhsNoteCommentDO> getCommentMonitorPage(XhsCommentMonitorPageReqVO reqVO);

    /**
     * 获取所有监控关键词
     */
    List<XhsCommentMonitorKeywordDO> getKeywords();

    /**
     * 新增关键词
     */
    XhsCommentMonitorKeywordDO addKeyword(String keyword);

    /**
     * 删除关键词
     */
    void deleteKeyword(Long id);

    /**
     * 获取已监控笔记的 noteId 列表
     */
    List<String> getMonitoredNoteIds();

    /**
     * 获取关键词字符串列表（供定时任务调用）
     */
    List<String> getKeywordStrings();

}
