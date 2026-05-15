package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMediaAccountDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizMediaAccountConvert {

    BizMediaAccountConvert INSTANCE = Mappers.getMapper(BizMediaAccountConvert.class);

    BizMediaAccountDO convert(BizMediaAccountSaveReqVO bean);

}
