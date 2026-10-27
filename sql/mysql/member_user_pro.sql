-- 会员用户表增加是否专业版字段
ALTER TABLE `member_user`
    ADD COLUMN `is_pro` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否专业版用户（开启后可使用AI私信等专业版专属功能）';
