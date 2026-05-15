package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostSaveReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizScraperPostDO;

import jakarta.validation.Valid;

/**
 * 采集帖子 Service 接口
 */
public interface BizScraperPostService {

    /**
     * 创建采集帖子
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createScraperPost(@Valid BizScraperPostSaveReqVO createReqVO);

    /**
     * 更新采集帖子
     *
     * @param updateReqVO 更新信息
     */
    void updateScraperPost(@Valid BizScraperPostSaveReqVO updateReqVO);

    /**
     * 删除采集帖子
     *
     * @param id 编号
     */
    void deleteScraperPost(Long id);

    /**
     * 获得采集帖子
     *
     * @param id 编号
     * @return 采集帖子
     */
    BizScraperPostDO getScraperPost(Long id);

    /**
     * 获得采集帖子分页
     *
     * @param pageReqVO 分页查询
     * @return 采集帖子分页
     */
    PageResult<BizScraperPostDO> getScraperPostPage(BizScraperPostPageReqVO pageReqVO);

    /**
     * 保存采集数据
     */
    boolean saveScrapedData(BizScraperPostDO post);

    /**
     * 查询已存在的ID列表
     */
    java.util.List<String> selectExistingIds(String platform, java.util.List<String> postIds);

    /**
     * 标记视频刷新
     */
    void markVideoRefresh(Long scraperId);

    /**
     * 获取待刷新视频列表
     */
    java.util.List<BizScraperPostDO> getPendingRefreshPosts();

    /**
     * 更新媒体文件
     */
    void updateMedia(Long scraperId, String images, String videos);

    /**
     * 更新重写信息
     */
    void updateRestyleInfo(Long scraperId, String restyleInfo);

}

