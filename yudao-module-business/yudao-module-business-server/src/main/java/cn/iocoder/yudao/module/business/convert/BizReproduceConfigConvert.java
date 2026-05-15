package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizReproduceConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizReproduceConfigConvert {

    BizReproduceConfigConvert INSTANCE = Mappers.getMapper(BizReproduceConfigConvert.class);

    BizReproduceConfigDO convert(BizReproduceConfigSaveReqVO bean);

}
