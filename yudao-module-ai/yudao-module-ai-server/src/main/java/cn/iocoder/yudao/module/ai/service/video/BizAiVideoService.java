package cn.iocoder.yudao.module.ai.service.video;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoGeekAiVeoSubmitReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.video.vo.BizAiVideoAihubmixSubmitReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.video.BizAiVideoDO;

/**
 * AI 视频 Service 接口
 *
 * @author CuiMa
 */
public interface BizAiVideoService {

    /**
     * 提交视频生成任务
     *
     * @param userId 用户编号
     * @param submitReqVO 提交请求
     * @return 视频编号
     */
    Long submitGeekAiVeoVideo(Long userId, BizAiVideoGeekAiVeoSubmitReqVO submitReqVO);

    /**
     * 提交视频生成任务 (Aihubmix)
     *
     * @param userId 用户编号
     * @param submitReqVO 提交请求
     * @return 视频编号
     */
    Long submitAihubmixVideo(Long userId, BizAiVideoAihubmixSubmitReqVO submitReqVO);

    /**
     * 同步视频生成进展 (Aihubmix)
     *
     * @param id 视频编号
     */
    void syncAihubmixVideo(Long id);

    /**
     * 同步【所有】进行中的视频生成进展 (Aihubmix)
     *
     * @return 同步数量
     */
    Integer syncAihubmixVideo();

    /**
     * 同步视频生成进展
     *
     * @param id 视频编号
     */
    void syncGeekAiVeoVideo(Long id);

    /**
     * 同步【所有】进行中的视频生成进展
     *
     * @return 同步数量
     */
    Integer syncGeekAiVeoVideo();

    /**
     * 回调视频生成进展
     *
     * @param notifyData 回调数据
     */
    void notifyVideo(String notifyData);

    /**
     * 获得视频分页
     *
     * @param pageReqVO 分页查询
     * @return 视频分页
     */
    PageResult<BizAiVideoDO> getVideoPage(BizAiVideoPageReqVO pageReqVO);

    /**
     * 删除视频
     *
     * @param id 编号
     */
    void deleteVideo(Long id);

}
