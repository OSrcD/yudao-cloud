package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsOfficialAccountDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizXhsOfficialAccountMapper extends BaseMapperX<BizXhsOfficialAccountDO> {

    default PageResult<BizXhsOfficialAccountDO> selectPage(BizXhsOfficialAccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizXhsOfficialAccountDO>()
                .eqIfPresent(BizXhsOfficialAccountDO::getUserId, reqVO.getUserId())
                .likeIfPresent(BizXhsOfficialAccountDO::getMobile, reqVO.getMobile())
                .likeIfPresent(BizXhsOfficialAccountDO::getAccountId, reqVO.getAccountId())
                .likeIfPresent(BizXhsOfficialAccountDO::getNickname, reqVO.getNickname())
                .likeIfPresent(BizXhsOfficialAccountDO::getRemark, reqVO.getRemark())
                .eqIfPresent(BizXhsOfficialAccountDO::getStatus, reqVO.getStatus())
                .orderByDesc(BizXhsOfficialAccountDO::getId));
    }

    default BizXhsOfficialAccountDO selectByUserIdAndAccountId(Long userId, String accountId) {
        LambdaQueryWrapperX<BizXhsOfficialAccountDO> wrapper = new LambdaQueryWrapperX<BizXhsOfficialAccountDO>()
                .eq(BizXhsOfficialAccountDO::getAccountId, accountId);
        if (userId != null) {
            wrapper.eq(BizXhsOfficialAccountDO::getUserId, userId);
        }
        return selectOne(wrapper.last("LIMIT 1"));
    }

    default BizXhsOfficialAccountDO selectByAccountId(String accountId) {
        return selectOne(new LambdaQueryWrapperX<BizXhsOfficialAccountDO>()
                .eq(BizXhsOfficialAccountDO::getAccountId, accountId)
                .last("LIMIT 1"));
    }

    default List<BizXhsOfficialAccountDO> selectListByUserIdAndStatus(Long userId, Integer status) {
        LambdaQueryWrapperX<BizXhsOfficialAccountDO> wrapper = new LambdaQueryWrapperX<BizXhsOfficialAccountDO>()
                .eqIfPresent(BizXhsOfficialAccountDO::getStatus, status);
        if (userId != null) {
            wrapper.eq(BizXhsOfficialAccountDO::getUserId, userId);
        }
        return selectList(wrapper.orderByDesc(BizXhsOfficialAccountDO::getId));
    }

    default List<BizXhsOfficialAccountDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<BizXhsOfficialAccountDO>()
                .eqIfPresent(BizXhsOfficialAccountDO::getStatus, status)
                .orderByDesc(BizXhsOfficialAccountDO::getId));
    }

}
