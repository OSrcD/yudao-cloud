package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizMaterialConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMaterialDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizMaterialMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 素材 Service 实现类
 */
@Service
@Validated
public class BizMaterialServiceImpl implements BizMaterialService {

    @Resource
    private BizMaterialMapper materialMapper;

    @Override
    public Long createMaterial(BizMaterialSaveReqVO createReqVO) {
        // 插入
        BizMaterialDO material = BizMaterialConvert.INSTANCE.convert(createReqVO);
        materialMapper.insert(material);
        // 返回
        return material.getId();
    }

    @Override
    public void updateMaterial(BizMaterialSaveReqVO updateReqVO) {
        // 更新
        BizMaterialDO updateObj = BizMaterialConvert.INSTANCE.convert(updateReqVO);
        materialMapper.updateById(updateObj);
    }

    @Override
    public void deleteMaterial(Long id) {
        // 删除
        materialMapper.deleteById(id);
    }

    @Override
    public BizMaterialDO getMaterial(Long id) {
        return materialMapper.selectById(id);
    }

    @Override
    public PageResult<BizMaterialDO> getMaterialPage(BizMaterialPageReqVO pageReqVO) {
        return materialMapper.selectPage(pageReqVO);
    }

}

