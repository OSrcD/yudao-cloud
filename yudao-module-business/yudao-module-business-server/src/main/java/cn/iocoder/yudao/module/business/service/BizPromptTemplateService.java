package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptTemplateDO;

import jakarta.validation.Valid;

/**
 * 提示词模板 Service 接口
 */
public interface BizPromptTemplateService {

    /**
     * 创建提示词模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPromptTemplate(@Valid BizPromptTemplateSaveReqVO createReqVO);

    /**
     * 更新提示词模板
     *
     * @param updateReqVO 更新信息
     */
    void updatePromptTemplate(@Valid BizPromptTemplateSaveReqVO updateReqVO);

    /**
     * 删除提示词模板
     *
     * @param id 编号
     */
    void deletePromptTemplate(Long id);

    /**
     * 获得提示词模板
     *
     * @param id 编号
     * @return 提示词模板
     */
    BizPromptTemplateDO getPromptTemplate(Long id);

    /**
     * 获得提示词模板分页
     *
     * @param pageReqVO 分页查询
     * @return 提示词模板分页
     */
    PageResult<BizPromptTemplateDO> getPromptTemplatePage(BizPromptTemplatePageReqVO pageReqVO);

}

