package cn.iocoder.yudao.module.business.dal.mysql.mapper;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorKeywordDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface XhsCommentMonitorKeywordMapper extends BaseMapperX<XhsCommentMonitorKeywordDO> {

    default List<XhsCommentMonitorKeywordDO> selectAll() {
        return selectList(new LambdaQueryWrapper<XhsCommentMonitorKeywordDO>()
                .orderByAsc(XhsCommentMonitorKeywordDO::getId));
    }

}
