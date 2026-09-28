package cn.iocoder.yudao.module.member.api.vip;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 会员 VIP")
public interface MemberVipApi {

    String PREFIX = ApiConstants.PREFIX + "/vip";

    @GetMapping(PREFIX + "/active")
    @Operation(summary = "判断用户 VIP 是否有效")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    CommonResult<Boolean> isVipActive(@RequestParam("userId") Long userId);

    @GetMapping(PREFIX + "/validate")
    @Operation(summary = "校验用户 VIP 是否有效，无效则抛出业务异常")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    CommonResult<Boolean> validateVip(@RequestParam("userId") Long userId);

}
