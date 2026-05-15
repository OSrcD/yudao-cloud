package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 爬取帖子记录 DO
 */
@TableName(value = "biz_scraper_post", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizScraperPostDO extends BaseDO {
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String platform;
    private String postId;
    private String title;
    private String content;
    private String author;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Object> images;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Object> videos;
    private String videoStatus;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Object> restyleInfo;
    private String sourceUrl;
    private String status;
    private String remark;
}
