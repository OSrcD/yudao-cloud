package cn.iocoder.yudao.module.ai.framework.ai.core.model.genai.api;

import cn.hutool.core.util.ArrayUtil;
import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Google GenAI API (Gemini SDK Wrapper)
 *
 * @author fansili
 * @since 1.0
 */
@Slf4j
public class GenAiApi {

    private final Client client;

    public GenAiApi(String baseUrl, String apiKey) {
        Client.Builder builder = Client.builder().apiKey(apiKey);
        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.httpOptions(HttpOptions.builder().baseUrl(baseUrl).build());
        }
        this.client = builder.build();
    }

    /**
     * generateContent - 生成内容（支持图片生成）
     * 对应 Python: client.models.generate_content(model=..., contents=[prompt, image])
     *
     * @param model 模型名称
     * @param prompt 提示词
     * @param imageBytes 图片字节数组
     * @return 响应结果
     */
    public GenerateContentResponse generateContent(String model, String prompt, byte[] imageBytes) {
        List<Part> parts = new ArrayList<>();
        parts.add(Part.fromText(prompt));
        if (imageBytes != null && imageBytes.length > 0) {
            parts.add(Part.fromBytes(imageBytes, "image/jpeg"));
        }

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseModalities("TEXT", "IMAGE")
                .build();

        // 对应 Python 调用方式：client.models.generate_content
        return client.models.generateContent(
                model,
                Content.fromParts(ArrayUtil.toArray(parts, Part.class)),
                config
        );
    }

}
