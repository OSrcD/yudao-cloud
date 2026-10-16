package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsOfficialAccountDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BizXhsOfficialAccountConvert {

    BizXhsOfficialAccountConvert INSTANCE = Mappers.getMapper(BizXhsOfficialAccountConvert.class);

    BizXhsOfficialAccountDO convert(BizXhsOfficialAccountSaveReqVO bean);

    BizXhsOfficialAccountRespVO convert(BizXhsOfficialAccountDO bean);

    List<BizXhsOfficialAccountRespVO> convertList(List<BizXhsOfficialAccountDO> list);

    PageResult<BizXhsOfficialAccountRespVO> convertPage(PageResult<BizXhsOfficialAccountDO> page);

}
