package cn.iocoder.yudao.module.ai.service.workflow;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.workflow.vo.AiWorkflowPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.workflow.vo.AiWorkflowSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.workflow.vo.AiWorkflowTestReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.workflow.AiWorkflowDO;
import cn.iocoder.yudao.module.ai.dal.mysql.puppeteer.AiLocalPuppeteerTaskMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.workflow.AiWorkflowMapper;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.service.workflow.node.puppeteer.LocalPuppeteerNodeParser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dev.tinyflow.core.Tinyflow;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.WORKFLOW_CODE_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.WORKFLOW_NOT_EXISTS;

/**
 * AI 工作流 Service 实现类
 *
 * @author lesan
 */
@Service
@Slf4j
public class AiWorkflowServiceImpl implements AiWorkflowService {

    @Resource
    private AiWorkflowMapper workflowMapper;

    @Resource
    private AiModelService apiModelService;

    @Override
    public Long createWorkflow(AiWorkflowSaveReqVO createReqVO) {
        // 1. 参数校验
        validateCodeUnique(null, createReqVO.getCode());

        // 2. 插入工作流配置
        AiWorkflowDO workflow = BeanUtils.toBean(createReqVO, AiWorkflowDO.class);
        workflowMapper.insert(workflow);
        return workflow.getId();
    }

    @Override
    public void updateWorkflow(AiWorkflowSaveReqVO updateReqVO) {
        // 1. 参数校验
        validateWorkflowExists(updateReqVO.getId());
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新工作流配置
        AiWorkflowDO workflow = BeanUtils.toBean(updateReqVO, AiWorkflowDO.class);
        workflowMapper.updateById(workflow);
    }

    @Override
    public void deleteWorkflow(Long id) {
        // 1. 校验存在
        validateWorkflowExists(id);

        // 2. 删除工作流配置
        workflowMapper.deleteById(id);
    }

    private AiWorkflowDO validateWorkflowExists(Long id) {
        if (ObjUtil.isNull(id)) {
            throw exception(WORKFLOW_NOT_EXISTS);
        }
        AiWorkflowDO workflow = workflowMapper.selectById(id);
        if (ObjUtil.isNull(workflow)) {
            throw exception(WORKFLOW_NOT_EXISTS);
        }
        return workflow;
    }

    private void validateCodeUnique(Long id, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        AiWorkflowDO workflow = workflowMapper.selectByCode(code);
        if (ObjUtil.isNull(workflow)) {
            return;
        }
        if (ObjUtil.isNull(id)) {
            throw exception(WORKFLOW_CODE_EXISTS);
        }
        if (ObjUtil.notEqual(workflow.getId(), id)) {
            throw exception(WORKFLOW_CODE_EXISTS);
        }
    }

    @Resource
    private AiLocalPuppeteerTaskMapper localPuppeteerTaskMapper;

    @Override
    public AiWorkflowDO getWorkflow(Long id) {
        return workflowMapper.selectById(id);
    }

    @Override
    public PageResult<AiWorkflowDO> getWorkflowPage(AiWorkflowPageReqVO pageReqVO) {
        return workflowMapper.selectPage(pageReqVO);
    }

    @Override
    public Object testWorkflow(AiWorkflowTestReqVO testReqVO) {
        // 加载 graph
        String graph = testReqVO.getGraph() != null ? testReqVO.getGraph()
                : validateWorkflowExists(testReqVO.getId()).getGraph();

        // 构建 TinyFlow 执行链
        Tinyflow tinyflow = parseFlowParam(graph, testReqVO.getId());

        // 执行
        Map<String, Object> variables = testReqVO.getParams();
        return tinyflow.toChain().executeForResult(variables);
    }

    private Tinyflow parseFlowParam(String graph, Long workflowId) {
        // TODO @lesan：可以使用 jackson 哇？
        JSONObject json = JSONObject.parseObject(graph);
        JSONArray nodeArr = json.getJSONArray("nodes");
        Tinyflow tinyflow = new Tinyflow(json.toJSONString());
        
        // 注册自定义节点解析器
        tinyflow.getChainParser().addNodeParser("puppeteerNode", new LocalPuppeteerNodeParser(localPuppeteerTaskMapper, workflowId));

        for (int i = 0; i < nodeArr.size(); i++) {
            JSONObject node = nodeArr.getJSONObject(i);
            switch (node.getString("type")) {
                case "llmNode":
                    JSONObject data = node.getJSONObject("data");
                    apiModelService.getLLmProvider4Tinyflow(tinyflow, data.getLong("llmId"));
                    break;
            }
        }
        return tinyflow;
    }

    @Override
    public String generateWorkflowHtml(Long id) {
        AiWorkflowDO workflow = validateWorkflowExists(id);
        JSONObject json = JSONObject.parseObject(workflow.getGraph());
        JSONArray nodes = json.getJSONArray("nodes");

        // 查找开始节点，解析其定义的参数
        JSONObject startNode = null;
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            if ("startNode".equals(node.getString("type"))) {
                startNode = node;
                break;
            }
        }

        if (startNode == null) {
            return "<html><body><h3>未找到开始节点</h3></body></html>";
        }

        JSONArray parameters = startNode.getJSONObject("data").getJSONArray("parameters");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body { font-family: -apple-system, system-ui, BlinkMacSystemFont, 'Segoe UI', Roboto; padding: 20px; background: #f5f7fa; }");
        html.append(".form-item { margin-bottom: 20px; background: #fff; padding: 15px; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05); }");
        html.append(".label { font-weight: bold; margin-bottom: 10px; display: block; color: #333; }");
        html.append("input, textarea, select { width: 100%; border: 1px solid #dcdfe6; border-radius: 4px; padding: 10px; box-sizing: border-box; font-size: 14px; }");
        html.append(".btn { background: #3473ff; color: white; padding: 12px; border-radius: 8px; text-align: center; margin-top: 20px; cursor: pointer; font-weight: bold; }");
        html.append("</style></head><body>");
        html.append("<h2>").append(workflow.getName()).append("</h2>");
        html.append("<form id='workflowForm'>");

        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                JSONObject param = parameters.getJSONObject(i);
                String name = param.getString("name");
                String label = param.getString("description"); // 这里通常存的是 Label
                if (label == null || label.isEmpty()) label = name;
                String type = param.getString("type");

                html.append("<div class='form-item'>");
                html.append("<span class='label'>").append(label).append("</span>");

                if ("textarea".equals(type)) {
                    html.append("<textarea name='").append(name).append("' rows='4' placeholder='请输入").append(label).append("'></textarea>");
                } else if ("select".equals(type)) {
                    html.append("<select name='").append(name).append("'>");
                    JSONArray options = param.getJSONArray("options");
                    if (options != null) {
                        for (int j = 0; j < options.size(); j++) {
                            JSONObject opt = options.getJSONObject(j);
                            html.append("<option value='").append(opt.getString("value")).append("'>").append(opt.getString("label")).append("</option>");
                        }
                    }
                    html.append("</select>");
                } else {
                    html.append("<input type='text' name='").append(name).append("' placeholder='请输入").append(label).append("' />");
                }
                html.append("</div>");
            }
        }

        html.append("<div class='btn' onclick='submitForm()'>提交任务</div>");
        html.append("</form>");
        html.append("<script>");
        html.append("function submitForm() {");
        html.append("  const formData = new FormData(document.getElementById('workflowForm'));");
        html.append("  const data = {};");
        html.append("  formData.forEach((value, key) => data[key] = value);");
        html.append("  console.log('提交数据:', data);");
        html.append("  // 这里可以调用小程序的 JSBridge 或者发送请求");
        html.append("  alert('任务已提交！参数：' + JSON.stringify(data));");
        html.append("}");
        html.append("</script>");
        html.append("</body></html>");

        return html.toString();
    }

}
