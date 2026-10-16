package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsOfficialAccountDO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 小红书不评论官方号名单 Service 接口
 */
public interface BizXhsOfficialAccountService {

    /**
     * 创建官方号记录（在同一用户下若已存在同名账号则自动更新）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOfficialAccount(@Valid BizXhsOfficialAccountSaveReqVO createReqVO);

    /**
     * 批量导入官方号
     *
     * @param list 待导入列表
     * @return 成功导入/更新数量
     */
    Integer batchCreateOfficialAccount(List<BizXhsOfficialAccountSaveReqVO> list);

    /**
     * 更新官方号记录
     *
     * @param updateReqVO 更新信息
     */
    void updateOfficialAccount(@Valid BizXhsOfficialAccountSaveReqVO updateReqVO);

    /**
     * 删除官方号记录
     *
     * @param id 编号
     */
    void deleteOfficialAccount(Long id);

    /**
     * 根据小红书号删除
     *
     * @param accountId 作者小红书号
     */
    void deleteOfficialAccountByAccountId(String accountId);

    /**
     * 根据所属用户编号与小红书号删除
     *
     * @param userId 所属App用户ID
     * @param accountId 作者小红书号
     */
    void deleteOfficialAccountByUserIdAndAccountId(Long userId, String accountId);

    /**
     * 清空全部官方号名单 (全局清空)
     */
    default void clearOfficialAccounts() {
        clearOfficialAccounts(null);
    }

    /**
     * 清空官方号名单 (若 userId 为空则清空全部，否则仅清空该用户下的名单)
     *
     * @param userId 所属App用户ID，为空时清空全局
     */
    void clearOfficialAccounts(Long userId);

    /**
     * 获得官方号详情
     *
     * @param id 编号
     * @return 官方号详情
     */
    BizXhsOfficialAccountDO getOfficialAccount(Long id);

    /**
     * 获得官方号分页
     *
     * @param pageReqVO 分页查询
     * @return 官方号分页
     */
    PageResult<BizXhsOfficialAccountDO> getOfficialAccountPage(BizXhsOfficialAccountPageReqVO pageReqVO);

    /**
     * 获得指定用户下的官方号列表（供 App 端同步）
     *
     * @param userId 所属App用户ID
     * @param status 状态过滤，null为全部，0为生效
     * @return 官方号列表
     */
    List<BizXhsOfficialAccountDO> getOfficialAccountList(Long userId, Integer status);

    /**
     * 获得全部官方号列表（供后台查询或全局同步）
     *
     * @param status 状态过滤，null为全部
     * @return 官方号列表
     */
    List<BizXhsOfficialAccountDO> getOfficialAccountList(Integer status);

}
