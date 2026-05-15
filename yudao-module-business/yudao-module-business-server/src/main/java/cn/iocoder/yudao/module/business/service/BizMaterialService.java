package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMaterialDO;

import jakarta.validation.Valid;
import java.util.Collection;

/**
 * 素材 Service 接口
 */
public interface BizMaterialService {

    /**
     * 创建素材
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMaterial(@Valid BizMaterialSaveReqVO createReqVO);

    /**
     * 更新素材
     *
     * @param updateReqVO 更新信息
     */
    void updateMaterial(@Valid BizMaterialSaveReqVO updateReqVO);

    /**
     * 删除素材
     *
     * @param id 编号
     */
    void deleteMaterial(Long id);

    /**
     * 获得素材
     *
     * @param id 编号
     * @return 素材
     */
    BizMaterialDO getMaterial(Long id);

    /**
     * 获得素材分页
     *
     * @param pageReqVO 分页查询
     * @return 素材分页
     */
    PageResult<BizMaterialDO> getMaterialPage(BizMaterialPageReqVO pageReqVO);

}

