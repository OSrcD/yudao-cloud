package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizPromptCommentConvert {

    BizPromptCommentConvert INSTANCE = Mappers.getMapper(BizPromptCommentConvert.class);

    BizPromptCommentDO convert(BizPromptCommentSaveReqVO bean);

}
