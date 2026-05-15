package cn.iocoder.yudao.module.business.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.business.service.IBizGeminiVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Gemini视频分析")
@RestController
@RequestMapping("/business/gemini")
@Validated
public class BizGeminiVideoController {

    @Resource
    private IBizGeminiVideoService geminiVideoService;

    @PostMapping(value = "/analyze", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "视频内容分析", description = "上传视频文件，支持三种模式：fast (快速), thinking (深度思考), pro (高级专业)")
    public CommonResult<String> analyzeVideo(
            @Parameter(description = "视频文件") @RequestPart("file") MultipartFile file,
            @RequestParam(value = "prompt", defaultValue = "请详细描述这段视频的内容。") String prompt,
            @RequestParam(value = "mode", defaultValue = "fast") String mode) {
        return success(geminiVideoService.analyzeVideo(file, prompt, mode));
    }

}
