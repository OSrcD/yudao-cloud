package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptTemplateDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizPromptTemplateMapper extends BaseMapperX<BizPromptTemplateDO> {

    default PageResult<BizPromptTemplateDO> selectPage(BizPromptTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizPromptTemplateDO>()
                .eqIfPresent(BizPromptTemplateDO::getTemplateType, reqVO.getTemplateType())
                .orderByDesc(BizPromptTemplateDO::getId));
    }

}
