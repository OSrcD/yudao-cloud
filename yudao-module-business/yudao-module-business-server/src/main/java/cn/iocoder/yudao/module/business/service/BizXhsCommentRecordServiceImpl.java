package cn.iocoder.yudao.module.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentAnalysisRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsCommentRecordDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizXhsCommentRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception0;

@Service
@Validated
@Slf4j
public class BizXhsCommentRecordServiceImpl implements BizXhsCommentRecordService {

    private static final Pattern XHS_NOTE_ID_PATTERN = Pattern.compile("(?i)(?:item/|explore/|note/|a/)([a-f0-9]{24}|[a-zA-Z0-9_-]{8,32})");

    @Resource
    private BizXhsCommentRecordMapper xhsCommentRecordMapper;

    @Override
    public Boolean checkDuplicate(Long appAccountId, String shareLink, String noteId, String noteTitle, Integer dedupMode) {
        if (appAccountId == null) {
            return false;
        }
        String effNoteId = StrUtil.isNotBlank(noteId) ? noteId.trim() : (StrUtil.isNotBlank(shareLink) ? extractNoteId(shareLink) : "");
        String effShareLink = StrUtil.isNotBlank(shareLink) ? shareLink.trim() : "";
        String effNoteTitle = StrUtil.isNotBlank(noteTitle) ? noteTitle.trim() : "";
        return xhsCommentRecordMapper.existsByAccount(appAccountId, effShareLink, effNoteId, effNoteTitle, dedupMode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCommentRecord(BizXhsCommentRecordSaveReqVO createReqVO) {
        if (createReqVO == null) {
            throw exception0(400, "评论记录请求参数不能为空");
        }
        BizXhsCommentRecordDO record = BeanUtil.copyProperties(createReqVO, BizXhsCommentRecordDO.class);
        if (StrUtil.isBlank(record.getNoteId()) && StrUtil.isNotBlank(record.getShareLink())) {
            record.setNoteId(extractNoteId(record.getShareLink()));
        }
        if (record.getCommentStatus() == null) {
            record.setCommentStatus(0); // 默认未知 (待检测)
        }
        if (record.getCheckStatus() == null) {
            record.setCheckStatus(0); // 默认未检测
        }
        xhsCommentRecordMapper.insert(record);
        log.info("【小红书评论记录入库】id={} appAccountId={} xhsUserId={} noteId={}",
                record.getId(), record.getAppAccountId(), record.getXhsUserId(), record.getNoteId());
        return record.getId();
    }

    @Override
    public void updateCommentRecord(BizXhsCommentRecordSaveReqVO updateReqVO) {
        if (updateReqVO == null || updateReqVO.getId() == null) {
            throw exception0(400, "记录编号不能为空");
        }
        BizXhsCommentRecordDO record = BeanUtil.copyProperties(updateReqVO, BizXhsCommentRecordDO.class);
        if (StrUtil.isBlank(record.getNoteId()) && StrUtil.isNotBlank(record.getShareLink())) {
            record.setNoteId(extractNoteId(record.getShareLink()));
        }
        xhsCommentRecordMapper.updateById(record);
    }

    @Override
    public void deleteCommentRecord(Long id) {
        xhsCommentRecordMapper.deleteById(id);
    }

    @Override
    public BizXhsCommentRecordDO getCommentRecord(Long id) {
        return xhsCommentRecordMapper.selectById(id);
    }

    @Override
    public PageResult<BizXhsCommentRecordRespVO> getCommentRecordPage(BizXhsCommentRecordPageReqVO pageReqVO) {
        PageResult<BizXhsCommentRecordDO> pageResult = xhsCommentRecordMapper.selectPage(pageReqVO);
        return new PageResult<>(BeanUtil.copyToList(pageResult.getList(), BizXhsCommentRecordRespVO.class), pageResult.getTotal());
    }

    @Override
    public BizXhsCommentAnalysisRespVO getCommentAnalysisPreview() {
        long total = xhsCommentRecordMapper.selectCount(new LambdaQueryWrapper<BizXhsCommentRecordDO>());
        long unknown = xhsCommentRecordMapper.selectCount(new LambdaQueryWrapper<BizXhsCommentRecordDO>()
                .eq(BizXhsCommentRecordDO::getCommentStatus, 0));
        long normal = xhsCommentRecordMapper.selectCount(new LambdaQueryWrapper<BizXhsCommentRecordDO>()
                .eq(BizXhsCommentRecordDO::getCommentStatus, 1));
        long swallowed = xhsCommentRecordMapper.selectCount(new LambdaQueryWrapper<BizXhsCommentRecordDO>()
                .eq(BizXhsCommentRecordDO::getCommentStatus, 2));
        long folded = xhsCommentRecordMapper.selectCount(new LambdaQueryWrapper<BizXhsCommentRecordDO>()
                .eq(BizXhsCommentRecordDO::getCommentStatus, 3));
        long pending = xhsCommentRecordMapper.selectCount(new LambdaQueryWrapper<BizXhsCommentRecordDO>()
                .eq(BizXhsCommentRecordDO::getCheckStatus, 0));

        BigDecimal normalRate = calculateRate(normal, total);
        BigDecimal swallowedRate = calculateRate(swallowed, total);
        BigDecimal foldedRate = calculateRate(folded, total);

        return BizXhsCommentAnalysisRespVO.builder()
                .totalCount(total)
                .unknownCount(unknown)
                .normalCount(normal)
                .swallowedCount(swallowed)
                .foldedCount(folded)
                .pendingCheckCount(pending)
                .normalRate(normalRate)
                .swallowedRate(swallowedRate)
                .foldedRate(foldedRate)
                .build();
    }

    private BigDecimal calculateRate(long count, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    private String extractNoteId(String text) {
        if (StrUtil.isBlank(text)) return "";
        Matcher matcher = XHS_NOTE_ID_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

}
