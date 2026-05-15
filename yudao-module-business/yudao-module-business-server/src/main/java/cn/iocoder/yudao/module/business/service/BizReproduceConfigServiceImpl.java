package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizReproduceConfigSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizReproduceConfigConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizReproduceConfigDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizReproduceConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 搬运配置 Service 实现类
 */
@Service
@Validated
public class BizReproduceConfigServiceImpl implements BizReproduceConfigService {

    @Resource
    private BizReproduceConfigMapper reproduceConfigMapper;

    @Override
    public Long createReproduceConfig(BizReproduceConfigSaveReqVO createReqVO) {
        BizReproduceConfigDO reproduceConfig = BizReproduceConfigConvert.INSTANCE.convert(createReqVO);
        reproduceConfigMapper.insert(reproduceConfig);
        return reproduceConfig.getId();
    }

    @Override
    public void updateReproduceConfig(BizReproduceConfigSaveReqVO updateReqVO) {
        BizReproduceConfigDO updateObj = BizReproduceConfigConvert.INSTANCE.convert(updateReqVO);
        reproduceConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteReproduceConfig(Long id) {
        reproduceConfigMapper.deleteById(id);
    }

    @Override
    public BizReproduceConfigDO getReproduceConfig(Long id) {
        return reproduceConfigMapper.selectById(id);
    }

    @Override
    public PageResult<BizReproduceConfigDO> getReproduceConfigPage(BizReproduceConfigPageReqVO pageReqVO) {
        return reproduceConfigMapper.selectPage(pageReqVO);
    }

}

