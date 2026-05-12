package cn.iocoder.yudao.module.business.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 业务测试")
@RestController
@RequestMapping("/business")
@Slf4j
public class TestController {

    @GetMapping("/test")
    @PermitAll
    @TenantIgnore
    public CommonResult<?> sendMessage() {
        return success("hi");
    }


}
