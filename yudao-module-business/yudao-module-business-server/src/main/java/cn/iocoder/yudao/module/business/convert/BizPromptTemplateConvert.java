package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizPromptTemplateConvert {

    BizPromptTemplateConvert INSTANCE = Mappers.getMapper(BizPromptTemplateConvert.class);

    BizPromptTemplateDO convert(BizPromptTemplateSaveReqVO bean);

}
