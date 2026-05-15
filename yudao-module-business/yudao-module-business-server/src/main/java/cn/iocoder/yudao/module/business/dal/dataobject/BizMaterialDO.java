package cn.iocoder.yudao.module.business.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 素材 DO
 *
 * @author 芋道源码
 */
@TableName("biz_material")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizMaterialDO extends BaseDO {

    /**
     * 素材ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 素材名称
     */
    private String materialName;

    /**
     * 素材地址
     */
    private String materialUrl;

    /**
     * 文件类型（0图片 1视频）
     */
    private String fileType;

    /**
     * 备注
     */
    private String remark;

}
