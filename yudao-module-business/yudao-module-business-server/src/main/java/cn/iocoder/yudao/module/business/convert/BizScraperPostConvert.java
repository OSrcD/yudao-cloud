package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizScraperPostDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizScraperPostConvert {

    BizScraperPostConvert INSTANCE = Mappers.getMapper(BizScraperPostConvert.class);

    BizScraperPostDO convert(BizScraperPostSaveReqVO bean);

}
