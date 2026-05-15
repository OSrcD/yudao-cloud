package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMediaAccountDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizMediaAccountMapper extends BaseMapperX<BizMediaAccountDO> {

    default PageResult<BizMediaAccountDO> selectPage(BizMediaAccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizMediaAccountDO>()
                .likeIfPresent(BizMediaAccountDO::getAccountName, reqVO.getAccountName())
                .eqIfPresent(BizMediaAccountDO::getAccountType, reqVO.getAccountType())
                .orderByDesc(BizMediaAccountDO::getId));
    }

}
