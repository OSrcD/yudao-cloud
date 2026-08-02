package cn.iocoder.yudao.module.business.controller.app;

import cn.hutool.core.io.IoUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialPageReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizMaterialSaveReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskCreateReqVO;
import cn.iocoder.yudao.module.business.controller.admin.vo.BizVideoReproduceTaskPageReqVO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizMaterialDO;
import cn.iocoder.yudao.module.business.dal.dataobject.BizVideoReproduceTaskDO;
import cn.iocoder.yudao.module.business.service.BizMaterialService;
import cn.iocoder.yudao.module.business.service.BizVideoReproduceService;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 素材与裂变复刻")
@RestController
@RequestMapping("/business/app")
@Validated
public class BizMaterialAppController {

    @Resource
    private BizMaterialService materialService;
    @Resource
    private BizVideoReproduceService videoReproduceService;
    @Resource
    private FileApi fileApi;

    @PostMapping("/material/create")
    @Operation(summary = "创建我的素材")
    public CommonResult<Long> createMaterial(@Valid @RequestBody BizMaterialSaveReqVO createReqVO) {
        if (createReqVO.getUserId() == null) {
            createReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        }
        if (createReqVO.getUserType() == null && SecurityFrameworkUtils.getLoginUser() != null) {
            createReqVO.setUserType(SecurityFrameworkUtils.getLoginUser().getUserType());
        }
        return success(materialService.createMaterial(createReqVO));
    }

    @PostMapping("/material/upload")
    @Operation(summary = "上传图片并创建素材")
    public CommonResult<Long> uploadAndCreateMaterial(@RequestPart("file") MultipartFile file,
                                                      @RequestParam(value = "materialName", required = false) String materialName) throws Exception {
        String url = fileApi.createFile(IoUtil.readBytes(file.getInputStream()), file.getOriginalFilename());
        BizMaterialSaveReqVO createReqVO = new BizMaterialSaveReqVO();
        createReqVO.setMaterialName(materialName != null ? materialName : file.getOriginalFilename());
        createReqVO.setMaterialUrl(url);
        createReqVO.setFileType("0");
        createReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        if (SecurityFrameworkUtils.getLoginUser() != null) {
            createReqVO.setUserType(SecurityFrameworkUtils.getLoginUser().getUserType());
        }
        return success(materialService.createMaterial(createReqVO));
    }

    @DeleteMapping("/material/delete")
    @Operation(summary = "删除我的素材")
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        // TODO 可以增加只能删除自己素材的校验，目前简化实现
        BizMaterialDO material = materialService.getMaterial(id);
        if (material != null && SecurityFrameworkUtils.getLoginUserId().equals(material.getUserId())) {
            materialService.deleteMaterial(id);
        }
        return success(true);
    }

    @GetMapping("/material/page")
    @Operation(summary = "我的素材分页")
    public CommonResult<PageResult<BizMaterialDO>> getMyMaterialPage(@Valid BizMaterialPageReqVO pageReqVO) {
        return success(materialService.getMaterialPage(pageReqVO));
    }

    @GetMapping("/material/my-page")
    @Operation(summary = "我的素材分页（自动按当前用户过滤）")
    public CommonResult<PageResult<BizMaterialDO>> getCurrentUserMaterialPage(@Valid BizMaterialPageReqVO pageReqVO) {
        pageReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        if (SecurityFrameworkUtils.getLoginUser() != null) {
            pageReqVO.setUserType(SecurityFrameworkUtils.getLoginUser().getUserType());
        }
        return success(materialService.getMaterialPage(pageReqVO));
    }

    @PostMapping("/reproduce/create")
    @Operation(summary = "创建裂变复刻任务")
    public CommonResult<BizVideoReproduceTaskDO> createReproduceTask(@Valid @RequestBody BizVideoReproduceTaskCreateReqVO createReqVO) {
        if (createReqVO.getUserId() == null) {
            createReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        }
        if (createReqVO.getUserType() == null && SecurityFrameworkUtils.getLoginUser() != null) {
            createReqVO.setUserType(SecurityFrameworkUtils.getLoginUser().getUserType());
        }
        return success(videoReproduceService.createAndStartTask(createReqVO));
    }

    @GetMapping("/reproduce/page")
    @Operation(summary = "我的裂变复刻任务分页")
    public CommonResult<PageResult<BizVideoReproduceTaskDO>> getMyReproduceTaskPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        BizVideoReproduceTaskPageReqVO pageReqVO = new BizVideoReproduceTaskPageReqVO();
        pageReqVO.setPageNo(pageNo);
        pageReqVO.setPageSize(pageSize);
        pageReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        if (SecurityFrameworkUtils.getLoginUser() != null) {
            pageReqVO.setUserType(SecurityFrameworkUtils.getLoginUser().getUserType());
        }
        return success(videoReproduceService.getTaskPage(pageReqVO));
    }
}
