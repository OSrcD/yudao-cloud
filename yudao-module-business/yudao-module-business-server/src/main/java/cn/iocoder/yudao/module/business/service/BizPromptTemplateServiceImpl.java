package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizPromptTemplateConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptTemplateDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptTemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 提示词模板 Service 实现类
 */
@Service
@Validated
public class BizPromptTemplateServiceImpl implements BizPromptTemplateService {

    @Resource
    private BizPromptTemplateMapper promptTemplateMapper;

    @Override
    public Long createPromptTemplate(BizPromptTemplateSaveReqVO createReqVO) {
        BizPromptTemplateDO promptTemplate = BizPromptTemplateConvert.INSTANCE.convert(createReqVO);
        promptTemplateMapper.insert(promptTemplate);
        return promptTemplate.getId();
    }

    @Override
    public void updatePromptTemplate(BizPromptTemplateSaveReqVO updateReqVO) {
        BizPromptTemplateDO updateObj = BizPromptTemplateConvert.INSTANCE.convert(updateReqVO);
        promptTemplateMapper.updateById(updateObj);
    }

    @Override
    public void deletePromptTemplate(Long id) {
        promptTemplateMapper.deleteById(id);
    }

    @Override
    public BizPromptTemplateDO getPromptTemplate(Long id) {
        return promptTemplateMapper.selectById(id);
    }

    @Override
    public PageResult<BizPromptTemplateDO> getPromptTemplatePage(BizPromptTemplatePageReqVO pageReqVO) {
        return promptTemplateMapper.selectPage(pageReqVO);
    }

}

