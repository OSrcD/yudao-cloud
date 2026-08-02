package cn.iocoder.yudao.module.business.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCollectSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCollectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Validated
public class XhsNoteCollectServiceImpl implements XhsNoteCollectService {

    @Resource
    private XhsNoteCollectMapper xhsNoteCollectMapper;

    @Override
    public Boolean saveOrUpdateXhsNote(XhsNoteCollectSaveReqVO reqVO) {
        if (StrUtil.isBlank(reqVO.getNoteId())) {
            log.warn("[笔记采集入库] 传入请求缺少 noteId，忽略保存");
            return false;
        }

        // 跨租户查询是否存在相同 note_id 的记录进行去重
        XhsNoteCollectDO existDO = TenantUtils.executeIgnore(() -> 
            xhsNoteCollectMapper.selectOne(
                new LambdaQueryWrapper<XhsNoteCollectDO>()
                        .eq(XhsNoteCollectDO::getNoteId, reqVO.getNoteId())
                        .last("LIMIT 1")
            )
        );

        Long tenantId = cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId == 0L) {
            tenantId = 1L; // 默认绑定租户 1
        }
        final Long targetTenantId = tenantId;

        if (existDO != null) {
            // 存在 -> 修改
            log.info("[笔记采集入库] 笔记ID [{}] 【在数据库中已存在】，执行【修改/更新】操作 (记录ID: {}, 标题: '{}', 作者: '{}')", 
                    reqVO.getNoteId(), existDO.getId(), reqVO.getTitle(), reqVO.getUserName());
            XhsNoteCollectDO updateDO = buildDO(reqVO);
            updateDO.setId(existDO.getId());
            TenantUtils.execute(targetTenantId, () -> xhsNoteCollectMapper.updateById(updateDO));
            return true;
        } else {
            // 不存在 -> 新增
            log.info("[笔记采集入库] 笔记ID [{}] 【在数据库中不存在】，执行【新增/插入】操作 (标题: '{}', 作者: '{}')", 
                    reqVO.getNoteId(), reqVO.getTitle(), reqVO.getUserName());
            XhsNoteCollectDO insertDO = buildDO(reqVO);
            TenantUtils.execute(targetTenantId, () -> xhsNoteCollectMapper.insert(insertDO));
            return true;
        }
    }

    @Override
    public Integer saveOrUpdateXhsNoteBatch(List<XhsNoteCollectSaveReqVO> list) {
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        int count = 0;
        for (XhsNoteCollectSaveReqVO reqVO : list) {
            try {
                if (saveOrUpdateXhsNote(reqVO)) {
                    count++;
                }
            } catch (Exception e) {
                log.error("[笔记采集入库异常] 笔记ID: {}", reqVO.getNoteId(), e);
            }
        }
        return count;
    }

    @Override
    public void deleteXhsNoteCollect(Long id) {
        xhsNoteCollectMapper.deleteById(id);
    }

    @Override
    public void deleteXhsNoteCollectBatch(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        xhsNoteCollectMapper.deleteBatchIds(ids);
    }

    @Override
    public void updateAllNotesValid() {
        xhsNoteCollectMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<XhsNoteCollectDO>()
                .eq(XhsNoteCollectDO::getIsLinkInvalid, true)
                .set(XhsNoteCollectDO::getIsLinkInvalid, false)
                .set(XhsNoteCollectDO::getLinkInvalidTime, null));
    }

    @Override
    public XhsNoteCollectDO getXhsNoteCollect(Long id) {
        return xhsNoteCollectMapper.selectById(id);
    }

    @Override
    public PageResult<XhsNoteCollectDO> getXhsNoteCollectPage(XhsNoteCollectPageReqVO pageReqVO) {
        return xhsNoteCollectMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateCommentStatus(String noteId) {
        XhsNoteCollectDO existDO = TenantUtils.executeIgnore(() -> 
            xhsNoteCollectMapper.selectOne(
                new LambdaQueryWrapper<XhsNoteCollectDO>()
                        .eq(XhsNoteCollectDO::getNoteId, noteId)
                        .last("LIMIT 1")
            )
        );
        if (existDO != null) {
            XhsNoteCollectDO updateDO = new XhsNoteCollectDO();
            updateDO.setId(existDO.getId());
            updateDO.setIsCommentCollected(true);
            updateDO.setLastCommentCollectTime(java.time.LocalDateTime.now());
            TenantUtils.executeIgnore(() -> xhsNoteCollectMapper.updateById(updateDO));
        }
    }

    @Override
    public void updateLinkInvalid(String noteId) {
        XhsNoteCollectDO existDO = TenantUtils.executeIgnore(() -> 
            xhsNoteCollectMapper.selectOne(
                new LambdaQueryWrapper<XhsNoteCollectDO>()
                        .eq(XhsNoteCollectDO::getNoteId, noteId)
                        .last("LIMIT 1")
            )
        );
        if (existDO != null) {
            XhsNoteCollectDO updateDO = new XhsNoteCollectDO();
            updateDO.setId(existDO.getId());
            updateDO.setIsLinkInvalid(true);
            updateDO.setLinkInvalidTime(java.time.LocalDateTime.now());
            TenantUtils.executeIgnore(() -> xhsNoteCollectMapper.updateById(updateDO));
        }
    }

    @Override
    public void updateMonitorStatus(List<Long> ids, Boolean isMonitored) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            XhsNoteCollectDO updateDO = new XhsNoteCollectDO();
            updateDO.setId(id);
            updateDO.setIsMonitored(isMonitored);
            xhsNoteCollectMapper.updateById(updateDO);
        }
    }

    @Override
    public PageResult<XhsNoteCollectDO> getMonitoredPage(XhsNoteCollectPageReqVO pageReqVO) {
        pageReqVO.setIsMonitored(true);
        return xhsNoteCollectMapper.selectPage(pageReqVO);
    }

    private XhsNoteCollectDO buildDO(XhsNoteCollectSaveReqVO reqVO) {
        XhsNoteCollectDO doObj = new XhsNoteCollectDO();
        doObj.setNoteId(reqVO.getNoteId());
        
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getKeyword())) doObj.setKeyword(reqVO.getKeyword());
        if (reqVO.getNoteType() != null) doObj.setNoteType(reqVO.getNoteType());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getTitle())) doObj.setTitle(reqVO.getTitle());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getDesc())) doObj.setNoteDesc(reqVO.getDesc());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getNoteUrl())) doObj.setNoteUrl(reqVO.getNoteUrl());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getShellCmd())) doObj.setShellCmd(reqVO.getShellCmd());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getPublishTime())) doObj.setPublishTime(reqVO.getPublishTime());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getUserName())) doObj.setUserName(reqVO.getUserName());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getUserId())) doObj.setUserId(reqVO.getUserId());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getRedId())) doObj.setRedId(reqVO.getRedId());
        if (reqVO.getLikedCount() != null) doObj.setLikedCount(reqVO.getLikedCount());
        if (reqVO.getCollectedCount() != null) doObj.setCollectedCount(reqVO.getCollectedCount());
        if (reqVO.getCommentsCount() != null) doObj.setCommentsCount(reqVO.getCommentsCount());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getRawJson())) doObj.setRawJson(reqVO.getRawJson());
        if (cn.hutool.core.util.StrUtil.isNotEmpty(reqVO.getPcShareLink())) {
            doObj.setPcShareLink(reqVO.getPcShareLink());
            doObj.setLastPcShareCollectTime(java.time.LocalDateTime.now());
        }
        return doObj;
    }

}
