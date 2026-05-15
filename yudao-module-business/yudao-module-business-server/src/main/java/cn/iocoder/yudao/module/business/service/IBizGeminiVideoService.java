package cn.iocoder.yudao.module.business.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Gemini 视频分析 Service 接口
 */
public interface IBizGeminiVideoService {

    /**
     * 分析视频
     */
    String analyzeVideo(MultipartFile file, String prompt, String mode);

    /**
     * 获取 Veo3 提示词列表
     */
    List<String> getVeo3Prompts(String productConfigJson);

    /**
     * 生成 Veo3 JSON
     */
    String generateVeo3Json(String videoUrl, String productConfigJson, List<String> charImageUrls, List<String> productImageUrls, String mode);

    /**
     * 洗图：分析图片并生成优化提示词
     */
    String polishImage(String originalImageUrl, List<String> charImageUrls, List<String> productImageUrls, String washMode, String customPrompt);

    /**
     * 图片生成
     */
    byte[] generateImage(String originalImageUrl, String finalPrompt);

    /**
     * 图生视频
     */
    byte[] generateVideoFromImage(String imageUrl, String prompt, List<String> referenceImageUrls);

    /**
     * 获取洗图分析模板
     */
    String getImageWashAnalyzeTemplate();

    /**
     * 获取洗图复刻模板
     */
    String getImageWashRestyleTemplate();

    Map<String, String> getImageWashTemplates();
}
