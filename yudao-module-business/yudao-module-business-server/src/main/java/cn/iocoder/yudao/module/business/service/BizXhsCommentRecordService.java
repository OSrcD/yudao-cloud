package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentAnalysisRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordRespVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizXhsCommentRecordSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizXhsCommentRecordDO;
import jakarta.validation.Valid;

/**
 * 小红书评论记录与去重分析 Service 接口
 */
public interface BizXhsCommentRecordService {

    /**
     * 检查当前App账号下是否已存在该作品的评论记录
     *
     * @param appAccountId App账号ID
     * @param shareLink    作品分享链接
     * @param noteId       提取的笔记ID(可选)
     * @param noteTitle    提取的作品标题(可选)
     * @param dedupMode    查重模式: 0仅对比链接, 1仅对比标题, 2标题和链接同时对比
     * @return true 已存在(重复), false 不存在(未评论)
     */
    Boolean checkDuplicate(Long appAccountId, String shareLink, String noteId, String noteTitle, Integer dedupMode);

    /**
     * 创建小红书评论记录
     *
     * @param createReqVO 创建信息
     * @return 记录编号
     */
    Long createCommentRecord(@Valid BizXhsCommentRecordSaveReqVO createReqVO);

    /**
     * 更新小红书评论记录
     *
     * @param updateReqVO 更新信息
     */
    void updateCommentRecord(@Valid BizXhsCommentRecordSaveReqVO updateReqVO);

    /**
     * 删除小红书评论记录
     *
     * @param id 记录编号
     */
    void deleteCommentRecord(Long id);

    /**
     * 获得小红书评论记录
     *
     * @param id 记录编号
     * @return 记录详情
     */
    BizXhsCommentRecordDO getCommentRecord(Long id);

    /**
     * 获得小红书评论记录分页
     *
     * @param pageReqVO 分页查询
     * @return 记录分页
     */
    PageResult<BizXhsCommentRecordRespVO> getCommentRecordPage(BizXhsCommentRecordPageReqVO pageReqVO);

    /**
     * 获取评论数据分析统计预览 (吞评/折叠/正常率统计)
     *
     * @return 数据分析统计结果
     */
    BizXhsCommentAnalysisRespVO getCommentAnalysisPreview();

}
