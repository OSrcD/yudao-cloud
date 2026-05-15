package cn.iocoder.yudao.module.business.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class PrompCommentSimBo {
    private List<CommentSimilarityBo> commentList;
}

