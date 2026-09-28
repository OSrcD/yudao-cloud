package cn.iocoder.yudao.module.member.dal.mysql.vip;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipPackageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberVipPackageMapper extends BaseMapperX<MemberVipPackageDO> {

    default List<MemberVipPackageDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<MemberVipPackageDO>()
                .eq(MemberVipPackageDO::getStatus, status)
                .orderByAsc(MemberVipPackageDO::getSort)
                .orderByAsc(MemberVipPackageDO::getId));
    }

}
