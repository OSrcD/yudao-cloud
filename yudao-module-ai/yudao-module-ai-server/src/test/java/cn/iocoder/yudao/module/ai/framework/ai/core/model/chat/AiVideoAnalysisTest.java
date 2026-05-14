package cn.iocoder.yudao.module.ai.framework.ai.core.model.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.AiChatMessageSendRespVO;
import cn.iocoder.yudao.module.ai.service.chat.AiChatMessageService;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 视频分析脚本生成集成测试
 *
 * @author CuiMa
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.nacos.discovery.enabled=false"
})
@Import(AuthenticationConfiguration.class)
public class AiVideoAnalysisTest {

    @Resource
    private AiChatMessageService chatMessageService;

    @Test
    @Disabled("手动执行测试，需确保数据库中已配置：短视频分镜逆向工程师、短视频8秒生成单元无损改写器、短视频8秒生成单元模板复刻导演")
    public void testAnalyzeVideoAndGenerateScript() {
        // 1. 准备参数
        Long userId = 1L; // 替换为真实的 userId
        String content = "请分析这个视频的结构并生成创意脚本";
        List<String> videoUrls = List.of("http://8.148.177.33:48080/admin-api/infra/file/29/get/20260508/veo_video__1778182970239_1778182973631.mp4"); // 替换为真实的视频地址
        List<String> charImageUrls = List.of(); // 替换为真实的角色参考图
        List<String> productImageUrls = List.of(); // 替换为真实的产品参考图

        // 2. 执行调用
        Flux<CommonResult<AiChatMessageSendRespVO>> flux = chatMessageService.analyzeVideoAndGenerateScript(
                userId, content, videoUrls, charImageUrls, productImageUrls);

        // 3. 打印结果
        flux.doOnNext(response -> {
            if (response.isSuccess()) {
                System.out.println("收到回复: " + response.getData());
            } else {
                System.err.println("调用失败: " + response.getMsg());
            }
        }).then().block(); // 阻塞等待流结束
    }

}
