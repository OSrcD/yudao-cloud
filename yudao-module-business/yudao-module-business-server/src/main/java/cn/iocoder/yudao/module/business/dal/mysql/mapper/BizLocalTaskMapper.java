package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizLocalTaskDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizLocalTaskMapper extends BaseMapperX<BizLocalTaskDO> {

    default PageResult<BizLocalTaskDO> selectPage(BizLocalTaskPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizLocalTaskDO>()
                .eqIfPresent(BizLocalTaskDO::getTaskType, reqVO.getTaskType())
                .eqIfPresent(BizLocalTaskDO::getStatus, reqVO.getStatus())
                .orderByDesc(BizLocalTaskDO::getId));
    }

}
