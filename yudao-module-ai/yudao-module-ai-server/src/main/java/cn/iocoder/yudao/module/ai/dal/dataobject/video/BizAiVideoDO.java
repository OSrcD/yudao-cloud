package cn.iocoder.yudao.module.ai.dal.dataobject.video;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.ai.enums.image.AiImageStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI 视频 DO
 *
 * @author CuiMa
 */
@TableName(value = "biz_ai_video", autoResultMap = true)
@KeySequence("biz_ai_video_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizAiVideoDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 平台
     */
    private String platform;
    /**
     * 模型编号
     */
    private Long modelId;
    /**
     * 模型
     */
    private String model;

    /**
     * 宽度
     */
    private Integer width;
    /**
     * 高度
     */
    private Integer height;
    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 状态
     *
     * 枚举 {@link AiImageStatusEnum}
     */
    private Integer status;
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 视频地址
     */
    private String videoUrl;
    /**
     * 预览图地址
     */
    private String previewUrl;

    /**
     * 配置选项
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> options;

    /**
     * 任务编号
     */
    private String taskId;

}
