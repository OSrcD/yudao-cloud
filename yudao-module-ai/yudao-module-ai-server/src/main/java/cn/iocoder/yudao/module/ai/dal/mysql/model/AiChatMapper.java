package cn.iocoder.yudao.module.ai.dal.mysql.model;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * API 模型 Mapper
 *
 * @author fansili
 */
@Mapper
public interface AiChatMapper extends BaseMapperX<AiModelDO> {

    default AiModelDO selectFirstByStatus(Integer type, Integer status) {
        return selectOne(new QueryWrapperX<AiModelDO>()
                .eq("type", type)
                .eq("status", status)
                .limitN(1)
                .orderByDesc("is_default")
                .orderByAsc("sort"));
    }

    default PageResult<AiModelDO> selectPage(AiModelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiModelDO>()
                .likeIfPresent(AiModelDO::getName, reqVO.getName())
                .eqIfPresent(AiModelDO::getModel, reqVO.getModel())
                .eqIfPresent(AiModelDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(AiModelDO::getClientType, reqVO.getClientType())
                .orderByDesc(AiModelDO::getIsDefault)
                .orderByAsc(AiModelDO::getSort));
    }

    default List<AiModelDO> selectListByStatusAndType(Integer status, Integer type,
                                                      @Nullable String platform) {
        return selectListByStatusAndType(status, type, platform, null);
    }

    default List<AiModelDO> selectListByStatusAndType(Integer status, Integer type,
                                                      @Nullable String platform,
                                                      @Nullable String clientType) {
        return selectList(new LambdaQueryWrapperX<AiModelDO>()
                .eq(AiModelDO::getStatus, status)
                .eq(AiModelDO::getType, type)
                .eqIfPresent(AiModelDO::getPlatform, platform)
                // 【向下兼容端类型】：若请求指定为 APP，则匹配 client_type IN ('APP', 'ALL')，确保通用模型能在 APP 端正常返回
                .inIfPresent(AiModelDO::getClientType, "APP".equalsIgnoreCase(clientType)
                        ? List.of("APP", "ALL")
                        : (cn.hutool.core.util.StrUtil.isNotEmpty(clientType) ? List.of(clientType) : null))
                .orderByDesc(AiModelDO::getIsDefault)
                .orderByAsc(AiModelDO::getSort));
    }

}
