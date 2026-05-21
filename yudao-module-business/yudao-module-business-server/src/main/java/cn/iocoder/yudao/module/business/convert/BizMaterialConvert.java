package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMaterialDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizMaterialConvert {

    BizMaterialConvert INSTANCE = Mappers.getMapper(BizMaterialConvert.class);

    BizMaterialDO convert(BizMaterialSaveReqVO bean);

}
