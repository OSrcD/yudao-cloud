package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompletePageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentCompleteDO;

import jakarta.validation.Valid;

/**
 * 已评论 Service 接口
 */
public interface BizPromptCommentCompleteService {

    /**
     * 创建已评论
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPromptCommentComplete(@Valid BizPromptCommentCompleteSaveReqVO createReqVO);

    /**
     * 更新已评论
     *
     * @param updateReqVO 更新信息
     */
    void updatePromptCommentComplete(@Valid BizPromptCommentCompleteSaveReqVO updateReqVO);

    /**
     * 删除已评论
     *
     * @param id 编号
     */
    void deletePromptCommentComplete(Long id);

    /**
     * 获得已评论
     *
     * @param id 编号
     * @return 已评论
     */
    BizPromptCommentCompleteDO getPromptCommentComplete(Long id);

    /**
     * 获得已评论分页
     *
     * @param pageReqVO 分页查询
     * @return 已评论分页
     */
    PageResult<BizPromptCommentCompleteRespVO> getPromptCommentCompletePage(BizPromptCommentCompletePageReqVO pageReqVO);

    /**
     * 获得待检查已评论分页
     */
    PageResult<BizPromptCommentCompleteRespVO> getCheckPageList(BizPromptCommentCompletePageReqVO pageReqVO);

    /**
     * 更新检测结果
     */
    Boolean updateCheckResult(BizPromptCommentCompletePageReqVO reqVO);

}

