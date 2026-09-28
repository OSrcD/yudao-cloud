package cn.iocoder.yudao.module.member.controller.app.vip;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipPackageRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipPackageDO;
import cn.iocoder.yudao.module.member.service.vip.MemberVipPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 会员 VIP 套餐")
@RestController
@RequestMapping("/member/vip-package")
@Validated
public class AppMemberVipPackageController {

    @Resource
    private MemberVipPackageService vipPackageService;

    @GetMapping("/list")
    @Operation(summary = "获得 VIP 套餐列表")
    public CommonResult<List<AppMemberVipPackageRespVO>> getVipPackageList() {
        List<MemberVipPackageDO> list = vipPackageService.getEnablePackageList();
        return success(BeanUtils.toBean(list, AppMemberVipPackageRespVO.class));
    }

}
