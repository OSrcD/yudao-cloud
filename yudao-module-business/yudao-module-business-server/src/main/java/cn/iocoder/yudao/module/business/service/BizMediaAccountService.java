package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMediaAccountDO;

import jakarta.validation.Valid;

/**
 * 媒体账号 Service 接口
 */
public interface BizMediaAccountService {

    /**
     * 创建媒体账号
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMediaAccount(@Valid BizMediaAccountSaveReqVO createReqVO);

    /**
     * 更新媒体账号
     *
     * @param updateReqVO 更新信息
     */
    void updateMediaAccount(@Valid BizMediaAccountSaveReqVO updateReqVO);

    /**
     * 删除媒体账号
     *
     * @param id 编号
     */
    void deleteMediaAccount(Long id);

    /**
     * 获得媒体账号
     *
     * @param id 编号
     * @return 媒体账号
     */
    BizMediaAccountDO getMediaAccount(Long id);

    /**
     * 获得媒体账号分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体账号分页
     */
    PageResult<BizMediaAccountDO> getMediaAccountPage(BizMediaAccountPageReqVO pageReqVO);

}

