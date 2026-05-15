package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompletePageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizPromptCommentCompleteConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentCompleteDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptCommentCompleteMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptCommentMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 已评论 Service 实现类
 */
@Service
@Validated
public class BizPromptCommentCompleteServiceImpl implements BizPromptCommentCompleteService {

    @Resource
    private BizPromptCommentCompleteMapper promptCommentCompleteMapper;

    @Resource
    private BizPromptCommentMapper promptCommentMapper;

    @Override
    public Long createPromptCommentComplete(BizPromptCommentCompleteSaveReqVO createReqVO) {
        BizPromptCommentCompleteDO promptCommentComplete = BizPromptCommentCompleteConvert.INSTANCE.convert(createReqVO);
        promptCommentCompleteMapper.insert(promptCommentComplete);
        return promptCommentComplete.getId();
    }

    @Override
    public void updatePromptCommentComplete(BizPromptCommentCompleteSaveReqVO updateReqVO) {
        BizPromptCommentCompleteDO updateObj = BizPromptCommentCompleteConvert.INSTANCE.convert(updateReqVO);
        promptCommentCompleteMapper.updateById(updateObj);
    }

    @Override
    public void deletePromptCommentComplete(Long id) {
        promptCommentCompleteMapper.deleteById(id);
    }

    @Override
    public BizPromptCommentCompleteDO getPromptCommentComplete(Long id) {
        return promptCommentCompleteMapper.selectById(id);
    }

    @Override
    public PageResult<BizPromptCommentCompleteRespVO> getPromptCommentCompletePage(BizPromptCommentCompletePageReqVO pageReqVO) {
        PageResult<BizPromptCommentCompleteDO> pageResult = promptCommentCompleteMapper.selectPage(pageReqVO);
        return new PageResult<>(BizPromptCommentCompleteConvert.INSTANCE.convertList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public PageResult<BizPromptCommentCompleteRespVO> getCheckPageList(BizPromptCommentCompletePageReqVO pageReqVO) {
        IPage<BizPromptCommentCompleteDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<BizPromptCommentCompleteDO> resultPage = promptCommentCompleteMapper.selectCheckList(page, pageReqVO);
        PageResult<BizPromptCommentCompleteRespVO> pageResult = new PageResult<>(BizPromptCommentCompleteConvert.INSTANCE.convertList(resultPage.getRecords()), resultPage.getTotal());
        
        Pattern pattern = Pattern.compile("https?://[a-zA-Z0-9./]+");
        for (BizPromptCommentCompleteRespVO vo : pageResult.getList()) {
            if (vo.getXhsNoteInfo() != null) {
                Matcher matcher = pattern.matcher(vo.getXhsNoteInfo());
                if (matcher.find()) {
                    vo.setNoteUrl(matcher.group());
                }
            }
        }
        return pageResult;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public Boolean updateCheckResult(BizPromptCommentCompletePageReqVO reqVO) {
        // 1. 更新 biz_prompt_comment_complete 表的 check_status
        BizPromptCommentCompleteDO complete = new BizPromptCommentCompleteDO();
        complete.setId(reqVO.getId());
        complete.setCheckStatus(1);
        if (reqVO.getCommentStatus() != null) {
            complete.setCommentStatus(reqVO.getCommentStatus());
        }
        int updateComplete = promptCommentCompleteMapper.updateById(complete);

        // 2. 更新 biz_prompt_comment 表的计数
        if (reqVO.getCommentId() != null) {
            BizPromptCommentDO comment = promptCommentMapper.selectById(reqVO.getCommentId());
            if (comment != null) {
                if (Integer.valueOf(1).equals(reqVO.getCommentStatus())) {
                    // 吞评
                    comment.setXhsInterceptCount((comment.getXhsInterceptCount() == null ? 0 : comment.getXhsInterceptCount()) + 1);
                } else {
                    // 正常
                    comment.setXhsNormalCount((comment.getXhsNormalCount() == null ? 0 : comment.getXhsNormalCount()) + 1);
                }
                promptCommentMapper.updateById(comment);
            }
        }
        return updateComplete > 0;
    }

}
