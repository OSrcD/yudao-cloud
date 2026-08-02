package cn.iocoder.yudao.module.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsNoteCommentUpdateStatusReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCommentMapper;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCollectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

@Service
@Validated
@Slf4j
public class XhsNoteCommentServiceImpl implements XhsNoteCommentService {

    @Resource
    private XhsNoteCommentMapper xhsNoteCommentMapper;

    @Resource
    private XhsNoteCollectMapper xhsNoteCollectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdateXhsNoteComment(XhsNoteCommentSaveReqVO reqVO) {
        if (reqVO == null || StrUtil.isBlank(reqVO.getCommentId())) {
            return false;
        }

        if (StrUtil.isNotBlank(reqVO.getNoteId())) {
            XhsNoteCollectDO noteExistDO = cn.iocoder.yudao.framework.tenant.core.util.TenantUtils.executeIgnore(() -> 
                xhsNoteCollectMapper.selectOne(
                    new LambdaQueryWrapperX<XhsNoteCollectDO>()
                            .eq(XhsNoteCollectDO::getNoteId, reqVO.getNoteId())
                            .last("LIMIT 1")
                )
            );
            if (noteExistDO == null) {
                log.info("[XhsNoteCommentService] 所属笔记不存在，忽略评论入库: noteId={}, commentId={}", reqVO.getNoteId(), reqVO.getCommentId());
                return false;
            }
        }

        XhsNoteCommentDO existDO = cn.iocoder.yudao.framework.tenant.core.util.TenantUtils.executeIgnore(() -> 
            xhsNoteCommentMapper.selectOne(
                new LambdaQueryWrapperX<XhsNoteCommentDO>()
                        .eq(XhsNoteCommentDO::getCommentId, reqVO.getCommentId())
                        .last("LIMIT 1")
            )
        );

        if (existDO != null) {
            log.info("[XhsNoteCommentService] 评论已存在，忽略: {}", reqVO.getCommentId());
            return false;
        }

        XhsNoteCommentDO entity = BeanUtil.copyProperties(reqVO, XhsNoteCommentDO.class);
        if (entity.getInterceptStatus() == null) {
            entity.setInterceptStatus(0); // 默认未触达
        }
        xhsNoteCommentMapper.insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveOrUpdateXhsNoteCommentBatch(List<XhsNoteCommentSaveReqVO> reqVOList) {
        if (reqVOList == null || reqVOList.isEmpty()) {
            return 0;
        }

        int insertCount = 0;
        int duplicateCount = 0;
        for (XhsNoteCommentSaveReqVO reqVO : reqVOList) {
            if (reqVO == null || StrUtil.isBlank(reqVO.getCommentId())) {
                continue;
            }
            if (saveOrUpdateXhsNoteComment(reqVO)) {
                insertCount++;
            } else {
                duplicateCount++;
            }
        }
        log.info("[XhsNoteCommentService] 批量处理完毕: 总共新增了 {} 个评论，已存在 {} 个重复的", insertCount, duplicateCount);
        return insertCount;
    }

    @Override
    public Boolean updateInterceptStatus(XhsNoteCommentUpdateStatusReqVO reqVO) {
        XhsNoteCommentDO existDO = xhsNoteCommentMapper.selectById(reqVO.getId());
        if (existDO == null) {
            return false;
        }
        XhsNoteCommentDO updateDO = new XhsNoteCommentDO();
        updateDO.setId(reqVO.getId());
        updateDO.setInterceptStatus(reqVO.getInterceptStatus());
        if (reqVO.getRemark() != null) {
            updateDO.setRemark(reqVO.getRemark());
        }
        xhsNoteCommentMapper.updateById(updateDO);
        return true;
    }

    @Override
    public PageResult<XhsNoteCommentDO> getXhsNoteCommentPage(XhsNoteCommentPageReqVO reqVO) {
        return xhsNoteCommentMapper.selectPage(reqVO);
    }

    @Override
    public List<XhsNoteCommentDO> getCommentListByNoteId(String noteId) {
        return xhsNoteCommentMapper.selectList(
                new LambdaQueryWrapperX<XhsNoteCommentDO>()
                        .eq(XhsNoteCommentDO::getNoteId, noteId)
                        .orderByDesc(XhsNoteCommentDO::getId)
        );
    }

    @Override
    public void deleteXhsNoteComment(Long id) {
        xhsNoteCommentMapper.deleteById(id);
    }

}
