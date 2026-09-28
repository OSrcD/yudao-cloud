package cn.iocoder.yudao.module.member.controller.app.vip;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipStatusRespVO;
import cn.iocoder.yudao.module.member.service.vip.MemberVipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 会员 VIP 状态")
@RestController
@RequestMapping("/member/vip")
@Validated
public class AppMemberVipController {

    @Resource
    private MemberVipService vipService;

    @GetMapping("/get-status")
    @Operation(summary = "获得当前用户 VIP 状态")
    public CommonResult<AppMemberVipStatusRespVO> getVipStatus() {
        return success(vipService.getVipStatus(getLoginUserId()));
    }

}
