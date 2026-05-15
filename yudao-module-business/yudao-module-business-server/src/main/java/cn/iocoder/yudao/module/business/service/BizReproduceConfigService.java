package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizReproduceConfigDO;

import jakarta.validation.Valid;

/**
 * 搬运配置 Service 接口
 */
public interface BizReproduceConfigService {

    /**
     * 创建搬运配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createReproduceConfig(@Valid BizReproduceConfigSaveReqVO createReqVO);

    /**
     * 更新搬运配置
     *
     * @param updateReqVO 更新信息
     */
    void updateReproduceConfig(@Valid BizReproduceConfigSaveReqVO updateReqVO);

    /**
     * 删除搬运配置
     *
     * @param id 编号
     */
    void deleteReproduceConfig(Long id);

    /**
     * 获得搬运配置
     *
     * @param id 编号
     * @return 搬运配置
     */
    BizReproduceConfigDO getReproduceConfig(Long id);

    /**
     * 获得搬运配置分页
     *
     * @param pageReqVO 分页查询
     * @return 搬运配置分页
     */
    PageResult<BizReproduceConfigDO> getReproduceConfigPage(BizReproduceConfigPageReqVO pageReqVO);

}

