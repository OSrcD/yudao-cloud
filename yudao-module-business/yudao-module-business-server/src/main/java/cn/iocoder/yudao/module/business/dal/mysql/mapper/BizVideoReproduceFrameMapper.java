package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceFrameDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizVideoReproduceFrameMapper extends BaseMapperX<BizVideoReproduceFrameDO> {

    default List<BizVideoReproduceFrameDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<BizVideoReproduceFrameDO>()
                .eq(BizVideoReproduceFrameDO::getTaskId, taskId));
    }

}
