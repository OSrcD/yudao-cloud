package cn.iocoder.yudao.module.member.controller.admin.vip;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipOrderPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipOrderRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipOrderDO;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import cn.iocoder.yudao.module.member.service.vip.MemberVipOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

@Tag(name = "管理后台 - 会员 VIP 订单")
@RestController
@RequestMapping("/member/vip-order")
@Validated
public class MemberVipOrderController {

    @Resource
    private MemberVipOrderService vipOrderService;
    @Resource
    private MemberUserService memberUserService;

    @GetMapping("/page")
    @Operation(summary = "获得 VIP 订单分页")
    @PreAuthorize("@ss.hasPermission('member:vip-order:query')")
    public CommonResult<PageResult<MemberVipOrderRespVO>> getVipOrderPage(@Valid MemberVipOrderPageReqVO pageReqVO) {
        PageResult<MemberVipOrderDO> pageResult = vipOrderService.getVipOrderPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        List<MemberUserDO> users = memberUserService.getUserList(
                convertList(pageResult.getList(), MemberVipOrderDO::getUserId));
        Map<Long, MemberUserDO> userMap = convertMap(users, MemberUserDO::getId);
        List<MemberVipOrderRespVO> list = BeanUtils.toBean(pageResult.getList(), MemberVipOrderRespVO.class);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
        });
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

}
