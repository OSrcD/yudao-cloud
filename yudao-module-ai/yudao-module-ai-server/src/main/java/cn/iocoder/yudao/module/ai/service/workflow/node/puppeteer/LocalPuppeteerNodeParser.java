package cn.iocoder.yudao.module.ai.service.workflow.node.puppeteer;

import cn.iocoder.yudao.module.ai.dal.mysql.puppeteer.AiLocalPuppeteerTaskMapper;
import com.alibaba.fastjson.JSONObject;
import com.agentsflex.core.chain.node.BaseNode;
import dev.tinyflow.core.Tinyflow;
import dev.tinyflow.core.parser.BaseNodeParser;

/**
 * 本地 Puppeteer 节点解析器
 *
 * @author 芋道源码
 */
public class LocalPuppeteerNodeParser extends BaseNodeParser {

    private final AiLocalPuppeteerTaskMapper taskMapper;
    private final Long workflowId;

    public LocalPuppeteerNodeParser(AiLocalPuppeteerTaskMapper taskMapper, Long workflowId) {
        this.taskMapper = taskMapper;
        this.workflowId = workflowId;
    }

    @Override
    public BaseNode doParse(JSONObject root, JSONObject data, Tinyflow tinyflow) {
        LocalLlmPuppeteerNode node = new LocalLlmPuppeteerNode();
        node.setTaskMapper(taskMapper);
        node.setWorkflowId(workflowId);

        // 从 data 中解析表单静态字段
        node.setModelType(data.getString("modelType"));
        node.setActionType(data.getString("actionType"));
        node.setExecutionMode(data.getString("executionMode"));
        node.setPrompt(data.getString("prompt"));

        node.setImageUrls(data.getString("imageUrls"));
        node.setVideoUrls(data.getString("videoUrls"));
        if (data.containsKey("newChat")) {
            node.setNewChat(data.getBoolean("newChat"));
        }


        return node;
    }

}
