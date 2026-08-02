package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;

import java.util.List;

public interface XhsNoteCollectService {

    Boolean saveOrUpdateXhsNote(XhsNoteCollectSaveReqVO reqVO);

    Integer saveOrUpdateXhsNoteBatch(List<XhsNoteCollectSaveReqVO> list);

    void deleteXhsNoteCollect(Long id);

    void deleteXhsNoteCollectBatch(List<Long> ids);

    void updateAllNotesValid();

    XhsNoteCollectDO getXhsNoteCollect(Long id);

    PageResult<XhsNoteCollectDO> getXhsNoteCollectPage(XhsNoteCollectPageReqVO pageReqVO);

    void updateCommentStatus(String noteId);

    void updateLinkInvalid(String noteId);

    void updateMonitorStatus(List<Long> ids, Boolean isMonitored);

    PageResult<XhsNoteCollectDO> getMonitoredPage(XhsNoteCollectPageReqVO pageReqVO);

}
