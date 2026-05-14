package cn.iocoder.yudao.module.ai.service.image;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.AiImageDrawReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.AiImagePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.AiImagePublicPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.AiImageUpdateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.midjourney.AiMidjourneyActionReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.midjourney.AiMidjourneyImagineReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.geekai.AiGeekAiImagineReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.nanobanana.AiGenAiImagineReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.image.AiImageDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.image.AiImageMapper;
import cn.iocoder.yudao.module.ai.enums.image.AiImageStatusEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.geekai.api.image.GeekAiGeminiImageApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.genai.api.GenAiApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.midjourney.api.MidjourneyApi;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.siliconflow.SiliconFlowImageOptions;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.qianfan.QianFanImageOptions;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 绘画 Service 实现类
 *
 * @author fansili
 */
@Service
@Slf4j
public class AiImageServiceImpl implements AiImageService {

    @Resource
    private AiModelService modelService;

    @Resource
    private AiImageMapper imageMapper;

    @Resource
    private FileApi fileApi;

    @Override
    public PageResult<AiImageDO> getImagePageMy(Long userId, AiImagePageReqVO pageReqVO) {
        return imageMapper.selectPageMy(userId, pageReqVO);
    }

    @Override
    public PageResult<AiImageDO> getImagePagePublic(AiImagePublicPageReqVO pageReqVO) {
        return imageMapper.selectPage(pageReqVO);
    }

    @Override
    public AiImageDO getImage(Long id) {
        return imageMapper.selectById(id);
    }

    @Override
    public List<AiImageDO> getImageList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return imageMapper.selectByIds(ids);
    }

    @Override
    public Long drawImage(Long userId, AiImageDrawReqVO drawReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(drawReqVO.getModelId());

        // 2. 保存数据库
        AiImageDO image = BeanUtils.toBean(drawReqVO, AiImageDO.class).setUserId(userId)
                .setPlatform(model.getPlatform()).setModelId(model.getId()).setModel(model.getModel())
                .setPublicStatus(false).setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        imageMapper.insert(image);

        // 3. 异步绘制，后续前端通过返回的 id 进行轮询结果
        getSelf().executeDrawImage(image, drawReqVO, model);
        return image.getId();
    }

    @Async
    public void executeDrawImage(AiImageDO image, AiImageDrawReqVO reqVO, AiModelDO model) {
        try {
            // 1.1 构建请求
            ImageOptions request = buildImageOptions(reqVO, model);
            // 1.2 执行请求
            ImageModel imageModel = modelService.getImageModel(model.getId());
            ImageResponse response = imageModel.call(new ImagePrompt(reqVO.getPrompt(), request));
            if (response.getResult() == null) {
                throw new IllegalArgumentException("生成结果为空");
            }

            // 2. 上传到文件服务
            String b64Json = response.getResult().getOutput().getB64Json();
            byte[] fileContent = StrUtil.isNotEmpty(b64Json) ? Base64.decode(b64Json)
                    : HttpUtil.downloadBytes(response.getResult().getOutput().getUrl());
            String filePath = fileApi.createFile(fileContent);

            // 3. 更新数据库
            imageMapper.updateById(new AiImageDO().setId(image.getId()).setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setPicUrl(filePath).setFinishTime(LocalDateTime.now()));
        } catch (Exception ex) {
            log.error("[executeDrawImage][image({}) 生成异常]", image, ex);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(ex.getMessage()).setFinishTime(LocalDateTime.now()));
        }
    }

    private static ImageOptions buildImageOptions(AiImageDrawReqVO draw, AiModelDO model) {
        if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.OPENAI.getPlatform())) {
            // https://platform.openai.com/docs/api-reference/images/create
            return OpenAiImageOptions.builder().model(model.getModel())
                    .height(draw.getHeight()).width(draw.getWidth())
                    .style(MapUtil.getStr(draw.getOptions(), "style")) // 风格
                    .responseFormat("b64_json")
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.SILICON_FLOW.getPlatform())) {
            // https://docs.siliconflow.cn/cn/api-reference/images/images-generations
            return SiliconFlowImageOptions.builder().model(model.getModel())
                    .height(draw.getHeight()).width(draw.getWidth())
                    .build();
        }  else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.STABLE_DIFFUSION.getPlatform())) {
            // https://platform.stability.ai/docs/api-reference#tag/SDXL-and-SD1.6/operation/textToImage
            // https://platform.stability.ai/docs/api-reference#tag/Text-to-Image/operation/textToImage
            return StabilityAiImageOptions.builder().model(model.getModel())
                    .height(draw.getHeight()).width(draw.getWidth())
                    .seed(Long.valueOf(draw.getOptions().get("seed")))
                    .cfgScale(Float.valueOf(draw.getOptions().get("scale")))
                    .steps(Integer.valueOf(draw.getOptions().get("steps")))
                    .sampler(String.valueOf(draw.getOptions().get("sampler")))
                    .stylePreset(String.valueOf(draw.getOptions().get("stylePreset")))
                    .clipGuidancePreset(String.valueOf(draw.getOptions().get("clipGuidancePreset")))
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.TONG_YI.getPlatform())) {
            return DashScopeImageOptions.builder()
                    .withModel(model.getModel()).withN(1)
                    .withHeight(draw.getHeight()).withWidth(draw.getWidth())
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.YI_YAN.getPlatform())) {
            return QianFanImageOptions.builder()
                    .model(model.getModel()).N(1)
                    .height(draw.getHeight()).width(draw.getWidth())
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.ZHI_PU.getPlatform())) {
            return ZhiPuAiImageOptions.builder()
                    .model(model.getModel())
                    .build();
        }
        throw new IllegalArgumentException("不支持的 AI 平台：" + model.getPlatform());
    }

    @Override
    public void deleteImageMy(Long id, Long userId) {
        // 1. 校验是否存在
        AiImageDO image = validateImageExists(id);
        if (ObjUtil.notEqual(image.getUserId(), userId)) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        // 2. 删除记录
        imageMapper.deleteById(id);
    }

    @Override
    public PageResult<AiImageDO> getImagePage(AiImagePageReqVO pageReqVO) {
        return imageMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateImage(AiImageUpdateReqVO updateReqVO) {
        // 1. 校验存在
        validateImageExists(updateReqVO.getId());
        // 2. 更新发布状态
        imageMapper.updateById(BeanUtils.toBean(updateReqVO, AiImageDO.class));
    }

    @Override
    public void deleteImage(Long id) {
        // 1. 校验存在
        validateImageExists(id);
        // 2. 删除
        imageMapper.deleteById(id);
    }

    private AiImageDO validateImageExists(Long id) {
        AiImageDO image = imageMapper.selectById(id);
        if (image == null) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        return image;
    }

    // ================ midjourney 专属 ================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long midjourneyImagine(Long userId, AiMidjourneyImagineReqVO drawReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(drawReqVO.getModelId());
        Assert.equals(model.getPlatform(), AiPlatformEnum.MIDJOURNEY.getPlatform(), "平台不匹配");
        MidjourneyApi midjourneyApi = modelService.getMidjourneyApi(model.getId());

        // 2. 保存数据库
        AiImageDO image = BeanUtils.toBean(drawReqVO, AiImageDO.class).setUserId(userId).setPublicStatus(false)
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(AiPlatformEnum.MIDJOURNEY.getPlatform()).setModelId(model.getId()).setModel(model.getName());
        imageMapper.insert(image);

        // 3. 调用 Midjourney Proxy 提交任务
        List<String> base64Array = StrUtil.isBlank(drawReqVO.getReferImageUrl()) ? null :
                Collections.singletonList("data:image/jpeg;base64,".concat(Base64.encode(HttpUtil.downloadBytes(drawReqVO.getReferImageUrl()))));
        MidjourneyApi.ImagineRequest imagineRequest = new MidjourneyApi.ImagineRequest(
                base64Array, drawReqVO.getPrompt(),null,
                MidjourneyApi.ImagineRequest.buildState(drawReqVO.getWidth(),
                        drawReqVO.getHeight(), drawReqVO.getVersion(), model.getModel()));
        MidjourneyApi.SubmitResponse imagineResponse = midjourneyApi.imagine(imagineRequest);

        // 4.1 情况一【失败】：抛出业务异常
        if (!MidjourneyApi.SubmitCodeEnum.SUCCESS_CODES.contains(imagineResponse.code())) {
            String description = imagineResponse.description().contains("quota_not_enough") ?
                    "账户余额不足" : imagineResponse.description();
            throw exception(IMAGE_MIDJOURNEY_SUBMIT_FAIL, description);
        }

        // 4.2 情况二【成功】：更新 taskId 和参数
        imageMapper.updateById(new AiImageDO().setId(image.getId())
                .setTaskId(imagineResponse.result()).setOptions(BeanUtil.beanToMap(drawReqVO)));
        return image.getId();
    }

    @Override
    public Integer midjourneySync() {
        // 1.1 获取 Midjourney 平台，状态在 “进行中” 的 image
        List<AiImageDO> images = imageMapper.selectListByStatusAndPlatform(
                AiImageStatusEnum.IN_PROGRESS.getStatus(), AiPlatformEnum.MIDJOURNEY.getPlatform());
        if (CollUtil.isEmpty(images)) {
            return 0;
        }
        // 1.2 调用 Midjourney Proxy 获取任务进展
        MidjourneyApi midjourneyApi = modelService.getMidjourneyApi(images.get(0).getModelId());
        List<MidjourneyApi.Notify> taskList = midjourneyApi.getTaskList(convertSet(images, AiImageDO::getTaskId));
        Map<String, MidjourneyApi.Notify> taskMap = convertMap(taskList, MidjourneyApi.Notify::id);

        // 2. 逐个处理，更新进展
        int count = 0;
        for (AiImageDO image : images) {
            MidjourneyApi.Notify notify = taskMap.get(image.getTaskId());
            if (notify == null) {
                log.error("[midjourneySync][image({}) 查询不到进展]", image);
                continue;
            }
            count++;
            updateMidjourneyStatus(image, notify);
        }
        return count;
    }

    @Override
    public void midjourneyNotify(MidjourneyApi.Notify notify) {
        // 1. 校验 image 存在
        AiImageDO image = imageMapper.selectByTaskId(notify.id());
        if (image == null) {
            log.warn("[midjourneyNotify][回调任务({}) 不存在]", notify.id());
            return;
        }
        // 2. 更新状态
        updateMidjourneyStatus(image, notify);
    }

    private void updateMidjourneyStatus(AiImageDO image, MidjourneyApi.Notify notify) {
        // 1. 转换状态
        Integer status = null;
        LocalDateTime finishTime = null;
        if (StrUtil.isNotBlank(notify.status())) {
            MidjourneyApi.TaskStatusEnum taskStatusEnum = MidjourneyApi.TaskStatusEnum.valueOf(notify.status());
            if (MidjourneyApi.TaskStatusEnum.SUCCESS == taskStatusEnum) {
                status = AiImageStatusEnum.SUCCESS.getStatus();
                finishTime = LocalDateTime.now();
            } else if (MidjourneyApi.TaskStatusEnum.FAILURE == taskStatusEnum) {
                status = AiImageStatusEnum.FAIL.getStatus();
                finishTime = LocalDateTime.now();
            }
        }

        // 2. 上传图片
        String picUrl = null;
        if (StrUtil.isNotBlank(notify.imageUrl())) {
            try {
                picUrl = fileApi.createFile(HttpUtil.downloadBytes(notify.imageUrl()));
            } catch (Exception e) {
                picUrl = notify.imageUrl();
                log.warn("[updateMidjourneyStatus][图片({}) 地址({}) 上传失败]", image.getId(), notify.imageUrl(), e);
            }
        }

        // 3. 更新 image 状态
        imageMapper.updateById(new AiImageDO().setId(image.getId()).setStatus(status)
                .setPicUrl(picUrl).setButtons(notify.buttons()).setErrorMessage(notify.failReason())
                .setFinishTime(finishTime));
    }

    @Override
    public Long midjourneyAction(Long userId, AiMidjourneyActionReqVO reqVO) {
        // 1.1 检查 image
        AiImageDO image = validateImageExists(reqVO.getId());
        if (ObjUtil.notEqual(userId, image.getUserId())) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        MidjourneyApi midjourneyApi = modelService.getMidjourneyApi(image.getModelId());
        // 1.2 检查 customId
        MidjourneyApi.Button button = CollUtil.findOne(image.getButtons(),
                buttonX -> buttonX.customId().equals(reqVO.getCustomId()));
        if (button == null) {
            throw exception(IMAGE_CUSTOM_ID_NOT_EXISTS);
        }

        // 2. 调用 Midjourney Proxy 提交任务
        MidjourneyApi.SubmitResponse actionResponse = midjourneyApi.action(
                new MidjourneyApi.ActionRequest(button.customId(), image.getTaskId(), null));
        if (!MidjourneyApi.SubmitCodeEnum.SUCCESS_CODES.contains(actionResponse.code())) {
            String description = actionResponse.description().contains("quota_not_enough") ?
                    "账户余额不足" : actionResponse.description();
            throw exception(IMAGE_MIDJOURNEY_SUBMIT_FAIL, description);
        }

        // 3. 新增 image 记录
        AiImageDO newImage = new AiImageDO().setUserId(image.getUserId()).setPublicStatus(false).setPrompt(image.getPrompt())
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(AiPlatformEnum.MIDJOURNEY.getPlatform())
                .setModel(image.getModel()).setWidth(image.getWidth()).setHeight(image.getHeight())
                .setOptions(image.getOptions()).setTaskId(actionResponse.result());
        imageMapper.insert(newImage);
        return newImage.getId();
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AiImageServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long genAiImagine(Long userId, AiGenAiImagineReqVO drawReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(drawReqVO.getModelId());
        Assert.equals(model.getPlatform(), AiPlatformEnum.GEMINI.getPlatform(), "平台不匹配");

        // 2. 保存数据库
        AiImageDO image = BeanUtils.toBean(drawReqVO, AiImageDO.class).setUserId(userId).setPublicStatus(false)
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(AiPlatformEnum.GEMINI.getPlatform()).setModelId(model.getId()).setModel(model.getName());
        imageMapper.insert(image);

        // 3. 异步绘制
        getSelf().executeGenAiImagine(image, drawReqVO, model);
        return image.getId();
    }

    @Async
    public void executeGenAiImagine(AiImageDO image, AiGenAiImagineReqVO drawReqVO, AiModelDO model) {
        try {
            // 1.1 获取客户端
            GenAiApi genAiApi = modelService.getGenAiApi(model.getId());
            // 1.2 下载参考图
            byte[] imageBytes = null;
            if (StrUtil.isNotBlank(drawReqVO.getReferImageUrl())) {
                imageBytes = HttpUtil.downloadBytes(drawReqVO.getReferImageUrl());
            }

            // 2. 执行请求
            GenerateContentResponse response = genAiApi.generateContent(model.getModel(), drawReqVO.getPrompt(), imageBytes);

            // 3. 处理结果
            byte[] generatedImage = null;
            for (Part part : response.parts()) {
                if (part.inlineData().isPresent() && part.inlineData().get().data().isPresent()) {
                    generatedImage = part.inlineData().get().data().get();
                    break;
                }
            }

            if (generatedImage == null) {
                throw new IllegalArgumentException("生成图片失败，响应中未包含图片数据");
            }

            // 4. 上传到文件服务
            String filePath = fileApi.createFile(generatedImage);

            // 5. 更新数据库
            imageMapper.updateById(new AiImageDO().setId(image.getId()).setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setPicUrl(filePath).setFinishTime(LocalDateTime.now()));
        } catch (Exception ex) {
            log.error("[executeGenAiImagine][image({}) 生成异常]", image, ex);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(ex.getMessage()).setFinishTime(LocalDateTime.now()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long geekAiGeminiImagine(Long userId, AiGeekAiImagineReqVO drawReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(drawReqVO.getModelId());
        Assert.equals(model.getPlatform(), AiPlatformEnum.GeekAI.getPlatform(), "平台不匹配");

        // 2. 保存数据库
        AiImageDO image = BeanUtils.toBean(drawReqVO, AiImageDO.class).setUserId(userId).setPublicStatus(false)
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(AiPlatformEnum.GeekAI.getPlatform()).setModelId(model.getId()).setModel(model.getName())
                .setOptions(cn.hutool.core.bean.BeanUtil.beanToMap(drawReqVO));
        imageMapper.insert(image);

        // 3. 异步绘制
        getSelf().executeGeekAiImagine(image, drawReqVO, model);
        return image.getId();
    }

    @Async
    public void executeGeekAiImagine(AiImageDO image, AiGeekAiImagineReqVO drawReqVO, AiModelDO model) {
        try {
            // 1.1 获取客户端
            GeekAiGeminiImageApi geekAiGeminiImageApi = modelService.getGeekAiGeminiImageApi(model.getId());

            // 2.1 构建请求内容
            List<GeekAiGeminiImageApi.Part> parts = new ArrayList<>();
            parts.add(GeekAiGeminiImageApi.Part.fromText(drawReqVO.getPrompt()));
            if (cn.hutool.core.collection.CollUtil.isNotEmpty(drawReqVO.getReferImageUrls())) {
                for (String referImageUrl : drawReqVO.getReferImageUrls()) {
                    byte[] imageBytes = HttpUtil.downloadBytes(referImageUrl);
                    String mimeType = "image/png";
                    if (referImageUrl.toLowerCase().contains(".jpg") || referImageUrl.toLowerCase().contains(".jpeg")) {
                        mimeType = "image/jpeg";
                    }
                    parts.add(GeekAiGeminiImageApi.Part.fromImage(mimeType, imageBytes));
                }
            }

            // 2.2 构建请求配置
            List<String> responseModalities = drawReqVO.getResponseModalities();
            if (cn.hutool.core.collection.CollUtil.isEmpty(responseModalities)) {
                responseModalities = Collections.singletonList("IMAGE");
            }
            GeekAiGeminiImageApi.GenerationConfig.GenerationConfigBuilder configBuilder = GeekAiGeminiImageApi.GenerationConfig.builder()
                    .responseModalities(responseModalities);

            // 构建图片配置
            GeekAiGeminiImageApi.ImageConfig.ImageConfigBuilder imageConfigBuilder = GeekAiGeminiImageApi.ImageConfig.builder();
            // 比例：优先使用前端传的，否则根据宽高计算
            String aspectRatio = drawReqVO.getAspectRatio();
            if (StrUtil.isEmpty(aspectRatio)) {
                aspectRatio = drawReqVO.getWidth().equals(drawReqVO.getHeight()) ? "1:1" :
                        (drawReqVO.getWidth() > drawReqVO.getHeight() ? "16:9" : "9:16");
            }
            imageConfigBuilder.aspectRatio(aspectRatio);
            // 图片大小：优先使用前端传的，默认 1K
            imageConfigBuilder.imageSize(StrUtil.blankToDefault(drawReqVO.getImageSize(), "1K"));
            configBuilder.imageConfig(imageConfigBuilder.build());

            // 安全设置
            List<GeekAiGeminiImageApi.SafetySetting> safetySettings = null;
            if (cn.hutool.core.collection.CollUtil.isNotEmpty(drawReqVO.getSafetySettings())) {
                safetySettings = BeanUtils.toBean(drawReqVO.getSafetySettings(), GeekAiGeminiImageApi.SafetySetting.class);
            }

            GeekAiGeminiImageApi.GenerateContentRequest request = GeekAiGeminiImageApi.GenerateContentRequest.builder()
                    .contents(Collections.singletonList(GeekAiGeminiImageApi.Content.builder()
                            .role("user")
                            .parts(parts)
                            .build()))
                    .generationConfig(configBuilder.build())
                    .safetySettings(safetySettings)
                    .build();

            // 3. 执行请求
            GeekAiGeminiImageApi.GenerateContentResponse response = geekAiGeminiImageApi.generateContent(model.getModel(), request);

            // 4. 解析结果
            byte[] generatedImage = null;
            String picUrl = null;
            if (response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                GeekAiGeminiImageApi.Candidate candidate = response.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                    for (GeekAiGeminiImageApi.Part part : candidate.getContent().getParts()) {
                        // 情况一：返回 Base64 数据
                        if (part.getInlineData() != null && StrUtil.isNotEmpty(part.getInlineData().getData())) {
                            generatedImage = Base64.decode(part.getInlineData().getData());
                            break;
                        }
                        // 情况二：返回 Markdown 格式的图片链接，例如：![image](https://...)
                        if (StrUtil.isNotEmpty(part.getText())) {
                            String text = part.getText().trim();
                            if (text.startsWith("![image](") && text.endsWith(")")) {
                                picUrl = text.substring(9, text.length() - 1);
                                break;
                            } else if (HttpUtil.isHttp(text) || HttpUtil.isHttps(text)) {
                                picUrl = text;
                                break;
                            }
                        }
                    }
                }
            }

            if (generatedImage == null && StrUtil.isNotEmpty(picUrl)) {
                // 如果返回的是 URL，则下载图片
                generatedImage = HttpUtil.downloadBytes(picUrl);
            }

            if (generatedImage == null) {
                throw new IllegalArgumentException("生成图片失败，响应中未包含图片数据");
            }

            // 5. 上传到文件服务
            String filePath = fileApi.createFile(generatedImage);

            // 6. 更新数据库
            imageMapper.updateById(new AiImageDO().setId(image.getId()).setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setPicUrl(filePath).setFinishTime(LocalDateTime.now()));
        } catch (Exception ex) {
            log.error("[executeGeekAiImagine][image({}) 生成异常]", image, ex);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(ex.getMessage()).setFinishTime(LocalDateTime.now()));
        }
    }

}
