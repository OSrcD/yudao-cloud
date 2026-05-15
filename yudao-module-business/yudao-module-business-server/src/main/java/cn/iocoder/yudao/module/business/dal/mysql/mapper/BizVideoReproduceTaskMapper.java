package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizVideoReproduceTaskMapper extends BaseMapperX<BizVideoReproduceTaskDO> {
    default List<BizVideoReproduceTaskDO> selectByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<BizVideoReproduceTaskDO>()
                .eq(BizVideoReproduceTaskDO::getCreator, userId));
    }
}
