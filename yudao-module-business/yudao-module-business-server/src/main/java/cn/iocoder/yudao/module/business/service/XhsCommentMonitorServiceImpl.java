package cn.iocoder.yudao.module.business.service;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.XhsCommentMonitorPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsCommentMonitorKeywordDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCollectDO;
import cn.iocoder.yudao.module.business.dal.dataobject.XhsNoteCommentDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsCommentMonitorKeywordMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCollectMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.XhsNoteCommentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class XhsCommentMonitorServiceImpl implements XhsCommentMonitorService {

    @Resource
    private XhsCommentMonitorKeywordMapper keywordMapper;

    @Resource
    private XhsNoteCollectMapper noteCollectMapper;

    @Resource
    private XhsNoteCommentMapper noteCommentMapper;

    @Override
    public PageResult<XhsNoteCommentDO> getCommentMonitorPage(XhsCommentMonitorPageReqVO reqVO) {
        List<String> monitoredNoteIds = getMonitoredNoteIds();
        if (CollUtil.isEmpty(monitoredNoteIds)) {
            return PageResult.empty();
        }
        List<String> keywords = getKeywordStrings();
        return noteCommentMapper.selectMonitorPage(reqVO, monitoredNoteIds, keywords);
    }

    @Override
    public List<XhsCommentMonitorKeywordDO> getKeywords() {
        return keywordMapper.selectAll();
    }

    @Override
    public XhsCommentMonitorKeywordDO addKeyword(String keyword) {
        XhsCommentMonitorKeywordDO exist = keywordMapper.selectOne(
                new LambdaQueryWrapper<XhsCommentMonitorKeywordDO>()
                        .eq(XhsCommentMonitorKeywordDO::getKeyword, keyword)
                        .last("LIMIT 1"));
        if (exist != null) {
            return exist;
        }
        XhsCommentMonitorKeywordDO kw = XhsCommentMonitorKeywordDO.builder()
                .keyword(keyword)
                .build();
        keywordMapper.insert(kw);
        return kw;
    }

    @Override
    public void deleteKeyword(Long id) {
        keywordMapper.deleteById(id);
    }

    @Override
    public List<String> getMonitoredNoteIds() {
        List<XhsNoteCollectDO> monitored = noteCollectMapper.selectList(
                new LambdaQueryWrapper<XhsNoteCollectDO>()
                        .eq(XhsNoteCollectDO::getIsMonitored, true)
                        .select(XhsNoteCollectDO::getNoteId));
        if (CollUtil.isEmpty(monitored)) {
            return Collections.emptyList();
        }
        return monitored.stream()
                .map(XhsNoteCollectDO::getNoteId)
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getKeywordStrings() {
        return getKeywords().stream()
                .map(XhsCommentMonitorKeywordDO::getKeyword)
                .collect(Collectors.toList());
    }

}
