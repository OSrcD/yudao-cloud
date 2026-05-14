package cn.iocoder.yudao.module.ai.framework.ai.core.model.geekai.api.image;

import cn.hutool.core.codec.Base64;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * GeekAI API (Gemini Format)
 *
 * @author fansili
 * @since 1.0
 */
@Slf4j
public class GeekAiGeminiImageApi {

    private final Predicate<HttpStatusCode> STATUS_PREDICATE = status -> !status.is2xxSuccessful();

    private final Function<Object, Function<ClientResponse, Mono<? extends Throwable>>> EXCEPTION_FUNCTION =
            reqParam -> response -> response.bodyToMono(String.class).handle((responseBody, sink) -> {
                HttpRequest request = response.request();
                log.error("[geekai-api] 调用失败！请求方式:[{}]，请求地址:[{}]，请求参数:[{}]，响应数据: [{}]",
                        request.getMethod(), request.getURI(), reqParam, responseBody);
                sink.error(new IllegalStateException("[geekai-api] 调用失败！"));
            });

    private final WebClient webClient;

    public GeekAiGeminiImageApi(String baseUrl, String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setBearerAuth(apiKey);
                })
                .build();
    }

    /**
     * generateContent - 生成内容（支持图片生成）
     *
     * @param model 模型名称
     * @param request 请求体
     * @return 响应
     */
    public GenerateContentResponse generateContent(String model, GenerateContentRequest request) {
        String uri = String.format("/v1beta/models/%s:generateContent", model);
        String responseBody = post(uri, request);
        return JsonUtils.parseObject(responseBody, GenerateContentResponse.class);
    }

    private String post(String uri, Object body) {
        return webClient.post()
                .uri(uri)
                .body(Mono.just(JsonUtils.toJsonString(body)), String.class)
                .retrieve()
                .onStatus(STATUS_PREDICATE, EXCEPTION_FUNCTION.apply(body))
                .bodyToMono(String.class)
                .block();
    }

    // ========== DTO 结构 ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GenerateContentRequest {
        private List<Content> contents;
        private GenerationConfig generationConfig;
        private List<SafetySetting> safetySettings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private String role;
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Part {
        private String text;
        @JsonProperty("inline_data")
        private InlineData inlineData;

        public static Part fromText(String text) {
            return Part.builder().text(text).build();
        }

        public static Part fromImage(String mimeType, byte[] data) {
            return Part.builder()
                    .inlineData(new InlineData(mimeType, Base64.encode(data)))
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InlineData {
        @JsonProperty("mime_type")
        private String mimeType;
        private String data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GenerationConfig {
        private List<String> responseModalities;
        private ImageConfig imageConfig;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageConfig {
        private String aspectRatio;
        private String imageSize;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SafetySetting {
        private String category;
        private String threshold;
    }

    @Data
    public static class GenerateContentResponse {
        private List<Candidate> candidates;
        private UsageMetadata usageMetadata;
    }

    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
        private List<SafetyRating> safetyRatings;
    }

    @Data
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Data
    public static class UsageMetadata {
        private Integer promptTokenCount;
        private Integer candidatesTokenCount;
        private Integer totalTokenCount;
    }

}
