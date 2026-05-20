package cn.iocoder.yudao.module.business.api.video.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
public class BizVideoReproduceTaskDTO {
    private Long id;
    private String originalVideoUrl;
    private Map<String, Object> productConfigJson;
    private List<String> charImages;
    private List<String> productImages;
    private String resultJson;
    private Map<String, Object> globalLocks;
    private String status;
    private String errorMsg;
    private Long conversationId;
    private Long payOrderId;
    private Boolean payStatus;
    private LocalDateTime payTime;
    private String combinedVideoUrl;
    private String remark;
    private String creator;
    private LocalDateTime createTime;
    private String execMode;
}

