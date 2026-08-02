package cn.iocoder.yudao.module.business.job;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorNotifyConfigVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCommentMapper;
import cn.iocoder.yudao.module.business.service.XhsCommentMonitorNotifyService;
import cn.iocoder.yudao.module.business.service.XhsCommentMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小红书评论监控定时任务
 * 每 60 秒扫描一次已监控笔记下的新评论，命中关键词则触发推送（去重保证只推一次）
 */
@Slf4j
@Component
public class XhsCommentMonitorJob {

    @Resource
    private XhsCommentMonitorService commentMonitorService;

    @Resource
    private XhsCommentMonitorNotifyService notifyService;

    @Resource
    private XhsNoteCommentMapper noteCommentMapper;

    /**
     * 上次执行时间，用于时间窗口扫描。
     * 使用当前时间 - 2分钟作为兜底，避免首次启动漏推。
     */
    private volatile LocalDateTime lastExecuteTime = LocalDateTime.now().minusMinutes(2);

    @Scheduled(fixedDelay = 60000)
    public void execute() {
        try {
            doExecute();
        } catch (Exception e) {
            log.error("[评论监控定时任务] 执行异常", e);
        }
    }

    private void doExecute() {
        // 1. 获取推送配置，开关关闭则跳过
        XhsCommentMonitorNotifyConfigVO config = notifyService.getNotifyConfig();
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            return;
        }

        // 2. 获取监控关键词，无关键词则跳过
        List<String> keywords = commentMonitorService.getKeywordStrings();
        if (CollUtil.isEmpty(keywords)) {
            log.debug("[评论监控定时任务] 未配置关键词，跳过本次扫描");
            return;
        }

        // 3. 获取已监控笔记的 noteId 列表
        List<String> monitoredNoteIds = commentMonitorService.getMonitoredNoteIds();
        if (CollUtil.isEmpty(monitoredNoteIds)) {
            log.debug("[评论监控定时任务] 暂无监控中的笔记，跳过本次扫描");
            return;
        }

        // 4. 查询时间窗口内的新评论（since 上次执行时间，与当前执行时间之间留 2 分钟重叠兜底）
        LocalDateTime since = lastExecuteTime.minusMinutes(2);
        LocalDateTime currentTime = LocalDateTime.now();

        List<XhsNoteCommentDO> recentComments = noteCommentMapper.selectRecentByNoteIds(monitoredNoteIds, since);
        lastExecuteTime = currentTime;

        if (CollUtil.isEmpty(recentComments)) {
            log.debug("[评论监控定时任务] 时间窗口[{} ~ {}]内无新评论", since, currentTime);
            return;
        }

        log.info("[评论监控定时任务] 时间窗口内发现 {} 条新评论，开始关键词匹配与推送", recentComments.size());

        // 5. 检查并推送（内部已做去重）
        notifyService.checkAndNotify(recentComments, keywords, config);
    }

}
