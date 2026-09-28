package cn.iocoder.yudao.module.member.dal.mysql.vip;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipOrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberVipOrderMapper extends BaseMapperX<MemberVipOrderDO> {

    default int updateByIdAndPayed(Long id, boolean wherePayed, MemberVipOrderDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<MemberVipOrderDO>()
                .eq(MemberVipOrderDO::getId, id)
                .eq(MemberVipOrderDO::getPayStatus, wherePayed));
    }

}
