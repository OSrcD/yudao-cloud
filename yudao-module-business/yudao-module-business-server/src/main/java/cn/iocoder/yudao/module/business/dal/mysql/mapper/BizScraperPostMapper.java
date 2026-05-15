package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizScraperPostDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BizScraperPostMapper extends BaseMapperX<BizScraperPostDO> {

    default PageResult<BizScraperPostDO> selectPage(BizScraperPostPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizScraperPostDO>()
                .likeIfPresent(BizScraperPostDO::getTitle, reqVO.getTitle())
                .eqIfPresent(BizScraperPostDO::getPostId, reqVO.getPostId())
                .orderByDesc(BizScraperPostDO::getId));
    }

}
