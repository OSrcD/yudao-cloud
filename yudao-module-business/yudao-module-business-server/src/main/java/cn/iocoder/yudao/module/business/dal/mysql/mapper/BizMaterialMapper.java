package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMaterialDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizMaterialMapper extends BaseMapperX<BizMaterialDO> {

    default PageResult<BizMaterialDO> selectPage(BizMaterialPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizMaterialDO>()
                .likeIfPresent(BizMaterialDO::getMaterialName, reqVO.getMaterialName())
                .eqIfPresent(BizMaterialDO::getFileType, reqVO.getFileType())
                .orderByDesc(BizMaterialDO::getId));
    }

}
