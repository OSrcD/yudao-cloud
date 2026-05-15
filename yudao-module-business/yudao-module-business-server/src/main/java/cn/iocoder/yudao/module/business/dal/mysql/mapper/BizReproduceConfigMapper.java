package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizReproduceConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizReproduceConfigMapper extends BaseMapperX<BizReproduceConfigDO> {

    default PageResult<BizReproduceConfigDO> selectPage(BizReproduceConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizReproduceConfigDO>()
                .likeIfPresent(BizReproduceConfigDO::getConfigName, reqVO.getConfigName())
                .orderByDesc(BizReproduceConfigDO::getId));
    }

}
