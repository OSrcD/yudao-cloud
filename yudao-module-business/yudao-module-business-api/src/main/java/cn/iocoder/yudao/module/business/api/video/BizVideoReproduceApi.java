package cn.iocoder.yudao.module.business.api.video;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.api.video.dto.BizVideoReproduceFrameDTO;
import cn.iocoder.yudao.module.business.api.video.dto.BizVideoReproduceTaskDTO;

import java.util.List;

/**
 * 视频复刻 API 接口
 */
public interface BizVideoReproduceApi {

    PageResult<BizVideoReproduceTaskDTO> getVideoReproduceTaskPage(Long userId, Integer pageNo, Integer pageSize);

    // === 任务相关 ===

    Long createVideoReproduceTask(BizVideoReproduceTaskDTO createDTO);

    void updateVideoReproduceTask(BizVideoReproduceTaskDTO updateDTO);

    BizVideoReproduceTaskDTO getVideoReproduceTask(Long id);

    // === 帧相关 ===

    Long createVideoReproduceFrame(BizVideoReproduceFrameDTO createDTO);

    void updateVideoReproduceFrame(BizVideoReproduceFrameDTO updateDTO);

    BizVideoReproduceFrameDTO getVideoReproduceFrame(Long id);

    List<BizVideoReproduceFrameDTO> getVideoReproduceFrameListByTaskId(Long taskId);

    /**
     * 启动本地分析工作流
     *
     * @param taskId 任务ID
     */
    void startLocalAnalyzeWorkflow(Long taskId);

}
