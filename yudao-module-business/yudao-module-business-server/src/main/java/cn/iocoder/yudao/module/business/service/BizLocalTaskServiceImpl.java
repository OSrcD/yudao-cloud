package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizLocalTaskConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizLocalTaskDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceFrameDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizLocalTaskMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizVideoReproduceFrameMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizVideoReproduceTaskMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

/**
 * 本地任务 Service 实现类
 */
@Slf4j
@Service
@Validated
public class BizLocalTaskServiceImpl implements BizLocalTaskService {

    @Resource
    private BizLocalTaskMapper localTaskMapper;

    @Resource
    private BizVideoReproduceFrameMapper frameMapper;

    @Resource
    private BizVideoReproduceTaskMapper taskMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Long createLocalTask(BizLocalTaskSaveReqVO createReqVO) {
        BizLocalTaskDO localTask = BizLocalTaskConvert.INSTANCE.convert(createReqVO);
        if (createReqVO.getExecParams() != null) {
            try {
                localTask.setExecParams(objectMapper.readValue(createReqVO.getExecParams(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {}));
            } catch (Exception e) {
                log.error("解析 execParams 异常", e);
            }
        }
        localTaskMapper.insert(localTask);
        return localTask.getId();
    }

    @Override
    public void updateLocalTask(BizLocalTaskSaveReqVO updateReqVO) {
        BizLocalTaskDO updateObj = BizLocalTaskConvert.INSTANCE.convert(updateReqVO);
        if (updateReqVO.getExecParams() != null) {
            try {
                updateObj.setExecParams(objectMapper.readValue(updateReqVO.getExecParams(), new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {}));
            } catch (Exception e) {
                log.error("解析 execParams 异常", e);
            }
        }
        localTaskMapper.updateById(updateObj);
    }

    @Override
    public void deleteLocalTask(Long id) {
        localTaskMapper.deleteById(id);
    }

    @Override
    public BizLocalTaskDO getLocalTask(Long id) {
        return localTaskMapper.selectById(id);
    }

    @Override
    public PageResult<BizLocalTaskDO> getLocalTaskPage(BizLocalTaskPageReqVO pageReqVO) {
        return localTaskMapper.selectPage(pageReqVO);
    }

    @Override
    public void enqueueTask(String taskType, Long refTaskId, Long refFrameId, String execParams) {
        BizLocalTaskDO localTask = new BizLocalTaskDO();
        localTask.setTaskType(taskType);
        localTask.setRefTaskId(refTaskId);
        localTask.setRefFrameId(refFrameId);
        localTask.setExecParams(JsonUtils.parseObject(execParams, Map.class));
        localTask.setStatus(0); // 待开始
        localTaskMapper.insert(localTask);
    }

    @Override
    public BizLocalTaskDO pollTask() {
        LambdaQueryWrapper<BizLocalTaskDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizLocalTaskDO::getStatus, 0)
                .orderByAsc(BizLocalTaskDO::getCreateTime)
                .last("LIMIT 1");

        BizLocalTaskDO task = localTaskMapper.selectOne(wrapper);
        if (task != null) {
            task.setStatus(1); // 运行中
            localTaskMapper.updateById(task);
            return task;
        }
        return null;
    }

    @Override
    public List<BizLocalTaskDO> list() {
        return localTaskMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public long getCount() {
        return localTaskMapper.selectCount(new LambdaQueryWrapper<BizLocalTaskDO>().eq(BizLocalTaskDO::getStatus, 0));
    }

    @Override
    public void completeTask(Long taskId, boolean success, String resultData, String errorMsg) {
        BizLocalTaskDO localTask = localTaskMapper.selectById(taskId);
        if (localTask == null) {
            return;
        }

        localTask.setStatus(success ? 2 : 3);
        localTask.setResultData(JsonUtils.parseObject(resultData, Map.class));
        localTask.setErrorMsg(errorMsg);
        localTaskMapper.updateById(localTask);

        if (!success) {
            return;
        }

        TenantUtils.execute(localTask.getTenantId(), () -> {
            if ("ANALYZE_VIDEO".equals(localTask.getTaskType())) {
                try {
                    BizVideoReproduceService videoReproduceService = applicationContext.getBean(BizVideoReproduceService.class);
                    videoReproduceService.continueFullWorkflowAfterAnalysis(localTask.getRefTaskId(), resultData);
                } catch (Exception e) {
                    log.error("处理 ANALYZE_VIDEO 回调异常", e);
                    BizVideoReproduceTaskDO taskUpdate = new BizVideoReproduceTaskDO();
                    taskUpdate.setId(localTask.getRefTaskId());
                    taskUpdate.setStatus("9");
                    taskUpdate.setErrorMsg("处理本地回传的分析结果失败: " + e.getMessage());
                    taskMapper.updateById(taskUpdate);
                }
            } else {
                Long frameId = localTask.getRefFrameId();
                if (frameId != null) {
                    BizVideoReproduceFrameDO frame = frameMapper.selectById(frameId);
                    if (frame != null) {
                        try {
                            JsonNode resultJson = objectMapper.readTree(resultData);
                            if ("WASH_IMAGE".equals(localTask.getTaskType())) {
                                String polishedUrl = resultJson.path("url").asText(null);
                                if (polishedUrl != null) {
                                    if (frame.getPolishedImageUrl() != null && !frame.getPolishedImageUrl().equals(polishedUrl)) {
                                        frame.setPrevPolishedUrl(frame.getPolishedImageUrl());
                                    }
                                    frame.setPolishedImageUrl(polishedUrl);
                                    frame.setStatus("2");
                                    frameMapper.updateById(frame);
                                }
                            } else if ("GEN_VIDEO".equals(localTask.getTaskType())) {
                                String videoUrl = resultJson.path("url").asText(null);
                                if (videoUrl != null) {
                                    if (frame.getGeneratedVideoUrl() != null && !frame.getGeneratedVideoUrl().equals(videoUrl)) {
                                        frame.setPrevVideoUrl(frame.getGeneratedVideoUrl());
                                    }
                                    frame.setGeneratedVideoUrl(videoUrl);
                                    frame.setStatus("3");
                                    frameMapper.updateById(frame);
                                }
                            }
                        } catch (Exception e) {
                            log.error("解析本地任务结果异常", e);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void partialCompleteTask(Long taskId, String resultData) {
        BizLocalTaskDO localTask = localTaskMapper.selectById(taskId);
        if (localTask == null) {
            return;
        }

        // 局部上报，更新中间结果数据
        localTask.setResultData(JsonUtils.parseObject(resultData, Map.class));
        localTaskMapper.updateById(localTask);

        TenantUtils.execute(localTask.getTenantId(), () -> {
            if ("ANALYZE_VIDEO".equals(localTask.getTaskType())) {
                try {
                    BizVideoReproduceService videoReproduceService = applicationContext.getBean(BizVideoReproduceService.class);
                    // 根据 JSON 内容路由：第三套含 grid_suggestions，第四套含 units
                    JsonNode root = objectMapper.readTree(resultData);
                    if (!root.path("units").isMissingNode()) {
                        // 第四套回调：生视频提示词
                        videoReproduceService.continueWorkflowAfterI2vPromptAnalysis(localTask.getRefTaskId(), resultData);
                    } else {
                        // 第三套回调：宫格图提示词 + 建帧
                        videoReproduceService.continueWorkflowAfterPartialAnalysis(localTask.getRefTaskId(), resultData);
                    }
                } catch (Exception e) {
                    log.error("处理 ANALYZE_VIDEO 局部回调异常", e);
                }
            }
        });
    }

}

