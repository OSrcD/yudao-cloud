package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskCreateReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceFrameDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 视频复刻任务 Service 接口
 */
public interface BizVideoReproduceService {

    /**
     * 创建并开始任务
     */
    BizVideoReproduceTaskDO createAndStartTask(BizVideoReproduceTaskCreateReqVO createReqVO);

    /**
     * 获得任务分页
     */
    PageResult<BizVideoReproduceTaskDO> getTaskPage(BizVideoReproduceTaskPageReqVO pageReqVO);

    /**
     * 获得任务详情
     */
    BizVideoReproduceTaskDO getTask(Long id);

    /**
     * 获取截帧列表
     */
    List<BizVideoReproduceFrameDO> getFrames(Long taskId);

    /**
     * 洗图
     */
    void washImage(Long frameId, String washMode, String customPrompt, List<String> refImages, String execMode);

    /**
     * 绑定音频
     */
    void bindAudio(Long frameId, MultipartFile audioFile);

    /**
     * 自动裁剪音频
     */
    void autoTrimAudio(Long frameId);

    /**
     * 生成视频
     */
    void generateVideo(Long frameId, String execMode);

    /**
     * 音画同步
     */
    void syncAudioToVideo(Long frameId);

    /**
     * 下载音频
     */
    void downloadAudio(Long frameId, jakarta.servlet.http.HttpServletResponse response);

    /**
     * 重试任务
     */
    void retryTask(Long taskId);

    /**
     * 一键全部生成视频
     */
    void generateAllVideos(Long taskId, String execMode);

    /**
     * 一键全部洗图
     */
    void washAllImages(Long taskId, String washMode, String customPrompt, List<String> refImages, String execMode);

    /**
     * 撤回洗图
     */
    void undoWash(Long frameId);

    /**
     * 撤回视频
     */
    void undoVideo(Long frameId);

    /**
     * 剪辑视频
     */
    void clipVideo(Long frameId, List<Map<String, Double>> removeRanges);

    /**
     * 合成全片视频
     */
    void mergeVideos(Long taskId, List<Long> frameIds);

    /**
     * 删除截帧记录
     */
    void deleteFrame(Long frameId);

    /**
     * 删除生成的视频
     */
    void deleteGeneratedVideo(Long frameId);

    /**
     * 删除洗图图片
     */
    void deletePolishedImage(Long frameId);

    /**
     * 删除原始截帧图片
     */
    void deleteOriginalImage(Long frameId);

    /**
     * 手动裁剪音频
     */
    void manualTrimAudio(Long frameId, Double start, Double end);

    /**
     * 更新提示词
     */
    void updatePrompts(Long frameId, String promptEn, String promptZh);

    /**
     * 手动重新截帧
     */
    void recaptureFrame(Long frameId, Double timestamp);

    /**
     * 手动上传生成的视频
     */
    void uploadGeneratedVideo(Long frameId, MultipartFile videoFile);

    /**
     * 手动上传原始图片
     */
    void uploadOriginalImage(Long frameId, MultipartFile imageFile);

    /**
     * 删除任务
     */
    void deleteTask(Long id);

    /**
     * 启动本地分析工作流
     */
    void startLocalAnalyzeWorkflow(Long taskId);

    /**
     * 启动完整分析工作流 (Gemini)
     */
    void startFullWorkflow(Long taskId);

    /**
     * 分析完成后继续后续工作流
     */
    void continueFullWorkflowAfterAnalysis(Long taskId, String resultJson);
}
