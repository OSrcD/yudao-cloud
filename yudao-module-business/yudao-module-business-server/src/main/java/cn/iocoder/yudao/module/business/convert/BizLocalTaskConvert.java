package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizLocalTaskDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizLocalTaskConvert {

    BizLocalTaskConvert INSTANCE = Mappers.getMapper(BizLocalTaskConvert.class);

    @org.mapstruct.Mapping(target = "execParams", ignore = true)
    @org.mapstruct.Mapping(target = "resultData", ignore = true)
    BizLocalTaskDO convert(BizLocalTaskSaveReqVO bean);

}
