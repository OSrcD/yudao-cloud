package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorNotifyLogDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XhsCommentMonitorNotifyLogMapper extends BaseMapperX<XhsCommentMonitorNotifyLogDO> {

    /**
     * 判断某评论对某关键词是否已经推送过（去重检查）
     */
    default boolean existsByCommentIdAndKeyword(String commentId, String keyword, Long tenantId) {
        return selectCount(new LambdaQueryWrapper<XhsCommentMonitorNotifyLogDO>()
                .eq(XhsCommentMonitorNotifyLogDO::getCommentId, commentId)
                .eq(XhsCommentMonitorNotifyLogDO::getKeyword, keyword)
                .eq(XhsCommentMonitorNotifyLogDO::getTenantId, tenantId)) > 0;
    }

}
