package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorNotifyConfigVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;

import java.util.List;

/**
 * 小红书评论监控推送服务
 */
public interface XhsCommentMonitorNotifyService {

    /**
     * 批量检查评论是否命中关键词并推送，每条评论每个关键词只推送一次
     *
     * @param comments 待检查的评论列表
     * @param keywords 监控关键词列表
     * @param config   推送配置
     */
    void checkAndNotify(List<XhsNoteCommentDO> comments, List<String> keywords, XhsCommentMonitorNotifyConfigVO config);

    /**
     * 发送测试推送（供前端验证配置）
     *
     * @param config 推送配置
     * @return 是否发送成功
     */
    boolean sendTest(XhsCommentMonitorNotifyConfigVO config);

    /**
     * 获取推送配置
     */
    XhsCommentMonitorNotifyConfigVO getNotifyConfig();

    /**
     * 保存推送配置
     */
    void saveNotifyConfig(XhsCommentMonitorNotifyConfigVO config);

}
