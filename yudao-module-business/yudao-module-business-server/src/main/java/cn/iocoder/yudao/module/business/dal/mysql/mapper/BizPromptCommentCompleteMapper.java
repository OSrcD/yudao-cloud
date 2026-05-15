package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompletePageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentCompleteDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizPromptCommentCompleteMapper extends BaseMapperX<BizPromptCommentCompleteDO> {

    default PageResult<BizPromptCommentCompleteDO> selectPage(BizPromptCommentCompletePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BizPromptCommentCompleteDO>()
                .eqIfPresent(BizPromptCommentCompleteDO::getCommentId, reqVO.getCommentId())
                .eqIfPresent(BizPromptCommentCompleteDO::getMediaAccountId, reqVO.getMediaAccountId())
                .likeIfPresent(BizPromptCommentCompleteDO::getXhsNoteInfo, reqVO.getXhsNoteInfo())
                .eqIfPresent(BizPromptCommentCompleteDO::getCheckStatus, reqVO.getCheckStatus())
                .eqIfPresent(BizPromptCommentCompleteDO::getCommentStatus, reqVO.getCommentStatus())
                .orderByDesc(BizPromptCommentCompleteDO::getId));
    }

    IPage<BizPromptCommentCompleteDO> selectCheckList(@Param("page") IPage<BizPromptCommentCompleteDO> page, @Param("bo") BizPromptCommentCompletePageReqVO bo);

}
