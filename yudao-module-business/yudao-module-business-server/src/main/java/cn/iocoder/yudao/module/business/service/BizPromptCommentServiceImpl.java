package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizPromptCommentConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptCommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 提示词评论 Service 实现类
 */
@Service
@Validated
public class BizPromptCommentServiceImpl implements BizPromptCommentService {

    @Resource
    private BizPromptCommentMapper promptCommentMapper;

    @Override
    public Boolean createPromptCommentBatch(java.util.List<BizPromptCommentSaveReqVO> createReqVOs) {
        java.util.List<BizPromptCommentDO> list = new java.util.ArrayList<>();
        for (BizPromptCommentSaveReqVO reqVO : createReqVOs) {
            list.add(BizPromptCommentConvert.INSTANCE.convert(reqVO));
        }
        promptCommentMapper.insertBatch(list);
        return true;
    }

    @Override
    public Long createPromptComment(BizPromptCommentSaveReqVO createReqVO) {
        BizPromptCommentDO promptComment = BizPromptCommentConvert.INSTANCE.convert(createReqVO);
        promptCommentMapper.insert(promptComment);
        return promptComment.getId();
    }

    @Override
    public void updatePromptComment(BizPromptCommentSaveReqVO updateReqVO) {
        BizPromptCommentDO updateObj = BizPromptCommentConvert.INSTANCE.convert(updateReqVO);
        promptCommentMapper.updateById(updateObj);
    }

    @Override
    public void deletePromptComment(Long id) {
        promptCommentMapper.deleteById(id);
    }

    @Override
    public BizPromptCommentDO getPromptComment(Long id) {
        return promptCommentMapper.selectById(id);
    }

    @Override
    public PageResult<BizPromptCommentDO> getPromptCommentPage(BizPromptCommentPageReqVO pageReqVO) {
        return promptCommentMapper.selectPage(pageReqVO);
    }

    @Override
    public java.util.List<BizPromptCommentDO> queryUnusedList(Long mediaAccountId, Integer platform) {
        return promptCommentMapper.selectUnusedList(mediaAccountId, platform);
    }

    @Override
    public java.util.List<BizPromptCommentDO> getNextAvailableGroup(Long mediaAccountId, Integer platform) {
        return promptCommentMapper.selectUnusedListByOneGroup(mediaAccountId, platform);
    }

}

