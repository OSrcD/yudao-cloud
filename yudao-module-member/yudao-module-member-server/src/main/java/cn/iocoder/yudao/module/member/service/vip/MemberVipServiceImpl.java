package cn.iocoder.yudao.module.member.service.vip;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.user.vo.MemberUserUpdateVipReqVO;
import cn.iocoder.yudao.module.member.controller.app.vip.vo.AppMemberVipStatusRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.dataobject.vip.MemberVipOrderDO;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.dal.mysql.vip.MemberVipOrderMapper;
import cn.iocoder.yudao.module.member.enums.vip.MemberVipConstants;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.USER_VIP_EXPIRED;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.USER_VIP_UPDATE_PARAM_INVALID;

@Service
@Validated
public class MemberVipServiceImpl implements MemberVipService {

    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberVipOrderMapper vipOrderMapper;

    @Override
    public boolean isVipActive(Long userId) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        return isActive(user);
    }

    @Override
    public void validateVip(Long userId) {
        if (!isVipActive(userId)) {
            throw exception(USER_VIP_EXPIRED);
        }
    }

    @Override
    public AppMemberVipStatusRespVO getVipStatus(Long userId) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        boolean active = isActive(user);
        boolean trialUsed = Boolean.TRUE.equals(user.getVipTrialUsed());
        Long paidCount = vipOrderMapper.selectCount(new LambdaQueryWrapperX<MemberVipOrderDO>()
                .eq(MemberVipOrderDO::getUserId, userId)
                .eq(MemberVipOrderDO::getPayStatus, true));
        boolean trial = active && trialUsed && (paidCount == null || paidCount == 0);

        AppMemberVipStatusRespVO resp = new AppMemberVipStatusRespVO();
        resp.setActive(active);
        resp.setExpireTime(user.getVipExpireTime());
        resp.setTrialUsed(trialUsed);
        resp.setTrial(trial);
        return resp;
    }

    @Override
    public LocalDateTime extendVipExpireTime(Long userId, int durationDays) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime base = user.getVipExpireTime();
        if (base == null || base.isBefore(now)) {
            base = now;
        }
        LocalDateTime newExpire = base.plusDays(durationDays);
        memberUserMapper.updateById(new MemberUserDO().setId(userId).setVipExpireTime(newExpire));
        return newExpire;
    }

    @Override
    public void fillTrialOnCreate(MemberUserDO user) {
        user.setVipExpireTime(LocalDateTime.now().plusDays(MemberVipConstants.TRIAL_DAYS));
        user.setVipTrialUsed(true);
    }

    @Override
    public void updateUserVip(MemberUserUpdateVipReqVO reqVO) {
        MemberUserDO user = memberUserMapper.selectById(reqVO.getId());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        Integer changeType = reqVO.getChangeType();
        if (changeType == 1) {
            // 设置到期时间
            if (reqVO.getVipExpireTime() == null) {
                throw exception(USER_VIP_UPDATE_PARAM_INVALID);
            }
            LambdaUpdateWrapper<MemberUserDO> wrapper = new LambdaUpdateWrapper<MemberUserDO>()
                    .eq(MemberUserDO::getId, reqVO.getId())
                    .set(MemberUserDO::getVipExpireTime, reqVO.getVipExpireTime());
            if (reqVO.getVipTrialUsed() != null) {
                wrapper.set(MemberUserDO::getVipTrialUsed, reqVO.getVipTrialUsed());
            }
            memberUserMapper.update(null, wrapper);
            return;
        }
        if (changeType == 2) {
            // 延长天数
            if (reqVO.getDurationDays() == null || reqVO.getDurationDays() <= 0) {
                throw exception(USER_VIP_UPDATE_PARAM_INVALID);
            }
            extendVipExpireTime(reqVO.getId(), reqVO.getDurationDays());
            if (reqVO.getVipTrialUsed() != null) {
                memberUserMapper.updateById(new MemberUserDO()
                        .setId(reqVO.getId())
                        .setVipTrialUsed(reqVO.getVipTrialUsed()));
            }
            return;
        }
        if (changeType == 3) {
            // 清空 VIP
            LambdaUpdateWrapper<MemberUserDO> wrapper = new LambdaUpdateWrapper<MemberUserDO>()
                    .eq(MemberUserDO::getId, reqVO.getId())
                    .set(MemberUserDO::getVipExpireTime, null);
            if (reqVO.getVipTrialUsed() != null) {
                wrapper.set(MemberUserDO::getVipTrialUsed, reqVO.getVipTrialUsed());
            }
            memberUserMapper.update(null, wrapper);
        }
    }

    private static boolean isActive(MemberUserDO user) {
        return user != null
                && user.getVipExpireTime() != null
                && user.getVipExpireTime().isAfter(LocalDateTime.now());
    }

}
