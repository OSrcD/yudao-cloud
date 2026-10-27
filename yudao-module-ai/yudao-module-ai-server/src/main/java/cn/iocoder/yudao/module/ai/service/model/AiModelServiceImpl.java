package cn.iocoder.yudao.module.ai.service.model;

import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.AiModelFactory;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.geekai.api.image.GeekAiGeminiImageApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.geekai.api.video.GeekAiVideoVeoApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.genai.api.GenAiApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.midjourney.api.MidjourneyApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.suno.api.SunoApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.jev.api.JevApi;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.model.AiChatMapper;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import dev.tinyflow.core.Tinyflow;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 模型 Service 实现类
 *
 * @author fansili
 */
@Service
@Validated
@Slf4j
public class AiModelServiceImpl implements AiModelService {

    @Resource
    private AiApiKeyService apiKeyService;

    @Resource
    private AiChatMapper modelMapper;

    @Resource
    private AiModelFactory modelFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createModel(AiModelSaveReqVO createReqVO) {
        // 1. 校验
        AiPlatformEnum.validatePlatform(createReqVO.getPlatform());
        apiKeyService.validateApiKey(createReqVO.getKeyId());

        // 2. 插入
        AiModelDO model = BeanUtils.toBean(createReqVO, AiModelDO.class);
        modelMapper.insert(model);
        if (Boolean.TRUE.equals(createReqVO.getIsDefault())) {
            setDefaultModel(model.getId());
        }
        return model.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateModel(AiModelSaveReqVO updateReqVO) {
        // 1. 校验
        validateModelExists(updateReqVO.getId());
        AiPlatformEnum.validatePlatform(updateReqVO.getPlatform());
        apiKeyService.validateApiKey(updateReqVO.getKeyId());

        // 2. 更新
        AiModelDO updateObj = BeanUtils.toBean(updateReqVO, AiModelDO.class);
        modelMapper.updateById(updateObj);
        if (Boolean.TRUE.equals(updateReqVO.getIsDefault())) {
            setDefaultModel(updateReqVO.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultModel(Long id) {
        AiModelDO model = validateModelExists(id);
        // 将同类型同终端的其他模型 isDefault 置为 false
        modelMapper.update(null, new LambdaUpdateWrapper<AiModelDO>()
                .eq(AiModelDO::getType, model.getType())
                .eq(AiModelDO::getClientType, model.getClientType())
                .set(AiModelDO::getIsDefault, false));
        // 将当前模型置为默认，并将 sort 设为 1
        modelMapper.update(null, new LambdaUpdateWrapper<AiModelDO>()
                .eq(AiModelDO::getId, id)
                .set(AiModelDO::getIsDefault, true)
                .set(AiModelDO::getSort, 1));
    }

    @Override
    public void deleteModel(Long id) {
        // 校验存在
        validateModelExists(id);
        // 删除
        modelMapper.deleteById(id);
    }

    private AiModelDO validateModelExists(Long id) {
        AiModelDO model = modelMapper.selectById(id);
        if (modelMapper.selectById(id) == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        return model;
    }

    @Override
    public AiModelDO getModel(Long id) {
        return modelMapper.selectById(id);
    }

    @Override
    public AiModelDO getRequiredDefaultModel(Integer type) {
        AiModelDO model = modelMapper.selectFirstByStatus(type, CommonStatusEnum.ENABLE.getStatus());
        if (model == null) {
            throw exception(MODEL_DEFAULT_NOT_EXISTS);
        }
        return model;
    }

    @Override
    public PageResult<AiModelDO> getModelPage(AiModelPageReqVO pageReqVO) {
        return modelMapper.selectPage(pageReqVO);
    }

    @Override
    public AiModelDO validateModel(Long id) {
        AiModelDO model = validateModelExists(id);
        if (CommonStatusEnum.isDisable(model.getStatus())) {
            throw exception(MODEL_DISABLE);
        }
        return model;
    }

    @Override
    public List<AiModelDO> getModelListByStatusAndType(Integer status, Integer type, String platform) {
        return getModelListByStatusAndType(status, type, platform, null);
    }

    @Override
    public List<AiModelDO> getModelListByStatusAndType(Integer status, Integer type, String platform, String clientType) {
        return modelMapper.selectListByStatusAndType(status, type, platform, clientType);
    }

    // ========== 与 Spring AI 集成 ==========

    @Override
    public ChatModel getChatModel(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public ChatModel getChatModelByApiKey(Long id, String customApiKey) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, customApiKey, apiKey.getUrl());
    }

    /**
     * 图一 commandcode 模型段式调用本地兼容 API 地址
     */
    private static final String COMMAND_CODE_COMPAT_URL = "http://127.0.0.1:3050";

    @Override
    public ChatModel getChatModelForSyncSend(Long id, String customApiKey) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        String effectiveApiKey = StrUtil.isNotBlank(customApiKey) ? customApiKey : apiKey.getApiKey();
        String url = apiKey.getUrl();
        // 版本兼容：段式发送（/send）如命中 commandcode 模型/密钥，强制将 API 请求地址定向为 http://127.0.0.1:3050
        if (isCommandCodeModel(model, apiKey, customApiKey)) {
            url = COMMAND_CODE_COMPAT_URL;
            log.info("[getChatModelForSyncSend] 命中 commandcode 兼容模型 [modelId={}, model={}, apiKeyName={}]，段式请求 API 地址已重定向为: {}",
                    id, model.getModel(), apiKey.getName(), url);
        }
        return modelFactory.getOrCreateChatModel(platform, effectiveApiKey, url);
    }

    private boolean isCommandCodeModel(AiModelDO model, AiApiKeyDO apiKey, String customApiKey) {
        if (apiKey != null) {
            if (StrUtil.equalsIgnoreCase(apiKey.getName(), "commandcode")
                    || StrUtil.equalsIgnoreCase(apiKey.getApiKey(), "commandcode")
                    || StrUtil.equalsIgnoreCase(apiKey.getPlatform(), "CommandCode")) {
                return true;
            }
        }
        if (model != null) {
            if (StrUtil.containsIgnoreCase(model.getModel(), "commandcode")
                    || StrUtil.containsIgnoreCase(model.getName(), "commandcode")
                    || StrUtil.equalsIgnoreCase(model.getPlatform(), "CommandCode")) {
                return true;
            }
        }
        if (StrUtil.equalsIgnoreCase(customApiKey, "commandcode")) {
            return true;
        }
        return false;
    }

    @Override
    public ImageModel getImageModel(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateImageModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public MidjourneyApi getMidjourneyApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateMidjourneyApi(apiKey.getApiKey(), apiKey.getUrl());
    }


    @Override
    public GenAiApi getGenAiApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateGenAiApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public GeekAiGeminiImageApi getGeekAiGeminiImageApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateGeekAiGeminiImageApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public GeekAiVideoVeoApi getGeekAiVideoVeoApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateGeekAiVideoVeoApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public cn.iocoder.yudao.module.ai.framework.ai.core.model.aihubmix.api.video.AihubmixVideoApi getAihubmixVideoApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateAihubmixVideoApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public SunoApi getSunoApi() {
        AiApiKeyDO apiKey = apiKeyService.getRequiredDefaultApiKey(
                AiPlatformEnum.SUNO.getPlatform(), CommonStatusEnum.ENABLE.getStatus());
        return modelFactory.getOrCreateSunoApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public VectorStore getOrCreateVectorStore(Long id, Map<String, Class<?>> metadataFields) {
        // 获取模型 + 密钥
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());

        // 创建或获取 EmbeddingModel 对象
        EmbeddingModel embeddingModel = modelFactory.getOrCreateEmbeddingModel(
                platform, apiKey.getApiKey(), apiKey.getUrl(), model.getModel());

        // 创建或获取 VectorStore 对象
         return modelFactory.getOrCreateVectorStore(SimpleVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(QdrantVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(RedisVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(MilvusVectorStore.class, embeddingModel, metadataFields);
    }

    // TODO @lesan：是不是返回 Llm 对象会好点哈？
    @Override
    public void getLLmProvider4Tinyflow(Tinyflow tinyflow, Long modelId) {
        AiModelDO model = validateModel(modelId);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        switch (platform) {
            // TODO @lesan 考虑到未来不需要使用agents-flex 现在仅测试通义千问
            // TODO @lesan：【重要】是不是可以实现一个 SpringAiLlm，这样的话，内部全部用它就好了。只实现 chat 部分；这样，就把 flex 作为一个 agent 框架，内部调用，还是 spring ai 相关的。成本可能低一点？！
            case TONG_YI:
                QwenLlmConfig qwenLlmConfig = new QwenLlmConfig();
                qwenLlmConfig.setApiKey(apiKey.getApiKey());
                qwenLlmConfig.setModel(model.getModel());
                // TODO @lesan：这个有点奇怪。。。如果一个链式里，有多个模型，咋整呀。。。
                tinyflow.setLlmProvider(id -> new QwenLlm(qwenLlmConfig));
                break;
            case OLLAMA:
                OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
                ollamaLlmConfig.setEndpoint(apiKey.getUrl());
                ollamaLlmConfig.setModel(model.getModel());
                tinyflow.setLlmProvider(id -> new OllamaLlm(ollamaLlmConfig));
                break;
        }
    }

    @Override
    public JevApi getJevApi(Long id) {
        return getJevApi(id, null);
    }

    @Override
    public JevApi getJevApi(Long id, String customApiKey) {
        if (id != null) {
            AiModelDO model = validateModel(id);
            AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
            String key = StrUtil.isNotBlank(customApiKey) ? customApiKey : apiKey.getApiKey();
            return modelFactory.getOrCreateJevApi(key, apiKey.getUrl());
        }
        // 如果未指定 modelId，尝试获取 COMMAND_CODE 平台的默认 apiKey
        AiApiKeyDO apiKey = null;
        try {
            apiKey = apiKeyService.getRequiredDefaultApiKey(
                    AiPlatformEnum.COMMAND_CODE.getPlatform(), CommonStatusEnum.ENABLE.getStatus());
        } catch (Exception ignored) {
        }
        String url = apiKey != null ? apiKey.getUrl() : null;
        String key = StrUtil.isNotBlank(customApiKey) ? customApiKey : (apiKey != null ? apiKey.getApiKey() : null);
        if (StrUtil.isBlank(key)) {
            throw exception(JEV_KEY_NOT_CONFIGURED);
        }
        return modelFactory.getOrCreateJevApi(key, url);
    }

}