package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentUpdateStatusReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;

import java.util.List;

/**
 * 小红书笔记评论 Service 接口
 */
public interface XhsNoteCommentService {

    /**
     * 保存或更新单条评论 (通过 commentId 去重)
     */
    Boolean saveOrUpdateXhsNoteComment(XhsNoteCommentSaveReqVO reqVO);

    /**
     * 批量保存或更新评论 (通过 commentId 去重)
     */
    Integer saveOrUpdateXhsNoteCommentBatch(List<XhsNoteCommentSaveReqVO> reqVOList);

    /**
     * 更新私信截流跟进状态及备注
     */
    Boolean updateInterceptStatus(XhsNoteCommentUpdateStatusReqVO reqVO);

    /**
     * 获得小红书笔记评论分页列表
     */
    PageResult<XhsNoteCommentDO> getXhsNoteCommentPage(XhsNoteCommentPageReqVO reqVO);

    /**
     * 根据笔记ID获取所有评论列表
     */
    List<XhsNoteCommentDO> getCommentListByNoteId(String noteId);

    /**
     * 删除评论
     */
    void deleteXhsNoteComment(Long id);

}
