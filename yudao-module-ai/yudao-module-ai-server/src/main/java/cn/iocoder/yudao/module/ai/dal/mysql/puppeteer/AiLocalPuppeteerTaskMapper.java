package cn.iocoder.yudao.module.ai.dal.mysql.puppeteer;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.puppeteer.AiLocalPuppeteerTaskDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 本地 Puppeteer 执行任务 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiLocalPuppeteerTaskMapper extends BaseMapperX<AiLocalPuppeteerTaskDO> {
}
