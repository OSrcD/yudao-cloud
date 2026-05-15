package cn.iocoder.yudao.module.business.convert;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizPromptCommentCompleteSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentCompleteDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizPromptCommentCompleteConvert {

    BizPromptCommentCompleteConvert INSTANCE = Mappers.getMapper(BizPromptCommentCompleteConvert.class);

    default BizPromptCommentCompleteDO convert(BizPromptCommentCompleteSaveReqVO bean) {
        return BeanUtils.toBean(bean, BizPromptCommentCompleteDO.class);
    }

    default BizPromptCommentCompleteRespVO convert(BizPromptCommentCompleteDO bean) {
        return BeanUtils.toBean(bean, BizPromptCommentCompleteRespVO.class);
    }

    default List<BizPromptCommentCompleteRespVO> convertList(List<BizPromptCommentCompleteDO> list) {
        return BeanUtils.toBean(list, BizPromptCommentCompleteRespVO.class);
    }

}
