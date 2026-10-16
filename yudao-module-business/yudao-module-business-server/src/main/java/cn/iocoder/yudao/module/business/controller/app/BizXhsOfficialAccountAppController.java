package cn.iocoder.yudao.module.business.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsOfficialAccountSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizXhsOfficialAccountConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsOfficialAccountDO;
import cn.iocoder.yudao.module.business.service.BizXhsOfficialAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 小红书不评论官方号名单")
@RestController
@RequestMapping("/business/app/xhs-official-account")
@Validated
@PermitAll
public class BizXhsOfficialAccountAppController {

    @Resource
    private BizXhsOfficialAccountService officialAccountService;

    @GetMapping("/list")
    @Operation(summary = "App端获取当前登录账号下全部生效的官方号列表")
    public CommonResult<List<BizXhsOfficialAccountRespVO>> getOfficialAccountList(
            @Parameter(name = "userId", description = "App端用户编号(可选)")
            @RequestParam(value = "userId", required = false) Long userId,
            @Parameter(name = "mobile", description = "App端用户手机号(可选)")
            @RequestParam(value = "mobile", required = false) String mobile) {
        Long loginUserId = resolveUserId(userId);
        // status = 0 表示开启过滤生效
        List<BizXhsOfficialAccountDO> list = officialAccountService.getOfficialAccountList(loginUserId, 0);
        return success(BizXhsOfficialAccountConvert.INSTANCE.convertList(list));
    }

    @PostMapping("/create")
    @Operation(summary = "App端添加单个官方号 (绑定当前App用户与手机号)")
    public CommonResult<Long> createOfficialAccount(@Valid @RequestBody BizXhsOfficialAccountSaveReqVO createReqVO) {
        if (createReqVO.getUserId() == null) {
            createReqVO.setUserId(resolveUserId(null));
        }
        return success(officialAccountService.createOfficialAccount(createReqVO));
    }

    @PostMapping("/batch-create")
    @Operation(summary = "App端批量导入官方号 (绑定当前App用户与手机号)")
    public CommonResult<Integer> batchCreateOfficialAccount(@Valid @RequestBody List<BizXhsOfficialAccountSaveReqVO> list) {
        Long loginUserId = resolveUserId(null);
        if (list != null) {
            for (BizXhsOfficialAccountSaveReqVO item : list) {
                if (item.getUserId() == null) {
                    item.setUserId(loginUserId);
                }
            }
        }
        return success(officialAccountService.batchCreateOfficialAccount(list));
    }

    @DeleteMapping("/delete-by-account-id")
    @Operation(summary = "App端根据小红书号删除官方号 (仅删除当前用户下的数据)")
    public CommonResult<Boolean> deleteByAccountId(
            @Parameter(name = "accountId", description = "作者小红书号", required = true)
            @RequestParam("accountId") String accountId,
            @Parameter(name = "userId", description = "App端用户编号(可选)")
            @RequestParam(value = "userId", required = false) Long userId) {
        Long loginUserId = resolveUserId(userId);
        officialAccountService.deleteOfficialAccountByUserIdAndAccountId(loginUserId, accountId);
        return success(true);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "App端清空当前用户官方号名单")
    public CommonResult<Boolean> clearOfficialAccounts(
            @Parameter(name = "userId", description = "App端用户编号(可选)")
            @RequestParam(value = "userId", required = false) Long userId) {
        Long loginUserId = resolveUserId(userId);
        officialAccountService.clearOfficialAccounts(loginUserId);
        return success(true);
    }

    /**
     * 解析当前请求的 App 用户 ID (优先使用传参，其次从登录会话中读取)
     */
    private Long resolveUserId(Long paramUserId) {
        if (paramUserId != null && paramUserId > 0) {
            return paramUserId;
        }
        try {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            if (loginUserId != null && loginUserId > 0) {
                return loginUserId;
            }
        } catch (Exception ignored) {}
        return null;
    }

}
