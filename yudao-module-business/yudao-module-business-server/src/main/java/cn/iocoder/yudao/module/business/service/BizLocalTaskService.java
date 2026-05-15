package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizLocalTaskSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizLocalTaskDO;

import jakarta.validation.Valid;

/**
 * 本地任务 Service 接口
 */
public interface BizLocalTaskService {

    /**
     * 创建本地任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLocalTask(@Valid BizLocalTaskSaveReqVO createReqVO);

    /**
     * 更新本地任务
     *
     * @param updateReqVO 更新信息
     */
    void updateLocalTask(@Valid BizLocalTaskSaveReqVO updateReqVO);

    /**
     * 删除本地任务
     *
     * @param id 编号
     */
    void deleteLocalTask(Long id);

    /**
     * 获得本地任务
     *
     * @param id 编号
     * @return 本地任务
     */
    BizLocalTaskDO getLocalTask(Long id);

    /**
     * 获得本地任务分页
     *
     * @param pageReqVO 分页查询
     * @return 本地任务分页
     */
    PageResult<BizLocalTaskDO> getLocalTaskPage(BizLocalTaskPageReqVO pageReqVO);

    /**
     * 入队任务
     */
    void enqueueTask(String taskType, Long refTaskId, Long refFrameId, String execParams);

    /**
     * 拉取一个任务执行
     */
    BizLocalTaskDO pollTask();

    /**
     * 获得所有任务
     */
    java.util.List<BizLocalTaskDO> list();

    /**
     * 获得待分配任务数量
     */
    long getCount();

    /**
     * 完成任务回调
     */
    void completeTask(Long taskId, boolean success, String resultData, String errorMsg);

}

