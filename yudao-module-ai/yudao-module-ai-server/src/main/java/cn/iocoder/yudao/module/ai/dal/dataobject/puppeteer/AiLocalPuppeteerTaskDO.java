package cn.iocoder.yudao.module.ai.dal.dataobject.puppeteer;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 本地 Puppeteer 执行任务 DO
 *
 * @author 芋道源码
 */
@TableName("ai_local_puppeteer_task")
@KeySequence("ai_local_puppeteer_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiLocalPuppeteerTaskDO extends BaseDO {

    /**
     * 任务编号
     */
    @TableId
    private Long id;
    /**
     * 工作流编号
     */
    private Long workflowId;
    /**
     * 执行编号
     */
    private String executionId;
    /**
     * 节点编号
     */
    private String nodeId;

    /**
     * 模型类型
     *
     * 枚举 {@link TODO}
     */
    private String modelType;
    /**
     * 动作类型
     *
     * 枚举 {@link TODO}
     */
    private String actionType;

    /**
     * 参数
     *
     * JSON 格式
     */
    private String parameters;

    /**
     * 状态
     *
     * 0 待处理，1 执行中，2 已完成，3 已失败
     */
    private Integer status;
    /**
     * 结果数据
     *
     * JSON 格式
     */
    private String resultData;
    /**
     * 错误信息
     */
    private String errorMsg;

}
