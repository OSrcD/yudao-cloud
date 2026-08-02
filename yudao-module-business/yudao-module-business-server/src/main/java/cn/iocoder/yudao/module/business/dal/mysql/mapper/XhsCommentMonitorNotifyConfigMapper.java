package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorNotifyConfigDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface XhsCommentMonitorNotifyConfigMapper extends BaseMapperX<XhsCommentMonitorNotifyConfigDO> {

    default XhsCommentMonitorNotifyConfigDO selectByTenantId(Long tenantId) {
        return selectOne(new LambdaQueryWrapper<XhsCommentMonitorNotifyConfigDO>()
                .eq(XhsCommentMonitorNotifyConfigDO::getId, tenantId)
                .last("LIMIT 1"));
    }

}
