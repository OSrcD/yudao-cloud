package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizPromptCommentMapper extends BaseMapperX<BizPromptCommentDO> {

    default PageResult<BizPromptCommentDO> selectPage(BizPromptCommentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizPromptCommentDO>()
                .eqIfPresent(BizPromptCommentDO::getPromptId, reqVO.getPromptId())
                .eqIfPresent(BizPromptCommentDO::getOperateGroupId, reqVO.getOperateGroupId())
                .eqIfPresent(BizPromptCommentDO::getTitle, reqVO.getTitle())
                .eqIfPresent(BizPromptCommentDO::getCommentContent, reqVO.getCommentContent())
                .orderByDesc(BizPromptCommentDO::getId));
    }

    List<BizPromptCommentDO> selectUnusedList(@Param("mediaAccountId") Long mediaAccountId, @Param("platform") Integer platform);

    List<BizPromptCommentDO> selectUnusedListByOneGroup(@Param("mediaAccountId") Long mediaAccountId, @Param("platform") Integer platform);

}
