package cn.iocoder.yudao.module.member.controller.app.vip;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipOrderCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipOrderCreateRespVO;
import cn.iocoder.yudao.module.member.service.vip.MemberVipOrderService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 会员 VIP 订单")
@RestController
@RequestMapping("/member/vip-order")
@Validated
public class AppMemberVipOrderController {

    @Resource
    private MemberVipOrderService vipOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建 VIP 开通订单")
    public CommonResult<AppMemberVipOrderCreateRespVO> createVipOrder(
            @Valid @RequestBody AppMemberVipOrderCreateReqVO reqVO) {
        return success(vipOrderService.createVipOrder(getLoginUserId(), reqVO.getPackageId(), getClientIP()));
    }

    @PostMapping("/update-paid")
    @Operation(summary = "更新 VIP 订单为已支付")
    @PermitAll
    public CommonResult<Boolean> updateVipOrderPaid(@RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        vipOrderService.updateVipOrderPaid(Long.valueOf(notifyReqDTO.getMerchantOrderId()),
                notifyReqDTO.getPayOrderId());
        return success(true);
    }

}
