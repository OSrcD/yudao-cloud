package cn.iocoder.yudao.module.ai.framework.ai.core.model.geekai.api.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * GeekAI Video API (Veo)
 *
 * @author CuiMa
 */
@Slf4j
public class GeekAiVideoVeoApi {

    private final Predicate<HttpStatusCode> STATUS_PREDICATE = status -> !status.is2xxSuccessful();

    private final Function<Object, Function<ClientResponse, Mono<? extends Throwable>>> EXCEPTION_FUNCTION =
            reqParam -> response -> response.bodyToMono(String.class).handle((responseBody, sink) -> {
                log.error("[geekai-video-api] 调用失败！请求地址:[{}]，请求参数:[{}]，响应数据: [{}]",
                        response.request().getURI(), reqParam, responseBody);
                sink.error(new IllegalStateException("[geekai-video-api] 调用失败！"));
            });

    private final WebClient webClient;

    public GeekAiVideoVeoApi(String baseUrl, String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setBearerAuth(apiKey);
                })
                .build();
    }

    /**
     * 提交视频生成任务
     *
     * @param request 请求
     * @return 任务编号
     */
    public VideoResponse submitTask(VideoSubmitRequest request) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("model", request.getModel());
        builder.part("prompt", request.getPrompt());
        if (StrUtil.isNotEmpty(request.getSize())) {
            builder.part("size", request.getSize());
        }
        if (request.getSeconds() != null) {
            builder.part("seconds", request.getSeconds());
        }
        if (CollUtil.isNotEmpty(request.getInputReferences())) {
            for (int i = 0; i < request.getInputReferences().size(); i++) {
                byte[] bytes = request.getInputReferences().get(i);
                // 对应截图里的 input_reference 字段名，支持多个同名字段
                builder.part("input_reference", new ByteArrayResource(bytes))
                        .filename("image_" + i + ".png")
                        .contentType(MediaType.IMAGE_PNG);
            }
        }

        MultiValueMap<String, HttpEntity<?>> parts = builder.build();
        String response = webClient.post()
                .uri("/v1/videos")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(parts)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION.apply(parts))
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
    public VideoTaskResponse getTask(String id) {
        String response = webClient.get()
                .uri("/v1/videos/{id}", id)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION.apply(id))
                .bodyToMono(String.class)
                .block();
        return JsonUtils.parseObject(response, VideoTaskResponse.class);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VideoSubmitRequest {
        private String model;
        private String prompt;
        private String size;
        private Integer seconds;
        private List<byte[]> inputReferences;
    }

    @Data
    public static class VideoResponse {
        private String id;
    }

    @Data
    public static class VideoTaskResponse {
        private String id;
        private String status; // queued, processing, completed, failed
        private Integer progress;
        private String url;
        @JsonProperty("fail_reason")
        private String failReason;
    }
}
