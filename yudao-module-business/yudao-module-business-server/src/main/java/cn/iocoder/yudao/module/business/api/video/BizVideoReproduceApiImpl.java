package cn.iocoder.yudao.module.business.api.video;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.module.business.api.video.dto.BizVideoReproduceFrameDTO;
import cn.iocoder.yudao.module.business.api.video.dto.BizVideoReproduceTaskDTO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceFrameDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizVideoReproduceFrameMapper;
import cn.iocoder.yudao.module.business.dal.mysql.mapper.BizVideoReproduceTaskMapper;
import cn.iocoder.yudao.module.business.service.BizVideoReproduceService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 视频复刻 API 实现类
 */
@RestController // 依照项目规范，API 实现类使用 @RestController
@Validated
public class BizVideoReproduceApiImpl implements BizVideoReproduceApi {

    @Resource
    private BizVideoReproduceTaskMapper videoReproduceTaskMapper;

    @Resource
    private BizVideoReproduceFrameMapper videoReproduceFrameMapper;

    @Resource
    private BizVideoReproduceService videoReproduceService;

    @Override
    public Long createVideoReproduceTask(BizVideoReproduceTaskDTO createDTO) {
        BizVideoReproduceTaskDO taskDO = BeanUtil.toBean(createDTO, BizVideoReproduceTaskDO.class);
        videoReproduceTaskMapper.insert(taskDO);
        return taskDO.getId();
    }

    @Override
    public void updateVideoReproduceTask(BizVideoReproduceTaskDTO updateDTO) {
        BizVideoReproduceTaskDO taskDO = BeanUtil.toBean(updateDTO, BizVideoReproduceTaskDO.class);
        videoReproduceTaskMapper.updateById(taskDO);
    }

    @Override
    public BizVideoReproduceTaskDTO getVideoReproduceTask(Long id) {
        BizVideoReproduceTaskDO taskDO = videoReproduceTaskMapper.selectById(id);
        return BeanUtil.toBean(taskDO, BizVideoReproduceTaskDTO.class);
    }

    @Override
    public Long createVideoReproduceFrame(BizVideoReproduceFrameDTO createDTO) {
        BizVideoReproduceFrameDO frameDO = BeanUtil.toBean(createDTO, BizVideoReproduceFrameDO.class);
        videoReproduceFrameMapper.insert(frameDO);
        return frameDO.getId();
    }

    @Override
    public void updateVideoReproduceFrame(BizVideoReproduceFrameDTO updateDTO) {
        BizVideoReproduceFrameDO frameDO = BeanUtil.toBean(updateDTO, BizVideoReproduceFrameDO.class);
        videoReproduceFrameMapper.updateById(frameDO);
    }

    @Override
    public BizVideoReproduceFrameDTO getVideoReproduceFrame(Long id) {
        BizVideoReproduceFrameDO frameDO = videoReproduceFrameMapper.selectById(id);
        return BeanUtil.toBean(frameDO, BizVideoReproduceFrameDTO.class);
    }

    @Override
    public List<BizVideoReproduceFrameDTO> getVideoReproduceFrameListByTaskId(Long taskId) {
        List<BizVideoReproduceFrameDO> list = videoReproduceFrameMapper.selectListByTaskId(taskId);
        return BeanUtil.copyToList(list, BizVideoReproduceFrameDTO.class);
    }

    @Override
    public PageResult<BizVideoReproduceTaskDTO> getVideoReproduceTaskPage(Long userId, Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);

        PageResult<BizVideoReproduceTaskDO> pageResult = videoReproduceTaskMapper.selectPage(pageParam,
                new LambdaQueryWrapperX<BizVideoReproduceTaskDO>()
                        .eq(BizVideoReproduceTaskDO::getCreator, userId)
                        .orderByDesc(BizVideoReproduceTaskDO::getId));

        return cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean(pageResult, BizVideoReproduceTaskDTO.class);
    }

    @Resource
    private cn.iocoder.yudao.module.business.service.BizLocalTaskService bizLocalTaskService;

    @Override
    public void startLocalAnalyzeWorkflow(Long taskId) {
        videoReproduceService.startLocalAnalyzeWorkflow(taskId);
    }

    @Override
    public void enqueueWashImageLocalTask(Long refFrameId, String execParams) {
        bizLocalTaskService.enqueueTask("WASH_IMAGE", null, refFrameId, execParams);
    }

    @Override
    public void enqueueGenVideoLocalTask(Long refTaskId, Long refFrameId, String execParams) {
        bizLocalTaskService.enqueueTask("GEN_VIDEO", refTaskId, refFrameId, execParams);
    }
}
