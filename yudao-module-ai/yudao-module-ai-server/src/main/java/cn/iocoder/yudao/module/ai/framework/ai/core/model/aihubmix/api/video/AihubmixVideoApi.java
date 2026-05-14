package cn.iocoder.yudao.module.ai.framework.ai.core.model.aihubmix.api.video;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Aihubmix Video API
 *
 * @author CuiMa
 */
@Slf4j
public class AihubmixVideoApi {

    private final Predicate<HttpStatusCode> STATUS_PREDICATE = status -> !status.is2xxSuccessful();

    private final Function<Object, Function<ClientResponse, Mono<? extends Throwable>>> EXCEPTION_FUNCTION =
            reqParam -> response -> response.bodyToMono(String.class).handle((responseBody, sink) -> {
                log.error("[aihubmix-video-api] 调用失败！请求地址:[{}]，请求参数:[{}]，响应数据: [{}]",
                        response.request().getURI(), reqParam, responseBody);
                sink.error(new IllegalStateException("[aihubmix-video-api] 调用失败！"));
            });

    private final WebClient webClient;

    public AihubmixVideoApi(String baseUrl, String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setBearerAuth(apiKey);
                })
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024)) // 设置最大内存限制为 100MB
                .build();
    }

    /**
     * 提交视频生成任务
     *
     * @param request 请求
     * @return 任务详情
     */
    public VideoResponse submitTask(VideoSubmitRequest request) {
        String response = webClient.post()
                .uri("/v1/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION.apply(request))
                .bodyToMono(String.class)
                .block();
        return JsonUtils.parseObject(response, VideoResponse.class);
    }

    /**
     * 查询任务状态
     *
     * @param id 任务编号
     * @return 任务详情
     */
    public VideoResponse getTask(String id) {
        String response = webClient.get()
                .uri("/v1/videos/{id}", id)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION.apply(id))
                .bodyToMono(String.class)
                .block();
        return JsonUtils.parseObject(response, VideoResponse.class);
    }

    /**
     * 下载视频内容
     *
     * @param id 任务编号
     * @return 视频内容
     */
    public byte[] downloadVideo(String id) {
        return webClient.get()
                .uri("/v1/videos/{id}/content", id)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION.apply(id))
                .bodyToMono(byte[].class)
                .block();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoSubmitRequest {
        private String model;
        private String prompt;
        private String seconds; // 视频时长（秒），统一使用字符串类型
        private String size; // 分辨率，格式 宽x高
        @JsonProperty("input_reference")
        private String inputReference; // 参考图片，支持 URL 或 base64
    }

    @Data
    public static class VideoResponse {
        private String id;
        private String object;
        private String status; // queued, in_progress, completed, failed
        private String model;
        private Integer duration;
        private Integer width;
        private Integer height;
        private String url;
        @JsonProperty("created_at")
        private Long createdAt;
        private Integer progress;
        private Error error;

        @Data
        public static class Error {
            private String message;
            private String type;
        }
    }
}
