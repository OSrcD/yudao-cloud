package cn.iocoder.yudao.module.member.service.vip;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackagePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipPackageSaveReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipPackageDO;

import java.util.List;

/**
 * 会员 VIP 套餐 Service
 */
public interface MemberVipPackageService {

    List<MemberVipPackageDO> getEnablePackageList();

    MemberVipPackageDO validPackage(Long id);

    Long createPackage(MemberVipPackageSaveReqVO reqVO);

    void updatePackage(MemberVipPackageSaveReqVO reqVO);

    void deletePackage(Long id);

    MemberVipPackageDO getPackage(Long id);

    PageResult<MemberVipPackageDO> getPackagePage(MemberVipPackagePageReqVO pageReqVO);

}
