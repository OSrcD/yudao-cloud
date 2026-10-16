package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizXhsOfficialAccountConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsOfficialAccountDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizXhsOfficialAccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 小红书不评论官方号名单 Service 实现类
 */
@Service
@Validated
public class BizXhsOfficialAccountServiceImpl implements BizXhsOfficialAccountService {

    @Resource
    private BizXhsOfficialAccountMapper officialAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOfficialAccount(BizXhsOfficialAccountSaveReqVO createReqVO) {
        String accountId = cleanAccountId(createReqVO.getAccountId());
        createReqVO.setAccountId(accountId);
        if (createReqVO.getStatus() == null) {
            createReqVO.setStatus(0);
        }

        // 检查当前用户下是否已存在该官方号
        BizXhsOfficialAccountDO exist = officialAccountMapper.selectByUserIdAndAccountId(createReqVO.getUserId(), accountId);
        if (exist != null) {
            exist.setNickname(StringUtils.hasText(createReqVO.getNickname()) ? createReqVO.getNickname() : exist.getNickname());
            exist.setRemark(StringUtils.hasText(createReqVO.getRemark()) ? createReqVO.getRemark() : exist.getRemark());
            exist.setStatus(createReqVO.getStatus());
            if (StringUtils.hasText(createReqVO.getMobile())) {
                exist.setMobile(createReqVO.getMobile());
            }
            if (createReqVO.getUserId() != null) {
                exist.setUserId(createReqVO.getUserId());
            }
            officialAccountMapper.updateById(exist);
            return exist.getId();
        }

        BizXhsOfficialAccountDO insertDO = BizXhsOfficialAccountConvert.INSTANCE.convert(createReqVO);
        officialAccountMapper.insert(insertDO);
        return insertDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchCreateOfficialAccount(List<BizXhsOfficialAccountSaveReqVO> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (BizXhsOfficialAccountSaveReqVO reqVO : list) {
            if (!StringUtils.hasText(reqVO.getAccountId())) {
                continue;
            }
            createOfficialAccount(reqVO);
            count++;
        }
        return count;
    }

    @Override
    public void updateOfficialAccount(BizXhsOfficialAccountSaveReqVO updateReqVO) {
        if (StringUtils.hasText(updateReqVO.getAccountId())) {
            updateReqVO.setAccountId(cleanAccountId(updateReqVO.getAccountId()));
        }
        BizXhsOfficialAccountDO updateObj = BizXhsOfficialAccountConvert.INSTANCE.convert(updateReqVO);
        officialAccountMapper.updateById(updateObj);
    }

    @Override
    public void deleteOfficialAccount(Long id) {
        officialAccountMapper.deleteById(id);
    }

    @Override
    public void deleteOfficialAccountByAccountId(String accountId) {
        deleteOfficialAccountByUserIdAndAccountId(null, accountId);
    }

    @Override
    public void deleteOfficialAccountByUserIdAndAccountId(Long userId, String accountId) {
        if (!StringUtils.hasText(accountId)) {
            return;
        }
        String cleanId = cleanAccountId(accountId);
        BizXhsOfficialAccountDO exist = officialAccountMapper.selectByUserIdAndAccountId(userId, cleanId);
        if (exist != null) {
            officialAccountMapper.deleteById(exist.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearOfficialAccounts() {
        clearOfficialAccounts(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearOfficialAccounts(Long userId) {
        LambdaQueryWrapperX<BizXhsOfficialAccountDO> wrapper = new LambdaQueryWrapperX<>();
        if (userId != null) {
            wrapper.eq(BizXhsOfficialAccountDO::getUserId, userId);
        }
        officialAccountMapper.delete(wrapper);
    }

    @Override
    public BizXhsOfficialAccountDO getOfficialAccount(Long id) {
        return officialAccountMapper.selectById(id);
    }

    @Override
    public PageResult<BizXhsOfficialAccountDO> getOfficialAccountPage(BizXhsOfficialAccountPageReqVO pageReqVO) {
        return officialAccountMapper.selectPage(pageReqVO);
    }

    @Override
    public List<BizXhsOfficialAccountDO> getOfficialAccountList(Long userId, Integer status) {
        return officialAccountMapper.selectListByUserIdAndStatus(userId, status);
    }

    @Override
    public List<BizXhsOfficialAccountDO> getOfficialAccountList(Integer status) {
        return officialAccountMapper.selectListByStatus(status);
    }

    /**
     * 清理账号ID中的多余空白字符
     */
    private String cleanAccountId(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().replaceAll("\\s+", "");
    }

}
