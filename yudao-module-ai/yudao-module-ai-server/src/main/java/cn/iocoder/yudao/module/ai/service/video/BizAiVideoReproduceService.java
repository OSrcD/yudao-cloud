package cn.iocoder.yudao.module.ai.service.video;

import cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceReqVO;

public interface BizAiVideoReproduceService {

    /**
     * 创建视频复刻任务并触发视频分析
     *
     * @param userId 用户编号
     * @param reqVO 请求
     * @return 任务及支付单号
     */
    cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceCreateRespVO createTask(Long userId, AppAiVideoReproduceReqVO reqVO);

    /**
     * 触发单帧洗图
     * @param userId 用户编号
     * @param frameId 帧编号
     * @param modelId 模型编号
     * @param width 宽度
     * @param height 高度
     */
    void washFrame(Long userId, Long frameId, Long modelId, Integer width, Integer height);

    /**
     * 触发生成视频
     * @param userId 用户编号
     * @param frameId 帧编号
     * @param modelId 模型编号
     * @param width 宽度
     * @param height 高度
     */
    void generateVideo(Long userId, Long frameId, Long modelId, Integer width, Integer height);

    /**
     * 触发生成视频
     * @param userId 用户编号
     * @param frameId 帧编号
     * @param modelId 模型编号
     * @param width 宽度
     * @param height 高度
     * @param inputReference 视频底图参考
     */
    void generateVideo(Long userId, Long frameId, Long modelId, Integer width, Integer height, String inputReference);

    /**
     * 获取视频复刻配置（如默认模型）
     * @return 配置信息
     */
    cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceConfigRespVO getReproduceConfig();

    /**
     * 获取视频复刻任务详情
     * @param userId 用户编号
     * @param taskId 任务编号
     * @return 任务详情
     */
    cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO getTaskDetail(Long userId, Long taskId);

    /**
     * 处理视频分析结果（供内部异步回调）
     * @param taskId 任务编号
     */
    void processVideoAnalysisResult(Long taskId);

    /**
     * 分页获取视频复刻任务列表
     * @param userId 用户编号
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 任务分页结果
     */
    cn.iocoder.yudao.framework.common.pojo.PageResult<cn.iocoder.yudao.module.ai.controller.app.video.vo.AppAiVideoReproduceTaskDetailRespVO> getTaskPage(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 更新视频复刻任务支付状态，并开始分析
     * @param id 任务ID
     * @param payOrderId 支付单号
     */
    void updateTaskPaid(Long id, Long payOrderId);
}
