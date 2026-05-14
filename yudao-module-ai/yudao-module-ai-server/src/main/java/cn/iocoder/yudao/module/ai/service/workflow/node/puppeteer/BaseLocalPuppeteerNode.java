package cn.iocoder.yudao.module.ai.service.workflow.node.puppeteer;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.ai.dal.dataobject.puppeteer.AiLocalPuppeteerTaskDO;
import cn.iocoder.yudao.module.ai.dal.mysql.puppeteer.AiLocalPuppeteerTaskMapper;
import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.node.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


/**
 * 本地 Puppeteer 执行节点的基类
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public abstract class BaseLocalPuppeteerNode extends BaseNode {

    protected String modelType;
    protected String actionType;
    protected String executionMode;
    protected String prompt;
    protected String imageUrls;
    protected String videoUrls;
    protected Boolean newChat; // 是否开启新对话
    protected Long workflowId;

    /**
     * 持久化到数据库，由子类或解析器注入 Mapper
     */
    protected AiLocalPuppeteerTaskMapper taskMapper;

    @Override
    public Map<String, Object> execute(Chain chain) {
        // 由于前端移除了参数连线配置，这里直接手动解析表单填写的 {{变量}}
        String finalActionType = resolveVariables(this.actionType, chain);
        String finalExecutionMode = resolveVariables(this.executionMode, chain);
        String finalPrompt = resolveVariables(this.prompt, chain);
        String finalImageUrls = resolveVariables(this.imageUrls, chain);
        String finalVideoUrls = resolveVariables(this.videoUrls, chain);
        Boolean finalNewChat = this.newChat != null ? this.newChat : false;

        // 构造传给本地执行客户端的完整参数
        Map<String, Object> params = new HashMap<>();
        params.put("actionType", finalActionType);
        params.put("executionMode", finalExecutionMode);
        params.put("prompt", finalPrompt);
        params.put("imageUrls", finalImageUrls);
        params.put("videoUrls", finalVideoUrls);
        params.put("newChat", finalNewChat);

        log.info("[execute][工作流执行到本地 Puppeteer 节点, modelType: {}, actionType: {}, newChat: {}]", modelType, finalActionType, finalNewChat);

        // 3. 构造任务并入库

        AiLocalPuppeteerTaskDO taskDO = AiLocalPuppeteerTaskDO.builder()
                .workflowId(workflowId)
                .executionId(chain.getId())
                .nodeId(getId())
                .modelType(modelType)
                .actionType(finalActionType)
                .parameters(JsonUtils.toJsonString(params)) // 原始参数快照
                .status(0) // 0: 待处理
                .build();
        taskMapper.insert(taskDO);

        log.info("[execute][任务已入库, ID: {}, 提示词长度: {}, 等待本地客户端执行...]", taskDO.getId(), finalPrompt.length());

        // 4. 同步阻塞轮询 (最大等待 10 分钟, 300 * 2秒)
        int maxRetry = 300;
        while (maxRetry-- > 0) {
            try {
                Thread.sleep(2000); // 睡 2 秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("工作流节点执行被中断", e);
            }

            AiLocalPuppeteerTaskDO latestTask = taskMapper.selectById(taskDO.getId());
            if (latestTask == null) {
                throw new RuntimeException("任务记录丢失: " + taskDO.getId());
            }

            if (latestTask.getStatus() == 2) { // 2: 已完成
                log.info("[execute][任务执行成功, ID: {}]", taskDO.getId());
                Map<String, Object> result = new HashMap<>();
                if (latestTask.getResultData() != null && !latestTask.getResultData().isEmpty()) {
                    try {
                        result = JsonUtils.parseObject(latestTask.getResultData(), Map.class);
                    } catch (Exception e) {
                        result.put("rawResult", latestTask.getResultData());
                    }
                }
                result.put("taskId", taskDO.getId());
                return result; // 将结果返回给链，由链传给下一个节点
            }

            if (latestTask.getStatus() == 3) { // 3: 已失败
                log.error("[execute][任务执行失败, ID: {}, 原因: {}]", taskDO.getId(), latestTask.getErrorMsg());
                throw new RuntimeException("本地 Puppeteer 执行失败: " + latestTask.getErrorMsg());
            }
        }

        throw new RuntimeException("本地 Puppeteer 执行超时");
    }

    /**
     * 手动解析字符串中的 {{变量}}，从 Chain 环境变量中提取真实值
     */
    private String resolveVariables(String text, Chain chain) {
        if (text == null || text.isEmpty()) {
            return text != null ? text : "";
        }
        String result = text;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{\\{([^}]+)\\}\\}");
        java.util.regex.Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            String varName = matcher.group(1).trim();
            Object val = chain.get(varName);
            if (val != null) {
                result = result.replace(matcher.group(0), val.toString());
            }
        }
        return result;
    }
}

