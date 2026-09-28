package cn.iocoder.yudao.module.member.service.vip;

import cn.iocoder.yudao.module.member.controller.admin.user.vo.MemberUserUpdateVipReqVO;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipStatusRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;

import java.time.LocalDateTime;

/**
 * 会员 VIP 权益 Service
 */
public interface MemberVipService {

    /**
     * 判断 VIP 是否有效
     */
    boolean isVipActive(Long userId);

    /**
     * 校验 VIP，无效抛异常
     */
    void validateVip(Long userId);

    /**
     * 获得会员状态（App 展示）
     */
    AppMemberVipStatusRespVO getVipStatus(Long userId);

    /**
     * 延长 VIP 有效期（从 max(now, 当前到期) 起叠加天数）
     */
    LocalDateTime extendVipExpireTime(Long userId, int durationDays);

    /**
     * 新用户发放试用（仅在创建用户时调用）
     */
    void fillTrialOnCreate(MemberUserDO user);

    /**
     * 管理后台修改用户 VIP
     */
    void updateUserVip(MemberUserUpdateVipReqVO reqVO);

}
