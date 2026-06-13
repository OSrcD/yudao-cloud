package cn.iocoder.yudao.module.ai.service.video;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.admin.image.vo.geekai.AiGeekAiImagineReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoAihubmixSubmitReqVO;
import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceReqVO;
import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO;
import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceConfigRespVO;
import cn.iocoder.yudao.module.business.api.video.BizVideoReproduceApi;
import cn.iocoder.yudao.module.business.api.video.dto.BizVideoReproduceFrameDTO;
import cn.iocoder.yudao.module.business.api.video.dto.BizVideoReproduceTaskDTO;
import cn.iocoder.yudao.module.ai.service.chat.AiChatMessageService;
import cn.iocoder.yudao.module.ai.service.image.AiImageService;
import cn.iocoder.yudao.module.member.api.point.MemberPointApi;
import cn.iocoder.yudao.module.member.enums.point.MemberPointBizTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceCreateRespVO;
import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import java.time.LocalDateTime;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.AI_BALANCE_NOT_ENOUGH;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.AI_POINT_NOT_ENOUGH;
import cn.iocoder.yudao.module.pay.api.wallet.PayWalletApi;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import cn.iocoder.yudao.module.pay.enums.wallet.PayWalletBizTypeEnum;

@Service
@Slf4j
public class BizAiVideoReproduceServiceImpl implements BizAiVideoReproduceService {

    @Resource
    private BizVideoReproduceApi bizVideoReproduceApi;

    @Resource
    private AiChatMessageService aiChatMessageService;

    @Resource
    private AiImageService aiImageService;

    @Resource
    private BizAiVideoService bizAiVideoService;

    @Resource
    private MemberPointApi memberPointApi;

    @Resource
    private PayWalletApi payWalletApi;

    @Resource
    private PayOrderApi payOrderApi;

    @Resource
    private cn.iocoder.yudao.module.ai.dal.mysql.video.BizAiVideoMapper bizAiVideoMapper;

    @Resource
    private cn.iocoder.yudao.module.ai.service.model.AiModelService aiModelService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private cn.iocoder.yudao.module.infra.api.file.FileApi fileApi;

    @Value("${yudao.ai.ffmpeg-path:ffmpeg}")
    private String ffmpegPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAiVideoReproduceCreateRespVO createTask(Long userId, AppAiVideoReproduceReqVO reqVO) {
        // 1. 创建任务记录，设置初始状态（未支付，待付款）
        BizVideoReproduceTaskDTO createDTO = new BizVideoReproduceTaskDTO();
        createDTO.setOriginalVideoUrl(reqVO.getVideoUrl());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getProductConfigJson())) {
            try {
                createDTO.setProductConfigJson(objectMapper.readValue(reqVO.getProductConfigJson(), new TypeReference<Map<String, Object>>() {}));
            } catch (Exception e) {
                log.error("解析 productConfigJson 失败", e);
            }
        }
        createDTO.setCharImages(reqVO.getCharImageUrls());
        createDTO.setProductImages(reqVO.getProductImageUrls());
        createDTO.setExecMode(cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getExecMode()) ? reqVO.getExecMode() : "api");
        createDTO.setStatus("0"); // 0: 队列中
        createDTO.setCreator(String.valueOf(userId));
        createDTO.setPayStatus(false);
        Long taskId = bizVideoReproduceApi.createVideoReproduceTask(createDTO);

        // 2. 创建支付单，金额 1.00元（100分）
        int costPrice = 100;
        Long payOrderId = payOrderApi.createOrder(new PayOrderCreateReqDTO()
                .setAppKey("video_reproduce").setUserIp(ServletUtils.getClientIP()) // 支付应用键为 video_reproduce
                .setUserId(userId).setUserType(UserTypeEnum.MEMBER.getValue()) // 会员用户
                .setMerchantOrderId(taskId.toString()) // 商户关联订单ID
                .setSubject("视频复刻任务").setBody("视频复刻任务消费").setPrice(costPrice) // 价格信息
                .setExpireTime(LocalDateTime.now().plusHours(2))).getCheckedData(); // 过期时间2小时

        // 3. 更新支付单ID到任务记录
        BizVideoReproduceTaskDTO updateDTO = new BizVideoReproduceTaskDTO();
        updateDTO.setId(taskId);
        updateDTO.setPayOrderId(payOrderId);
        bizVideoReproduceApi.updateVideoReproduceTask(updateDTO);

        return new AppAiVideoReproduceCreateRespVO().setId(taskId).setPayOrderId(payOrderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskPaid(Long id, Long payOrderId) {
        // 1. 校验任务是否存在
        BizVideoReproduceTaskDTO task = bizVideoReproduceApi.getVideoReproduceTask(id);
        if (task == null) {
            log.error("[updateTaskPaid][任务({})不存在]", id);
            return;
        }
        // 2. 校验是否已支付过，避免重复处理
        if (Boolean.TRUE.equals(task.getPayStatus())) {
            log.info("[updateTaskPaid][任务({})已支付过，无需重复处理]", id);
            return;
        }

        // 3. 更新任务支付状态
        BizVideoReproduceTaskDTO updateDTO = new BizVideoReproduceTaskDTO();
        updateDTO.setId(id);
        updateDTO.setPayStatus(true);
        updateDTO.setPayTime(LocalDateTime.now());
        
        // 判断执行模式
        if ("local".equalsIgnoreCase(task.getExecMode())) {
            // 本地模式：更新状态为等待本地消费状态 (1)
            updateDTO.setStatus("1");
            bizVideoReproduceApi.updateVideoReproduceTask(updateDTO);
            bizVideoReproduceApi.startLocalAnalyzeWorkflow(id);
            log.info("[updateTaskPaid][任务({})为本地模式，已更新为等待本地消费状态，并已下发至本地队列]", id);
            return;
        }

        // 否则为 api 模式，直接调用后端的异步 Gemini 分析流
        bizVideoReproduceApi.updateVideoReproduceTask(updateDTO);

        Long userId = Long.valueOf(task.getCreator());
        java.util.concurrent.atomic.AtomicBoolean idUpdated = new java.util.concurrent.atomic.AtomicBoolean(false);
        
        String productConfigStr = "";
        if (task.getProductConfigJson() != null) {
            try {
                productConfigStr = objectMapper.writeValueAsString(task.getProductConfigJson());
            } catch (Exception e) {
                log.error("序列化 productConfigJson 失败", e);
            }
        }

        aiChatMessageService.analyzeVideoAndGenerateScript(
                userId, 
                productConfigStr, 
                java.util.Collections.singletonList(task.getOriginalVideoUrl()), 
                task.getCharImages(), 
                task.getProductImages())
                .doOnNext(result -> {
                    if (!idUpdated.get() && result.isSuccess() && result.getData() != null && result.getData().getReceive() != null) {
                        Long conversationId = result.getData().getReceive().getConversationId();
                        if (conversationId != null && idUpdated.compareAndSet(false, true)) {
                            BizVideoReproduceTaskDTO updateTaskDTO = new BizVideoReproduceTaskDTO();
                            updateTaskDTO.setId(id);
                            updateTaskDTO.setConversationId(conversationId);
                            bizVideoReproduceApi.updateVideoReproduceTask(updateTaskDTO);
                        }
                    }
                })
                .doFinally(signalType -> {
                    getSelf().processVideoAnalysisResult(id);
                })
                .subscribe();
    }

    private BizAiVideoReproduceService getSelf() {
        return cn.hutool.extra.spring.SpringUtil.getBean(getClass());
    }

    private String cleanMarkdownJson(String content) {
        if (cn.hutool.core.util.StrUtil.isEmpty(content)) {
            return "";
        }
        content = content.trim();
        if (content.startsWith("```")) {
            int firstNewLine = content.indexOf("\n");
            int lastBackticks = content.lastIndexOf("```");
            if (firstNewLine != -1 && lastBackticks > firstNewLine) {
                content = content.substring(firstNewLine, lastBackticks).trim();
            }
        }
        return content;
    }

    @org.springframework.scheduling.annotation.Async
    public void processVideoAnalysisResult(Long taskId) {
        BizVideoReproduceTaskDTO taskDTO = bizVideoReproduceApi.getVideoReproduceTask(taskId);
        if (taskDTO == null) {
            return;
        }

        String jsonResult = taskDTO.getResultJson();
        boolean isFission = (taskDTO.getProductImages() != null && !taskDTO.getProductImages().isEmpty());

        // 如果 resultJson 为空，尝试从聊天消息中获取并融合
        if (cn.hutool.core.util.StrUtil.isEmpty(jsonResult) && taskDTO.getConversationId() != null) {
            List<cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO> messages = aiChatMessageService.getChatMessageListByConversationId(taskDTO.getConversationId());
            if (cn.hutool.core.collection.CollUtil.isEmpty(messages)) {
                updateTaskStatusError(taskId, "未获取到分析消息");
                return;
            }

            if (isFission) {
                // 裂变模式下：需要融合 Step 4 和 Step 6 的两条消息 JSON
                String gridSuggestionsJson = null;
                String unitsJson = null;
                for (cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO msg : messages) {
                    if (!org.springframework.ai.chat.messages.MessageType.ASSISTANT.getValue().equals(msg.getType())) {
                        continue;
                    }
                    String contentStr = cleanMarkdownJson(msg.getContent());
                    if (contentStr.contains("grid_suggestions")) {
                        gridSuggestionsJson = contentStr;
                    }
                    if (contentStr.contains("units")) {
                        unitsJson = contentStr;
                    }
                }
                
                if (gridSuggestionsJson == null || unitsJson == null) {
                    log.warn("[processVideoAnalysisResult][任务({})尚未生成完整的 Step 4 或 Step 6 消息，可能仍在分析中]", taskId);
                    return; // 依然在进行中，等待下一次触发
                }
                
                try {
                    JsonNode gridNode = objectMapper.readTree(gridSuggestionsJson).path("grid_suggestions");
                    JsonNode unitsNode = objectMapper.readTree(unitsJson).path("units");
                    
                    com.fasterxml.jackson.databind.node.ObjectNode mergedRoot = objectMapper.createObjectNode();
                    mergedRoot.set("grid_suggestions", gridNode);
                    mergedRoot.set("units", unitsNode);
                    
                    jsonResult = objectMapper.writeValueAsString(mergedRoot);
                } catch (Exception e) {
                    updateTaskStatusError(taskId, "融合 Step 4 和 Step 6 JSON 失败: " + e.getMessage());
                    return;
                }
            } else {
                // 传统模式：直接获取最后一条 Assistant 消息
                cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO lastMessage = messages.get(messages.size() - 1);
                if (lastMessage != null && org.springframework.ai.chat.messages.MessageType.ASSISTANT.getValue().equals(lastMessage.getType())) {
                    jsonResult = cleanMarkdownJson(lastMessage.getContent());
                }
            }
        }

        if (cn.hutool.core.util.StrUtil.isEmpty(jsonResult)) {
            updateTaskStatusError(taskId, "生成的 JSON 结果为空");
            return;
        }

        // 保存 resultJson 并更新状态为进行中(2)
        BizVideoReproduceTaskDTO updateDTO = new BizVideoReproduceTaskDTO();
        updateDTO.setId(taskId);
        updateDTO.setResultJson(jsonResult);
        updateDTO.setStatus("2");
        bizVideoReproduceApi.updateVideoReproduceTask(updateDTO);

        try {
            JsonNode root = objectMapper.readTree(jsonResult);

            if (isFission) {
                // 裂变复刻模式解析：不需要 FFmpeg 截帧，直接索引关联
                JsonNode units = root.path("units");
                JsonNode gridSuggestions = root.path("grid_suggestions");
                if (units.isMissingNode() || !units.isArray()) {
                    updateTaskStatusError(taskId, "JSON 结构不正确: 缺失 units 数组");
                    return;
                }

                List<String> productImages = taskDTO.getProductImages();
                int index = 0;
                for (JsonNode unit : units) {
                    String unitId = unit.path("unit_id").asText();
                    
                    // 获取分镜提示词
                    String i2vPromptEn = unit.path("i2v_prompt_for_model_en").isContainerNode() 
                            ? unit.path("i2v_prompt_for_model_en").path("visual_dialogue_sfx").asText() 
                            : unit.path("i2v_prompt_for_model_en").asText();
                    String i2vPromptZh = unit.path("i2v_prompt_zh_check").isContainerNode() 
                            ? unit.path("i2v_prompt_zh_check").path("visual_dialogue_sfx").asText() 
                            : unit.path("i2v_prompt_zh_check").asText();

                    String gridImagePromptEn = unit.path("image_prompt_for_model_en").isContainerNode()
                            ? unit.path("image_prompt_for_model_en").path("visual_dialogue_sfx").asText()
                            : unit.path("image_prompt_for_model_en").asText();
                    String gridImagePromptZh = unit.path("image_prompt_zh_check").isContainerNode()
                            ? unit.path("image_prompt_zh_check").path("visual_dialogue_sfx").asText()
                            : unit.path("image_prompt_zh_check").asText();

                    // 寻找匹配的 grid_suggestion 以提取 source_image_indices
                    List<String> gridSourceImages = new ArrayList<>();
                    if (gridSuggestions.isArray()) {
                        for (JsonNode suggestion : gridSuggestions) {
                            if (unitId.equals(suggestion.path("unit_id").asText())) {
                                JsonNode indicesNode = suggestion.path("source_image_indices");
                                if (indicesNode.isArray() && productImages != null) {
                                    for (JsonNode indexNode : indicesNode) {
                                        int imgIdx = indexNode.asInt();
                                        if (imgIdx >= 0 && imgIdx < productImages.size()) {
                                            gridSourceImages.add(productImages.get(imgIdx));
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }

                    // 创建分镜记录
                    BizVideoReproduceFrameDTO frameDTO = new BizVideoReproduceFrameDTO();
                    frameDTO.setTaskId(taskId);
                    frameDTO.setGuId(unitId);
                    frameDTO.setFrameIndex(index++);
                    frameDTO.setTimestampSec("0"); // 裂变模式无物理时间戳
                    
                    // 用关联的第 1 张商品原图作为占位 originalImageUrl
                    if (!gridSourceImages.isEmpty()) {
                        frameDTO.setOriginalImageUrl(gridSourceImages.get(0));
                    }
                    frameDTO.setI2vPromptEn(i2vPromptEn);
                    frameDTO.setI2vPromptZh(i2vPromptZh);
                    
                    // 特有宫格属性
                    frameDTO.setGridImagePromptEn(gridImagePromptEn);
                    frameDTO.setGridImagePromptZh(gridImagePromptZh);
                    frameDTO.setGridSourceImages(gridSourceImages);
                    
                    frameDTO.setStatus("0"); // 0: 就绪
                    bizVideoReproduceApi.createVideoReproduceFrame(frameDTO);
                }

            } else {
                // 传统模式：下载视频并截帧
                JsonNode guPrompts = root.path("gu_prompts");
                if (!guPrompts.isArray()) {
                    updateTaskStatusError(taskId, "JSON 结构不正确: 缺失 gu_prompts 数组");
                    return;
                }

                // 更新全局锁
                JsonNode globalLockNode = root.path("global_lock_card");
                if (!globalLockNode.isMissingNode() && !globalLockNode.isNull()) {
                    BizVideoReproduceTaskDTO updateTaskDTO = new BizVideoReproduceTaskDTO();
                    updateTaskDTO.setId(taskId);
                    updateTaskDTO.setGlobalLocks(objectMapper.convertValue(globalLockNode, new TypeReference<Map<String, Object>>() {}));
                    bizVideoReproduceApi.updateVideoReproduceTask(updateTaskDTO);
                }

                File tempVideo = File.createTempFile("reproduce_", ".mp4");
                cn.hutool.http.HttpUtil.downloadFile(taskDTO.getOriginalVideoUrl(), tempVideo);
                
                try {
                    int index = 0;
                    for (JsonNode gu : guPrompts) {
                        String guId = gu.path("gu_id").asText();
                        double timestamp = gu.path("reference_frame_info").path("timestamp_sec").asDouble();

                        String frameFileName = "frame_" + taskId + "_" + guId + ".png";
                        File outFrameFile = new File(tempVideo.getParent(), frameFileName);
                        
                        ProcessBuilder pb = new ProcessBuilder(
                            ffmpegPath, "-y", "-ss", String.format("%.3f", timestamp),
                            "-i", tempVideo.getAbsolutePath(), "-vframes", "1", "-q:v", "2", outFrameFile.getAbsolutePath()
                        );
                        pb.redirectErrorStream(true);
                        Process process = pb.start();
                        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                            while (reader.readLine() != null) {}
                        }
                        process.waitFor();

                        if (outFrameFile.exists()) {
                            String frameUrl = fileApi.createFile(cn.hutool.core.io.FileUtil.readBytes(outFrameFile), frameFileName);
                            
                            BizVideoReproduceFrameDTO frameDTO = new BizVideoReproduceFrameDTO();
                            frameDTO.setTaskId(taskId);
                            frameDTO.setGuId(guId);
                            frameDTO.setFrameIndex(index++);
                            frameDTO.setTimestampSec(String.valueOf(timestamp));
                            frameDTO.setOriginalImageUrl(frameUrl);
                            frameDTO.setI2vPromptZh(gu.path("i2v_prompt_zh_check").path("visual_dialogue_sfx").asText());
                            frameDTO.setI2vPromptEn(gu.path("i2v_prompt_for_model_en").path("visual_dialogue_sfx").asText());
                            frameDTO.setStatus("0");
                            bizVideoReproduceApi.createVideoReproduceFrame(frameDTO);
                            
                            cn.hutool.core.io.FileUtil.del(outFrameFile);
                        } else {
                            log.error("FFmpeg 截帧失败: {}, guId: {}", taskId, guId);
                        }
                    }
                } finally {
                    cn.hutool.core.io.FileUtil.del(tempVideo);
                }
            }

            // 更新任务为已完成(3)
            BizVideoReproduceTaskDTO finalUpdate = new BizVideoReproduceTaskDTO();
            finalUpdate.setId(taskId);
            finalUpdate.setStatus("3");
            bizVideoReproduceApi.updateVideoReproduceTask(finalUpdate);
            
        } catch (Exception e) {
            log.error("处理视频分析结果失败", e);
            updateTaskStatusError(taskId, "处理失败: " + e.getMessage());
        }
    }

    private void updateTaskStatusError(Long taskId, String errorMsg) {
        BizVideoReproduceTaskDTO updateDTO = new BizVideoReproduceTaskDTO();
        updateDTO.setId(taskId);
        updateDTO.setStatus("9");
        updateDTO.setErrorMsg(errorMsg);
        bizVideoReproduceApi.updateVideoReproduceTask(updateDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void washFrame(Long userId, Long frameId, Long modelId, Integer width, Integer height, String customPrompt) {
        BizVideoReproduceFrameDTO frameDTO = bizVideoReproduceApi.getVideoReproduceFrame(frameId);
        if (frameDTO == null) {
            return;
        }

        // 扣减钱包余额：极光洗图 0.50元（50分）
        int costPrice = 50;
        PayWalletAddBalanceReqDTO walletReqDTO = new PayWalletAddBalanceReqDTO();
        walletReqDTO.setUserId(userId);
        walletReqDTO.setUserType(1); // Member
        walletReqDTO.setBizType(PayWalletBizTypeEnum.UPDATE_BALANCE.getType());
        walletReqDTO.setBizId("video_reproduce_wash_" + frameId + "_" + System.currentTimeMillis());
        walletReqDTO.setPrice(-costPrice);

        CommonResult<Boolean> walletResult = payWalletApi.addWalletBalance(walletReqDTO);
        if (!walletResult.isSuccess() || Boolean.FALSE.equals(walletResult.getData())) {
            throw exception(AI_BALANCE_NOT_ENOUGH);
        }

        BizVideoReproduceFrameDTO updateDTO = new BizVideoReproduceFrameDTO();
        updateDTO.setId(frameId);
        updateDTO.setStatus("1");
        bizVideoReproduceApi.updateVideoReproduceFrame(updateDTO);

        AiGeekAiImagineReqVO drawReqVO = new AiGeekAiImagineReqVO();
        
        // 区分是否为裂变模式
        boolean isFission = (frameDTO.getGridSourceImages() != null && !frameDTO.getGridSourceImages().isEmpty());
        if (isFission) {
            String prompt = cn.hutool.core.util.StrUtil.isNotEmpty(customPrompt) ? customPrompt :
                    (cn.hutool.core.util.StrUtil.isNotEmpty(frameDTO.getGridImagePromptEn()) 
                    ? frameDTO.getGridImagePromptEn() : frameDTO.getI2vPromptEn());
            drawReqVO.setPrompt(prompt);
            drawReqVO.setReferImageUrls(frameDTO.getGridSourceImages());
        } else {
            String prompt = cn.hutool.core.util.StrUtil.isNotEmpty(customPrompt) ? customPrompt : frameDTO.getI2vPromptEn();
            drawReqVO.setPrompt(prompt);
        }
        
        drawReqVO.setModelId(modelId);
        drawReqVO.setWidth(width);
        drawReqVO.setHeight(height);

        aiImageService.geekAiGeminiImagine(userId, drawReqVO);
    }

    @Override
    public void washFrameLocal(Long userId, Long frameId, String washMode, String customPrompt, List<String> refImages) {
        // 1. 校验帧存在
        BizVideoReproduceFrameDTO frameDTO = bizVideoReproduceApi.getVideoReproduceFrame(frameId);
        if (frameDTO == null) {
            return;
        }

        // 2. 构造 execParams（对应 VideoReproduceAutomation.executeWashImage 入参格式）
        //    mode 以 _pure 结尾时，跳过 AI 分析阶段，直接使用 customPrompt 生图
        String mode = (washMode != null && !washMode.isEmpty()) ? washMode : "fission_pure";
        String prompt = (customPrompt != null && !customPrompt.isEmpty())
                ? customPrompt
                : frameDTO.getGridImagePromptEn();

        List<String> productUrls = (refImages != null && !refImages.isEmpty())
                ? refImages
                : frameDTO.getGridSourceImages();

        // sourceUrl 取第一张商品图（ai-chrome-app 的 executeWashImage 要求必填）
        String sourceUrl = (productUrls != null && !productUrls.isEmpty()) ? productUrls.get(0) : null;
        if (sourceUrl == null) {
            log.warn("[washFrameLocal][帧({})无可用参考图，无法创建本地任务]", frameId);
            return;
        }

        try {
            java.util.Map<String, Object> execParamsMap = new java.util.LinkedHashMap<>();
            execParamsMap.put("mode", mode);
            execParamsMap.put("customPrompt", prompt);
            execParamsMap.put("sourceUrl", sourceUrl);
            
            // 重要：将已经被选为 sourceUrl 的第一张图从 productUrls 列表剔除，
            // 否则 ai-chrome-app 会下载两次并上传两次同样的图片！
            List<String> remainingProductUrls = new java.util.ArrayList<>();
            if (productUrls.size() > 1) {
                remainingProductUrls.addAll(productUrls.subList(1, productUrls.size()));
            }
            execParamsMap.put("productUrls", remainingProductUrls);
            
            String execParamsJson = objectMapper.writeValueAsString(execParamsMap);

            // 3. 更新帧状态为等待中（1）
            BizVideoReproduceFrameDTO updateDTO = new BizVideoReproduceFrameDTO();
            updateDTO.setId(frameId);
            updateDTO.setStatus("1");
            bizVideoReproduceApi.updateVideoReproduceFrame(updateDTO);

            // 4. 入队本地任务，等待 ai-chrome-app 轮询执行
            bizVideoReproduceApi.enqueueWashImageLocalTask(frameId, execParamsJson);
            log.info("[washFrameLocal][帧({})本地洗图任务已入队，mode={}, prompt长度={}]", frameId, mode, prompt.length());
        } catch (Exception e) {
            log.error("[washFrameLocal][帧({})入队失败]", frameId, e);
        }
    }


    @Override
    public void updateFramePrompts(Long userId, cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceUpdatePromptsReqVO reqVO) {
        BizVideoReproduceFrameDTO updateDTO = new BizVideoReproduceFrameDTO();
        updateDTO.setId(reqVO.getFrameId());
        
        if (reqVO.getOriginalPrompt() != null) {
            updateDTO.setOriginalPrompt(reqVO.getOriginalPrompt());
        }
        if (reqVO.getGridImagePromptEn() != null) {
            updateDTO.setGridImagePromptEn(reqVO.getGridImagePromptEn());
        }
        if (reqVO.getGridImagePromptZh() != null) {
            updateDTO.setGridImagePromptZh(reqVO.getGridImagePromptZh());
        }
        if (reqVO.getImagePromptForModelEn() != null) {
            updateDTO.setImagePromptForModelEn(reqVO.getImagePromptForModelEn());
        }
        if (reqVO.getImagePromptZhCheck() != null) {
            updateDTO.setImagePromptZhCheck(reqVO.getImagePromptZhCheck());
        }
        if (reqVO.getI2vPromptEn() != null) {
            updateDTO.setI2vPromptEn(reqVO.getI2vPromptEn());
        }
        if (reqVO.getI2vPromptZh() != null) {
            updateDTO.setI2vPromptZh(reqVO.getI2vPromptZh());
        }
        
        bizVideoReproduceApi.updateVideoReproduceFrame(updateDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateVideo(Long userId, Long frameId, Long modelId, Integer width, Integer height) {
        generateVideo(userId, frameId, modelId, width, height, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateVideo(Long userId, Long frameId, Long modelId, Integer width, Integer height, String inputReference) {
        BizVideoReproduceFrameDTO frameDTO = bizVideoReproduceApi.getVideoReproduceFrame(frameId);
        if (frameDTO == null) {
            return;
        }

        // 扣减钱包余额：视频生成 2.00元（200分）
        int costPrice = 200;
        PayWalletAddBalanceReqDTO walletReqDTO = new PayWalletAddBalanceReqDTO();
        walletReqDTO.setUserId(userId);
        walletReqDTO.setUserType(1); // Member
        walletReqDTO.setBizType(PayWalletBizTypeEnum.UPDATE_BALANCE.getType());
        walletReqDTO.setBizId("video_reproduce_gen_" + frameId + "_" + System.currentTimeMillis());
        walletReqDTO.setPrice(-costPrice);

        CommonResult<Boolean> walletResult = payWalletApi.addWalletBalance(walletReqDTO);
        if (!walletResult.isSuccess() || Boolean.FALSE.equals(walletResult.getData())) {
            throw exception(AI_BALANCE_NOT_ENOUGH);
        }

        BizVideoReproduceFrameDTO updateDTO = new BizVideoReproduceFrameDTO();
        updateDTO.setId(frameId);
        updateDTO.setStatus("3");
        bizVideoReproduceApi.updateVideoReproduceFrame(updateDTO);

        BizAiVideoAihubmixSubmitReqVO videoReqVO = new BizAiVideoAihubmixSubmitReqVO();
        videoReqVO.setPrompt(frameDTO.getI2vPromptEn());
        videoReqVO.setModelId(modelId);
        
        String refUrl = inputReference;
        if (!cn.hutool.core.util.StrUtil.isNotEmpty(refUrl)) {
            refUrl = frameDTO.getPolishedImageUrl();
        }
        if (!cn.hutool.core.util.StrUtil.isNotEmpty(refUrl)) {
            refUrl = frameDTO.getOriginalImageUrl();
        }
        
        videoReqVO.setInputReference(refUrl);
        videoReqVO.setSize(width + "x" + height);
        
        bizAiVideoService.submitAihubmixVideo(userId, videoReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateVideoLocal(Long userId, Long frameId, String inputReference) {
        BizVideoReproduceFrameDTO frameDTO = bizVideoReproduceApi.getVideoReproduceFrame(frameId);
        if (frameDTO == null) {
            return;
        }

        // 构造 execParams（参考 BizVideoReproduceServiceImpl 中的 GEN_VIDEO 逻辑）
        try {
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("prompt", frameDTO.getI2vPromptEn());
            
            // 优先顺序：inputReference > polishedImageUrl > originalImageUrl
            String refUrl = inputReference;
            if (!cn.hutool.core.util.StrUtil.isNotEmpty(refUrl)) {
                refUrl = frameDTO.getPolishedImageUrl();
            }
            if (!cn.hutool.core.util.StrUtil.isNotEmpty(refUrl)) {
                refUrl = frameDTO.getOriginalImageUrl();
            }
            
            List<String> urls = new java.util.ArrayList<>();
            if (refUrl != null) {
                urls.add(refUrl);
            }
            params.put("referenceUrls", urls);
            
            String execParamsJson = objectMapper.writeValueAsString(params);

            // 更新帧状态为处理中
            BizVideoReproduceFrameDTO updateDTO = new BizVideoReproduceFrameDTO();
            updateDTO.setId(frameId);
            updateDTO.setStatus("3");
            bizVideoReproduceApi.updateVideoReproduceFrame(updateDTO);

            // 存入本地队列
            bizVideoReproduceApi.enqueueGenVideoLocalTask(frameDTO.getTaskId(), frameId, execParamsJson);
            log.info("[generateVideoLocal][帧({})本地生成视频任务已入队, referenceUrl={}]", frameId, refUrl);
        } catch (Exception e) {
            log.error("[generateVideoLocal][帧({})本地任务入队失败]", frameId, e);
        }
    }

    @Override
    public AppAiVideoReproduceConfigRespVO getReproduceConfig() {
        AppAiVideoReproduceConfigRespVO respVO = new AppAiVideoReproduceConfigRespVO();
        try {
            cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO imageModel = aiModelService.getRequiredDefaultModel(cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum.IMAGE.getType());
            respVO.setDefaultWashModelId(imageModel.getId());
        } catch (Exception e) {}
        try {
            cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO videoModel = aiModelService.getRequiredDefaultModel(cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum.VIDEO.getType());
            respVO.setDefaultVideoModelId(videoModel.getId());
        } catch (Exception e) {}
        return respVO;
    }

    @Override
    public AppAiVideoReproduceTaskDetailRespVO getTaskDetail(Long userId, Long taskId) {
        BizVideoReproduceTaskDTO taskDTO = bizVideoReproduceApi.getVideoReproduceTask(taskId);
        if (taskDTO == null || !java.util.Objects.equals(taskDTO.getCreator(), String.valueOf(userId))) {
            throw exception(cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.IMAGE_NOT_EXISTS);
        }

        AppAiVideoReproduceTaskDetailRespVO respVO = BeanUtil.toBean(taskDTO, AppAiVideoReproduceTaskDetailRespVO.class);
        respVO.setProductImages(taskDTO.getProductImages());

        // 为了兼容老的任务（数据库中字段为 null，但 resultJson 中可能存在）
        if (cn.hutool.core.util.StrUtil.isNotEmpty(taskDTO.getResultJson())) {
            try {
                JsonNode root = objectMapper.readTree(taskDTO.getResultJson());
                if (respVO.getUnitsTitle() == null && root.has("units_title")) {
                    respVO.setUnitsTitle(root.path("units_title").asText());
                }
                if (respVO.getUnitsSpokenText() == null && root.has("units_spoken_text")) {
                    respVO.setUnitsSpokenText(root.path("units_spoken_text").asText());
                }
                if (respVO.getUnitsGlobalTags() == null && root.has("units_global_tags")) {
                    respVO.setUnitsGlobalTags(objectMapper.convertValue(root.path("units_global_tags"), new TypeReference<List<String>>() {}));
                }
            } catch (Exception e) {
                log.error("尝试从 resultJson 恢复老任务的 units 字段失败", e);
            }
        }

        java.util.List<BizVideoReproduceFrameDTO> frameDTOs = bizVideoReproduceApi.getVideoReproduceFrameListByTaskId(taskId);
        if (cn.hutool.core.collection.CollUtil.isNotEmpty(frameDTOs)) {
            java.util.List<AppAiVideoReproduceTaskDetailRespVO.AppAiVideoReproduceFrameRespVO> frames = new java.util.ArrayList<>();
            for (BizVideoReproduceFrameDTO frameDTO : frameDTOs) {
                AppAiVideoReproduceTaskDetailRespVO.AppAiVideoReproduceFrameRespVO frameResp = BeanUtil.toBean(frameDTO, AppAiVideoReproduceTaskDetailRespVO.AppAiVideoReproduceFrameRespVO.class);
                
                frameResp.setStepStatus(frameDTO.getStatus());
                frameResp.setOriginalPrompt(frameDTO.getI2vPromptZh());
                frameResp.setRewrittenPrompt(frameDTO.getI2vPromptEn());
                frameResp.setGridImagePromptEn(frameDTO.getGridImagePromptEn());
                frameResp.setGridImagePromptZh(frameDTO.getGridImagePromptZh());
                frameResp.setGridSourceImages(frameDTO.getGridSourceImages());
                // 显式填充生视频提示词字段（供前端步骤二展示）
                frameResp.setI2vPromptEn(frameDTO.getI2vPromptEn());
                frameResp.setI2vPromptZh(frameDTO.getI2vPromptZh());
                
                // 显式填充新增的第7第8步独立字段
                frameResp.setSingleImageSourceIndices(frameDTO.getSingleImageSourceIndices());
                frameResp.setSingleSourceImages(frameDTO.getSingleSourceImages());
                frameResp.setSingleI2vPromptEn(frameDTO.getSingleI2vPromptEn());
                frameResp.setSingleI2vPromptZh(frameDTO.getSingleI2vPromptZh());
                
                frameResp.setPeopleSingleImageSourceIndices(frameDTO.getPeopleSingleImageSourceIndices());
                frameResp.setPeopleSingleSourceImages(frameDTO.getPeopleSingleSourceImages());
                frameResp.setPeopleSingleImagePromptEn(frameDTO.getPeopleSingleImagePromptEn());
                frameResp.setPeopleSingleImagePromptZh(frameDTO.getPeopleSingleImagePromptZh());
                frameResp.setPeopleSingleI2vPromptEn(frameDTO.getPeopleSingleI2vPromptEn());
                frameResp.setPeopleSingleI2vPromptZh(frameDTO.getPeopleSingleI2vPromptZh());

                // 显式填充分镜标识和洗图结果（BeanUtil 可能因字段名差异而丢失）
                frameResp.setGuId(frameDTO.getGuId());
                frameResp.setPolishedImageUrl(frameDTO.getPolishedImageUrl());

                if (cn.hutool.core.util.StrUtil.isNotEmpty(frameDTO.getAiImageId())) {
                    cn.iocoder.yudao.module.ai.dal.dataobject.image.AiImageDO imageDO = aiImageService.getImage(Long.valueOf(frameDTO.getAiImageId()));
                    if (imageDO != null) {
                        if (cn.hutool.core.util.StrUtil.isNotEmpty(imageDO.getPicUrl())) {
                            frameResp.setOutputUrl(imageDO.getPicUrl());
                        }
                        if (imageDO.getStatus() != null) {
                            if (imageDO.getStatus() == 20) {
                                frameResp.setStepStatus("2"); // 成功
                            } else if (imageDO.getStatus() == 30) {
                                frameResp.setStepStatus("3"); // 失败
                            } else {
                                frameResp.setStepStatus("1"); // 处理中
                            }
                        }
                    }
                }
                
                if (cn.hutool.core.util.StrUtil.isNotEmpty(frameDTO.getAiVideoId())) {
                    cn.iocoder.yudao.module.ai.dal.dataobject.video.BizAiVideoDO videoDO = bizAiVideoMapper.selectById(Long.valueOf(frameDTO.getAiVideoId()));
                    if (videoDO != null) {
                        if (cn.hutool.core.util.StrUtil.isNotEmpty(videoDO.getVideoUrl())) {
                            frameResp.setOutputUrl(videoDO.getVideoUrl());
                        }
                        if (videoDO.getStatus() != null) {
                            if (videoDO.getStatus() == 20) {
                                frameResp.setStepStatus("2"); // 成功
                            } else if (videoDO.getStatus() == 30) {
                                frameResp.setStepStatus("3"); // 失败
                            } else {
                                frameResp.setStepStatus("1"); // 处理中
                            }
                        }
                    }
                } else if (cn.hutool.core.util.StrUtil.isNotEmpty(frameDTO.getGeneratedVideoUrl())) {
                    // 本地模式时，不会产生 aiVideoId，而是直接在 frame 表里记录 generatedVideoUrl
                    frameResp.setOutputUrl(frameDTO.getGeneratedVideoUrl());
                    frameResp.setStepStatus("2"); // 成功
                } else if ("3".equals(frameDTO.getStatus()) && !cn.hutool.core.util.StrUtil.isNotEmpty(frameDTO.getGeneratedVideoUrl())) {
                    // 如果状态是3，但没有生成视频URL，说明是本地正在处理中或者入队了
                    frameResp.setStepStatus("1");
                }
                frames.add(frameResp);
            }
            respVO.setFrames(frames);
        }
        return respVO;
    }

    @Override
    public cn.iocoder.yudao.framework.common.pojo.PageResult<cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO> getTaskPage(Long userId, Integer pageNo, Integer pageSize) {
        cn.iocoder.yudao.framework.common.pojo.PageResult<BizVideoReproduceTaskDTO> pageResult = bizVideoReproduceApi.getVideoReproduceTaskPage(userId, pageNo, pageSize);
        return cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean(pageResult, cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO.class);
    }
}
