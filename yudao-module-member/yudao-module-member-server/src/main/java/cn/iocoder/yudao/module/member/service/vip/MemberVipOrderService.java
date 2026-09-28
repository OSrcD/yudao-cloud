package cn.iocoder.yudao.module.member.service.vip;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.vip.vo.MemberVipOrderPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipOrderCreateRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipOrderDO;

/**
 * 会员 VIP 订单 Service
 */
public interface MemberVipOrderService {

    AppMemberVipOrderCreateRespVO createVipOrder(Long userId, Long packageId, String userIp);

    void updateVipOrderPaid(Long id, Long payOrderId);

    MemberVipOrderDO getVipOrder(Long id);

    PageResult<MemberVipOrderDO> getVipOrderPage(MemberVipOrderPageReqVO pageReqVO);

}
