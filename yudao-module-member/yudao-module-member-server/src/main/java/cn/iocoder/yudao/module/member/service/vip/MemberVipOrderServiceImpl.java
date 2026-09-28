package cn.iocoder.yudao.module.member.service.vip;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipOrderPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipOrderCreateRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipOrderDO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipPackageDO;
import cn.iocoder.yudao.module.member.dal.mysql.vip.MemberVipOrderMapper;
import cn.iocoder.yudao.module.member.enums.vip.MemberVipConstants;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDateTime;

import static cn.hutool.core.util.ObjectUtil.notEqual;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.addTime;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants.PAY_ORDER_NOT_FOUND;

@Service
@Validated
@Slf4j
public class MemberVipOrderServiceImpl implements MemberVipOrderService {

    @Resource
    private MemberVipOrderMapper vipOrderMapper;
    @Resource
    private MemberVipPackageService vipPackageService;
    @Resource
    private MemberVipService vipService;
    @Resource
    private PayOrderApi payOrderApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMemberVipOrderCreateRespVO createVipOrder(Long userId, Long packageId, String userIp) {
        MemberVipPackageDO pkg = vipPackageService.validPackage(packageId);

        MemberVipOrderDO order = new MemberVipOrderDO()
                .setUserId(userId)
                .setPackageId(pkg.getId())
                .setPackageName(pkg.getName())
                .setPrice(pkg.getPrice())
                .setDurationDays(pkg.getDurationDays())
                .setPayStatus(false);
        vipOrderMapper.insert(order);

        Long payOrderId = payOrderApi.createOrder(new PayOrderCreateReqDTO()
                .setAppKey(MemberVipConstants.PAY_APP_KEY)
                .setUserIp(userIp)
                .setUserId(userId)
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantOrderId(order.getId().toString())
                .setSubject(pkg.getName())
                .setBody("开通" + pkg.getName())
                .setPrice(pkg.getPrice())
                .setExpireTime(addTime(Duration.ofHours(2L)))).getCheckedData();

        vipOrderMapper.updateById(new MemberVipOrderDO().setId(order.getId()).setPayOrderId(payOrderId));

        return new AppMemberVipOrderCreateRespVO()
                .setId(order.getId())
                .setPayOrderId(payOrderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVipOrderPaid(Long id, Long payOrderId) {
        MemberVipOrderDO order = vipOrderMapper.selectById(id);
        if (order == null) {
            log.error("[updateVipOrderPaid][order({}) payOrder({}) 不存在订单]", id, payOrderId);
            throw exception(VIP_ORDER_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(order.getPayStatus())) {
            if (ObjectUtil.equals(order.getPayOrderId(), payOrderId)) {
                log.warn("[updateVipOrderPaid][order({}) 已支付且支付单号相同，忽略]", id);
                return;
            }
            log.error("[updateVipOrderPaid][order({}) 支付单不匹配({})]", id, payOrderId);
            throw exception(VIP_ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
        }

        PayOrderRespDTO payOrder = validatePayOrderPaid(order, payOrderId);

        int updateCount = vipOrderMapper.updateByIdAndPayed(id, false,
                new MemberVipOrderDO().setPayStatus(true).setPayTime(LocalDateTime.now())
                        .setPayChannelCode(payOrder.getChannelCode()));
        if (updateCount == 0) {
            throw exception(VIP_ORDER_UPDATE_PAID_STATUS_NOT_UNPAID);
        }

        vipService.extendVipExpireTime(order.getUserId(), order.getDurationDays());
    }

    @Override
    public MemberVipOrderDO getVipOrder(Long id) {
        return vipOrderMapper.selectById(id);
    }

    @Override
    public PageResult<MemberVipOrderDO> getVipOrderPage(MemberVipOrderPageReqVO pageReqVO) {
        return vipOrderMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<MemberVipOrderDO>()
                .eqIfPresent(MemberVipOrderDO::getUserId, pageReqVO.getUserId())
                .eqIfPresent(MemberVipOrderDO::getPayStatus, pageReqVO.getPayStatus())
                .betweenIfPresent(MemberVipOrderDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(MemberVipOrderDO::getId));
    }

    private PayOrderRespDTO validatePayOrderPaid(MemberVipOrderDO order, Long payOrderId) {
        PayOrderRespDTO payOrder = payOrderApi.getOrder(payOrderId).getCheckedData();
        if (payOrder == null) {
            log.error("[validatePayOrderPaid][order({}) payOrder({}) 不存在]", order.getId(), payOrderId);
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
            log.error("[validatePayOrderPaid][order({}) payOrder({}) 未支付成功] data={}",
                    order.getId(), payOrderId, toJsonString(payOrder));
            throw exception(VIP_ORDER_UPDATE_PAID_FAIL_PAY_ORDER_STATUS_NOT_SUCCESS);
        }
        if (notEqual(payOrder.getPrice(), order.getPrice())) {
            log.error("[validatePayOrderPaid][order({}) 金额不匹配] order={}, payOrder={}",
                    order.getId(), toJsonString(order), toJsonString(payOrder));
            throw exception(VIP_ORDER_UPDATE_PAID_FAIL_PAY_PRICE_NOT_MATCH);
        }
        if (notEqual(payOrder.getMerchantOrderId(), order.getId().toString())) {
            log.error("[validatePayOrderPaid][order({}) 商户单号不匹配] payOrder={}",
                    order.getId(), toJsonString(payOrder));
            throw exception(VIP_ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
        }
        return payOrder;
    }

}
