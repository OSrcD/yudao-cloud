package cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * TypeSafe JEV 决策模型 Client API
 * 官方文档：<a href="https://commandcode.ai/docs/provider#api-support">Command Code Provider API - System One</a>
 *
 * @author 芋道源码
 */
@Slf4j
public class JevApi {

    public static final String DEFAULT_BASE_URL = "https://api.commandcode.ai";
    public static final String SYSTEM_ONE_PATH = "/provider/v1/systemone";

    private final WebClient webClient;
    private final String requestUri;

    public JevApi(String baseUrl, String apiKey) {
        this(baseUrl, apiKey, false);
    }

    public JevApi(String baseUrl, String apiKey, boolean zdr) {
        String cleanUrl = StrUtil.blankToDefault(baseUrl, DEFAULT_BASE_URL).trim();
        if (cleanUrl.endsWith("/")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
        }

        // 智能拼接 endpoint
        if (cleanUrl.endsWith("/systemone")) {
            this.requestUri = cleanUrl;
        } else if (cleanUrl.endsWith("/provider/v1")) {
            this.requestUri = cleanUrl + "/systemone";
        } else {
            this.requestUri = cleanUrl + SYSTEM_ONE_PATH;
        }

        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeaders(headers -> {
                    if (zdr) {
                        headers.add("x-cmd-zdr", "1");
                    }
                })
                .build();
    }

    /**
     * 发起 JEV 决策请求
     *
     * @param request 决策输入
     * @return 决策回答
     */
    public JevDecisionResponse decide(JevDecisionRequest request) {
        return decide(request, false);
    }

    /**
     * 发起 JEV 决策请求（支持指定 Zero Data Retention）
     *
     * @param request 决策输入
     * @param zdr 是否启用零数据保留 (x-cmd-zdr: 1)
     * @return 决策回答
     */
    public JevDecisionResponse decide(JevDecisionRequest request, boolean zdr) {
        log.info("[decide][发起 JEV 决策请求] url={}, model={}, zdr={}, stateLength={}, questionsCount={}",
                requestUri, request.getModel(), zdr,
                request.getState() != null ? String.valueOf(request.getState()).length() : 0,
                request.getQuestions() != null ? request.getQuestions().size() : 0);

        long start = System.currentTimeMillis();
        try {
            JevDecisionResponse response = webClient.post()
                    .uri(requestUri)
                    .headers(headers -> {
                        if (zdr) {
                            headers.set("x-cmd-zdr", "1");
                        }
                    })
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), res ->
                            res.bodyToMono(String.class).flatMap(body -> {
                                log.error("[decide][JEV 请求失败] statusCode={}, body={}", res.statusCode(), body);
                                return Mono.error(new ServiceException(
                                        GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                                        "JEV 决策服务调用失败: " + body));
                            })
                    )
                    .bodyToMono(JevDecisionResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            long cost = System.currentTimeMillis() - start;
            if (response != null) {
                response.setResponseTimeMs(cost);
            }
            return response;
        } catch (Exception e) {
            log.error("[decide][JEV 请求异常] requestUri={}, request={}", requestUri, request, e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(),
                    "调用 JEV 决策模型异常: " + e.getMessage());
        }
    }

}
