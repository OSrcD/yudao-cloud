package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 小红书评论监控关键词配置 DO
 */
@TableName("xhs_comment_monitor_keyword")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XhsCommentMonitorKeywordDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 监控关键词
     */
    private String keyword;

}
