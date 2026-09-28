package cn.iocoder.yudao.module.member.api.vip;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.service.vip.MemberVipService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class MemberVipApiImpl implements MemberVipApi {

    @Resource
    private MemberVipService vipService;

    @Override
    public CommonResult<Boolean> isVipActive(Long userId) {
        return success(vipService.isVipActive(userId));
    }

    @Override
    public CommonResult<Boolean> validateVip(Long userId) {
        vipService.validateVip(userId);
        return success(true);
    }

}
