package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;

import jakarta.validation.Valid;

/**
 * 提示词评论 Service 接口
 */
public interface BizPromptCommentService {

    /**
     * 批量创建提示词评论
     *
     * @param createReqVOs 创建信息列表
     * @return 是否成功
     */
    Boolean createPromptCommentBatch(java.util.List<BizPromptCommentSaveReqVO> createReqVOs);

    /**
     * 创建提示词评论
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPromptComment(@Valid BizPromptCommentSaveReqVO createReqVO);

    /**
     * 更新提示词评论
     *
     * @param updateReqVO 更新信息
     */
    void updatePromptComment(@Valid BizPromptCommentSaveReqVO updateReqVO);

    /**
     * 删除提示词评论
     *
     * @param id 编号
     */
    void deletePromptComment(Long id);

    /**
     * 获得提示词评论
     *
     * @param id 编号
     * @return 提示词评论
     */
    BizPromptCommentDO getPromptComment(Long id);

    /**
     * 获得提示词评论分页
     *
     * @param pageReqVO 分页查询
     * @return 提示词评论分页
     */
    PageResult<BizPromptCommentDO> getPromptCommentPage(BizPromptCommentPageReqVO pageReqVO);

    /**
     * 查询未使用的提示词评论列表
     */
    java.util.List<BizPromptCommentDO> queryUnusedList(Long mediaAccountId, Integer platform);

    /**
     * 获取下一组可用的提示词评论
     */
    java.util.List<BizPromptCommentDO> getNextAvailableGroup(Long mediaAccountId, Integer platform);

}

