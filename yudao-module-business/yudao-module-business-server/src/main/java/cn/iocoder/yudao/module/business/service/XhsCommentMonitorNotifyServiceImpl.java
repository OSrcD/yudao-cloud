package cn.iocoder.yudao.module.business.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorNotifyConfigVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorNotifyConfigDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorNotifyLogDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsCommentMonitorNotifyConfigMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsCommentMonitorNotifyLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class XhsCommentMonitorNotifyServiceImpl implements XhsCommentMonitorNotifyService {

    @Resource
    private XhsCommentMonitorNotifyLogMapper notifyLogMapper;

    @Resource
    private XhsCommentMonitorNotifyConfigMapper notifyConfigMapper;

    @Override
    public void checkAndNotify(List<XhsNoteCommentDO> comments, List<String> keywords, XhsCommentMonitorNotifyConfigVO config) {
        if (CollUtil.isEmpty(comments) || CollUtil.isEmpty(keywords) || config == null) {
            return;
        }
        if (!Boolean.TRUE.equals(config.getEnabled())) {
            return;
        }
        if (StrUtil.isBlank(config.getUrl())) {
            log.warn("[评论监控推送] 推送地址未配置，跳过推送");
            return;
        }

        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) tenantId = 1L;
        final Long finalTenantId = tenantId;

        for (XhsNoteCommentDO comment : comments) {
            if (StrUtil.isBlank(comment.getContent())) {
                continue;
            }
            for (String keyword : keywords) {
                if (!StrUtil.containsIgnoreCase(comment.getContent(), keyword)) {
                    continue;
                }
                // 去重检查
                if (notifyLogMapper.existsByCommentIdAndKeyword(comment.getCommentId(), keyword, finalTenantId)) {
                    log.debug("[评论监控推送] 评论[{}] 关键词[{}] 已推送过，跳过", comment.getCommentId(), keyword);
                    continue;
                }
                // 执行推送
                boolean success = false;
                String resp = "";
                try {
                    String body = buildNotifyBody(comment, keyword, config.getType());
                    HttpResponse response = HttpRequest.post(config.getUrl())
                            .header("Content-Type", "application/json")
                            .body(body)
                            .timeout(5000)
                            .execute();
                    resp = response.body();
                    success = response.isOk();
                    log.info("[评论监控推送] 评论[{}] 关键词[{}] 推送{}, 响应: {}",
                            comment.getCommentId(), keyword, success ? "成功" : "失败", resp);
                } catch (Exception e) {
                    log.error("[评论监控推送] 评论[{}] 关键词[{}] 推送异常", comment.getCommentId(), keyword, e);
                    resp = e.getMessage();
                }
                // 写入去重日志（无论成功失败都记录，防止失败后重复推送）
                try {
                    XhsCommentMonitorNotifyLogDO logDO = XhsCommentMonitorNotifyLogDO.builder()
                            .tenantId(finalTenantId)
                            .commentId(comment.getCommentId())
                            .keyword(keyword)
                            .notifyType(config.getType())
                            .notifyUrl(config.getUrl())
                            .notifyStatus(success ? 0 : 1)
                            .notifyResp(StrUtil.maxLength(resp, 1000))
                            .createTime(LocalDateTime.now())
                            .build();
                    notifyLogMapper.insert(logDO);
                } catch (Exception e) {
                    log.error("[评论监控推送] 写入推送日志异常，commentId={}, keyword={}", comment.getCommentId(), keyword, e);
                }
            }
        }
    }

    @Override
    public boolean sendTest(XhsCommentMonitorNotifyConfigVO config) {
        if (config == null || StrUtil.isBlank(config.getUrl())) {
            return false;
        }
        try {
            String body = buildTestBody(config.getType());
            HttpResponse response = HttpRequest.post(config.getUrl())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .timeout(5000)
                    .execute();
            log.info("[评论监控推送] 测试推送，url={}, status={}, resp={}", config.getUrl(), response.getStatus(), response.body());
            return response.isOk();
        } catch (Exception e) {
            log.error("[评论监控推送] 测试推送异常，url={}", config.getUrl(), e);
            return false;
        }
    }

    @Override
    public XhsCommentMonitorNotifyConfigVO getNotifyConfig() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) tenantId = 1L;
        XhsCommentMonitorNotifyConfigDO configDO = notifyConfigMapper.selectOne(
                new LambdaQueryWrapper<XhsCommentMonitorNotifyConfigDO>().last("LIMIT 1"));
        if (configDO == null) {
            XhsCommentMonitorNotifyConfigVO vo = new XhsCommentMonitorNotifyConfigVO();
            vo.setEnabled(false);
            vo.setType("webhook");
            vo.setUrl("");
            vo.setSecret("");
            return vo;
        }
        XhsCommentMonitorNotifyConfigVO vo = new XhsCommentMonitorNotifyConfigVO();
        vo.setEnabled(configDO.getEnabled());
        vo.setType(configDO.getNotifyType());
        vo.setUrl(configDO.getNotifyUrl());
        vo.setSecret(configDO.getSecret());
        return vo;
    }

    @Override
    public void saveNotifyConfig(XhsCommentMonitorNotifyConfigVO config) {
        XhsCommentMonitorNotifyConfigDO existing = notifyConfigMapper.selectOne(
                new LambdaQueryWrapper<XhsCommentMonitorNotifyConfigDO>().last("LIMIT 1"));
        if (existing == null) {
            XhsCommentMonitorNotifyConfigDO newDO = new XhsCommentMonitorNotifyConfigDO();
            newDO.setEnabled(config.getEnabled());
            newDO.setNotifyType(config.getType());
            newDO.setNotifyUrl(config.getUrl());
            newDO.setSecret(config.getSecret());
            notifyConfigMapper.insert(newDO);
        } else {
            existing.setEnabled(config.getEnabled());
            existing.setNotifyType(config.getType());
            existing.setNotifyUrl(config.getUrl());
            existing.setSecret(config.getSecret());
            notifyConfigMapper.updateById(existing);
        }
    }

    private String buildNotifyBody(XhsNoteCommentDO comment, String keyword, String type) {
        String text = String.format(
                "[小红书评论监控] 命中关键词「%s」\n评论者: %s\n内容: %s\n评论时间: %s\n笔记ID: %s",
                keyword,
                StrUtil.blankToDefault(comment.getNickname(), "未知"),
                comment.getContent(),
                StrUtil.blankToDefault(comment.getCommentTime(), "未知"),
                comment.getNoteId()
        );
        if ("wecom".equalsIgnoreCase(type)) {
            return String.format("{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}", escapeJson(text));
        } else if ("feishu".equalsIgnoreCase(type)) {
            return String.format("{\"msg_type\":\"text\",\"content\":{\"text\":\"%s\"}}", escapeJson(text));
        } else {
            // 通用 webhook
            return String.format("{\"text\":\"%s\"}", escapeJson(text));
        }
    }

    private String buildTestBody(String type) {
        String text = "[小红书评论监控] 测试推送 - 配置成功！";
        if ("wecom".equalsIgnoreCase(type)) {
            return String.format("{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}", text);
        } else if ("feishu".equalsIgnoreCase(type)) {
            return String.format("{\"msg_type\":\"text\",\"content\":{\"text\":\"%s\"}}", text);
        } else {
            return String.format("{\"text\":\"%s\"}", text);
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
