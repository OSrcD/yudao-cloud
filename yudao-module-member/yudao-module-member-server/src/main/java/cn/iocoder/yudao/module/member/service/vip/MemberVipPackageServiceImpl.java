package cn.iocoder.yudao.module.member.service.vip;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackagePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackageSaveReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipPackageDO;
import cn.iocoder.yudao.module.member.dal.mysql.vip.MemberVipPackageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.VIP_PACKAGE_DISABLE;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.VIP_PACKAGE_NOT_EXISTS;

@Service
@Validated
public class MemberVipPackageServiceImpl implements MemberVipPackageService {

    @Resource
    private MemberVipPackageMapper vipPackageMapper;

    @Override
    public List<MemberVipPackageDO> getEnablePackageList() {
        return vipPackageMapper.selectListByStatus(ENABLE.getStatus());
    }

    @Override
    public MemberVipPackageDO validPackage(Long id) {
        MemberVipPackageDO pkg = vipPackageMapper.selectById(id);
        if (pkg == null) {
            throw exception(VIP_PACKAGE_NOT_EXISTS);
        }
        if (cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.isDisable(pkg.getStatus())) {
            throw exception(VIP_PACKAGE_DISABLE);
        }
        return pkg;
    }

    @Override
    public Long createPackage(MemberVipPackageSaveReqVO reqVO) {
        MemberVipPackageDO pkg = BeanUtils.toBean(reqVO, MemberVipPackageDO.class);
        if (pkg.getSort() == null) {
            pkg.setSort(0);
        }
        vipPackageMapper.insert(pkg);
        return pkg.getId();
    }

    @Override
    public void updatePackage(MemberVipPackageSaveReqVO reqVO) {
        validateExists(reqVO.getId());
        vipPackageMapper.updateById(BeanUtils.toBean(reqVO, MemberVipPackageDO.class));
    }

    @Override
    public void deletePackage(Long id) {
        validateExists(id);
        vipPackageMapper.deleteById(id);
    }

    @Override
    public MemberVipPackageDO getPackage(Long id) {
        return vipPackageMapper.selectById(id);
    }

    @Override
    public PageResult<MemberVipPackageDO> getPackagePage(MemberVipPackagePageReqVO pageReqVO) {
        return vipPackageMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<MemberVipPackageDO>()
                .likeIfPresent(MemberVipPackageDO::getName, pageReqVO.getName())
                .eqIfPresent(MemberVipPackageDO::getStatus, pageReqVO.getStatus())
                .betweenIfPresent(MemberVipPackageDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByAsc(MemberVipPackageDO::getSort)
                .orderByDesc(MemberVipPackageDO::getId));
    }

    private void validateExists(Long id) {
        if (id == null || vipPackageMapper.selectById(id) == null) {
            throw exception(VIP_PACKAGE_NOT_EXISTS);
        }
    }

}
