package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMediaAccountSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizMediaAccountConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMediaAccountDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizMediaAccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 媒体账号 Service 实现类
 */
@Service
@Validated
public class BizMediaAccountServiceImpl implements BizMediaAccountService {

    @Resource
    private BizMediaAccountMapper mediaAccountMapper;

    @Override
    public Long createMediaAccount(BizMediaAccountSaveReqVO createReqVO) {
        BizMediaAccountDO mediaAccount = BizMediaAccountConvert.INSTANCE.convert(createReqVO);
        mediaAccountMapper.insert(mediaAccount);
        return mediaAccount.getId();
    }

    @Override
    public void updateMediaAccount(BizMediaAccountSaveReqVO updateReqVO) {
        BizMediaAccountDO updateObj = BizMediaAccountConvert.INSTANCE.convert(updateReqVO);
        mediaAccountMapper.updateById(updateObj);
    }

    @Override
    public void deleteMediaAccount(Long id) {
        mediaAccountMapper.deleteById(id);
    }

    @Override
    public BizMediaAccountDO getMediaAccount(Long id) {
        return mediaAccountMapper.selectById(id);
    }

    @Override
    public PageResult<BizMediaAccountDO> getMediaAccountPage(BizMediaAccountPageReqVO pageReqVO) {
        return mediaAccountMapper.selectPage(pageReqVO);
    }

}

