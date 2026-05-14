package cn.iocoder.yudao.module.ai.dal.mysql.video;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.video.BizAiVideoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 视频 Mapper
 *
 * @author CuiMa
 */
@Mapper
public interface BizAiVideoMapper extends BaseMapperX<BizAiVideoDO> {

    default PageResult<BizAiVideoDO> selectPage(BizAiVideoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizAiVideoDO>()
                .eqIfPresent(BizAiVideoDO::getUserId, reqVO.getUserId())
                .likeIfPresent(BizAiVideoDO::getPrompt, reqVO.getPrompt())
                .eqIfPresent(BizAiVideoDO::getStatus, reqVO.getStatus())
                .orderByDesc(BizAiVideoDO::getId));
    }

    default List<BizAiVideoDO> selectListByStatusAndPlatform(Integer status, String platform) {
        return selectList(new LambdaQueryWrapperX<BizAiVideoDO>()
                .eq(BizAiVideoDO::getStatus, status)
                .eqIfPresent(BizAiVideoDO::getPlatform, platform));
    }

}
