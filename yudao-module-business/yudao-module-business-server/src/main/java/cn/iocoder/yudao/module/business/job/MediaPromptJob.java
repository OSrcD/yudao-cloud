package cn.iocoder.yudao.module.business.job;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;

import cn.iocoder.yudao.module.business.controller.admin.vo.*;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMediaAccountDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentCompleteDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptCommentDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizPromptTemplateDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizMediaAccountMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptCommentCompleteMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptCommentMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizPromptTemplateMapper;
import cn.iocoder.yudao.module.business.service.BizPromptCommentService;
import cn.iocoder.yudao.module.business.util.PollinationsAI;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MediaPromptJob {

    @Resource
    private BizPromptTemplateMapper bizPromptTemplateMapper;

    @Resource
    private BizPromptCommentService promptCommentService;

    @Resource
    private BizMediaAccountMapper bizMediaAccountMapper;

    @Resource
    private BizPromptCommentMapper bizPromptCommentMapper;

    @Resource
    private BizPromptCommentCompleteMapper bizPromptCommentCompleteMapper;

    @Scheduled(cron = "0 * * * * ?")
    @TenantIgnore
    public void clearCompletedCommentsJob() {
        log.info("开始执行定期清理已用满评论账号的完成记录任务...");
        List<BizMediaAccountDO> accounts = bizMediaAccountMapper.selectList(new LambdaQueryWrapper<>());
        for (BizMediaAccountDO account : accounts) {
            Long mediaAccountId = account.getId();
            Integer platform = account.getAccountPlatform();
            
            // 检查是否有未使用列表
            List<BizPromptCommentDO> unusedList = bizPromptCommentMapper.selectUnusedList(mediaAccountId, platform);
            if (unusedList == null || unusedList.isEmpty()) {
                // 如果为空，并且complete表里该账号有数据，说明所有有效的评论都已被该账号用完
                Long count = bizPromptCommentCompleteMapper.selectCount(new LambdaQueryWrapper<BizPromptCommentCompleteDO>()
                        .eq(BizPromptCommentCompleteDO::getMediaAccountId, mediaAccountId)
                        .ne(BizPromptCommentCompleteDO::getXhsNoteInfo, ""));
                
                if (count != null && count > 0) {
                    log.info("自媒体账号ID {} (平台 {}) 评论已全部用完，开始将其 completed 记录执行逻辑删除", mediaAccountId, platform);
                    bizPromptCommentCompleteMapper.delete(new LambdaQueryWrapper<BizPromptCommentCompleteDO>()
                            .eq(BizPromptCommentCompleteDO::getMediaAccountId, mediaAccountId)
                            .ne(BizPromptCommentCompleteDO::getXhsNoteInfo, ""));
                }
            }
        }
        log.info("定期清理已用满评论任务执行结束");
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    @TenantIgnore
    public void generateAICommentsJob() {
        log.info("开始执行 AI 评论自动生成任务...");
        List<BizPromptTemplateDO> templates = bizPromptTemplateMapper.selectList(new LambdaQueryWrapper<BizPromptTemplateDO>()
                .eq(BizPromptTemplateDO::getStatus, 0)); // 仅处理启用的模板
        for (BizPromptTemplateDO template : templates) {
            try {
                insertPromptText(template);
            } catch (Exception e) {
                log.error("AI 评论生成失败, TemplateId: {}", template.getId(), e);
            }
        }
        log.info("AI 评论自动生成任务执行结束");
    }

    private void insertPromptText(BizPromptTemplateDO bizPromptTemplate) {
        if (bizPromptTemplate != null) {
            log.info("当前提示词模板：{} 类型：{} 开始执行", bizPromptTemplate.getRemark(), bizPromptTemplate.getTemplateType());
            String commentByPrompt = PollinationsAI.getCommentByPrompt(bizPromptTemplate.getTemplate());
            if (commentByPrompt == null) return;

            if (bizPromptTemplate.getTemplateType() == 2) { // 降低相似度
                BeanOutputConverter<PrompCommentSimBo> converter = new BeanOutputConverter<>(PrompCommentSimBo.class);
                PrompCommentSimBo convert = converter.convert(commentByPrompt);
                if (convert != null && convert.getCommentList() != null) {
                    List<BizPromptCommentSaveReqVO> voList = new ArrayList<>();
                    for (CommentSimilarityBo bo : convert.getCommentList()) {
                        BizPromptCommentSaveReqVO vo = new BizPromptCommentSaveReqVO();
                        vo.setPromptId(bizPromptTemplate.getId());
                        vo.setTitle(bo.getSearchTile());
                        vo.setCommentContent(bo.getComments());
                        voList.add(vo);
                    }
                    promptCommentService.createPromptCommentBatch(voList);
                }
            } else if (bizPromptTemplate.getTemplateType() == 3) { // AI操作系列
                BeanOutputConverter<AIOperateListBo> converter = new BeanOutputConverter<>(AIOperateListBo.class);
                AIOperateListBo convert = converter.convert(commentByPrompt);
                if (convert != null && convert.getOperateList() != null) {
                    List<BizPromptCommentSaveReqVO> voList = new ArrayList<>();
                    long operateGroupId = IdWorker.getId();
                    for (AIOperateSequenceBo bo : convert.getOperateList()) {
                        BizPromptCommentSaveReqVO vo = new BizPromptCommentSaveReqVO();
                        vo.setPromptId(bizPromptTemplate.getId());
                        vo.setCommentContent(bo.getOperate());
                        vo.setOperateGroupId(operateGroupId);
                        vo.setRemark("时间段：" + bo.getTimeOfDay() + "，操作时间：" + bo.getCurrentTime());
                        voList.add(vo);
                    }
                    promptCommentService.createPromptCommentBatch(voList);
                }
            } else {
                BeanOutputConverter<PrompCommentDto> converter = new BeanOutputConverter<>(PrompCommentDto.class);
                PrompCommentDto convert = converter.convert(commentByPrompt);
                if (convert != null && convert.getCommentList() != null) {
                    List<BizPromptCommentSaveReqVO> voList = new ArrayList<>();
                    for (String comment : convert.getCommentList()) {
                        BizPromptCommentSaveReqVO vo = new BizPromptCommentSaveReqVO();
                        vo.setPromptId(bizPromptTemplate.getId());
                        vo.setCommentContent(comment);
                        voList.add(vo);
                    }
                    promptCommentService.createPromptCommentBatch(voList);
                }
            }
        }
    }
}
