package cn.iocoder.yudao.module.business.service;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizScraperPostSaveReqVO;
import cn.iocoder.yudao.module.business.convert.BizScraperPostConvert;
import cn.iocoder.yudao.module.business.dal.dataobject.BizScraperPostDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizScraperPostMapper;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采集帖子 Service 实现类
 */
@Slf4j
@Service
@Validated
public class BizScraperPostServiceImpl implements BizScraperPostService {

    @Resource
    private BizScraperPostMapper scraperPostMapper;

    @Override
    public Long createScraperPost(BizScraperPostSaveReqVO createReqVO) {
        BizScraperPostDO scraperPost = BizScraperPostConvert.INSTANCE.convert(createReqVO);
        scraperPostMapper.insert(scraperPost);
        return scraperPost.getId();
    }

    @Override
    public void updateScraperPost(BizScraperPostSaveReqVO updateReqVO) {
        BizScraperPostDO updateObj = BizScraperPostConvert.INSTANCE.convert(updateReqVO);
        scraperPostMapper.updateById(updateObj);
    }

    @Override
    public void deleteScraperPost(Long id) {
        scraperPostMapper.deleteById(id);
    }

    @Override
    public BizScraperPostDO getScraperPost(Long id) {
        return scraperPostMapper.selectById(id);
    }

    @Override
    public PageResult<BizScraperPostDO> getScraperPostPage(BizScraperPostPageReqVO pageReqVO) {
        return scraperPostMapper.selectPage(pageReqVO);
    }

    @Override
    public boolean saveScrapedData(BizScraperPostDO post) {
        try {
            int rows = scraperPostMapper.insert(post);
            if (rows > 0) {
                log.info("【业务采集同步】平台标识:[{}], 帖子ID:[{}] 已存入 biz_scraper_post", post.getPlatform(), post.getPostId());
                return true;
            }
            return false;
        } catch (DuplicateKeyException e) {
            log.info("【业务数据去重】平台标识:[{}], 帖子ID:[{}] 数据已存在，自动跳过", post.getPlatform(), post.getPostId());
            return false;
        } catch (Exception e) {
            log.error("【业务入库异常】平台:[{}], PostID:[{}]", post.getPlatform(), post.getPostId(), e);
            throw e;
        }
    }

    @Override
    public List<String> selectExistingIds(String platform, List<String> postIds) {
        if (postIds == null || postIds.isEmpty()) return new java.util.ArrayList<>();
        
        return TenantUtils.executeIgnore(() -> 
            scraperPostMapper.selectList(new LambdaQueryWrapper<BizScraperPostDO>()
                .eq(BizScraperPostDO::getPlatform, platform)
                .in(BizScraperPostDO::getPostId, postIds)
                .select(BizScraperPostDO::getPostId))
                .stream()
                .map(BizScraperPostDO::getPostId)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void markVideoRefresh(Long scraperId) {
        BizScraperPostDO update = new BizScraperPostDO();
        update.setId(scraperId);
        update.setVideoStatus("1");
        scraperPostMapper.updateById(update);
    }

    @Override
    public List<BizScraperPostDO> getPendingRefreshPosts() {
        return TenantUtils.executeIgnore(() ->
            scraperPostMapper.selectList(new LambdaQueryWrapper<BizScraperPostDO>()
                .eq(BizScraperPostDO::getVideoStatus, "1")
                .select(BizScraperPostDO::getId, BizScraperPostDO::getSourceUrl, BizScraperPostDO::getPlatform))
        );
    }

    @Override
    public void updateMedia(Long scraperId, String images, String videos) {
        BizScraperPostDO update = new BizScraperPostDO();
        update.setId(scraperId);
        update.setImages(JsonUtils.parseArray(images, Object.class));
        update.setVideos(JsonUtils.parseArray(videos, Object.class));
        update.setVideoStatus("0");
        scraperPostMapper.updateById(update);
    }

    @Override
    public void updateRestyleInfo(Long scraperId, String restyleInfo) {
        BizScraperPostDO update = new BizScraperPostDO();
        update.setId(scraperId);
        update.setRestyleInfo(JsonUtils.parseArray(restyleInfo, Object.class));
        scraperPostMapper.updateById(update);
    }

}
