-- VIP 会员：用户权益字段 + 套餐 + 订单 + 支付应用
-- 新用户试用 3 天；套餐用 duration_days 区分月付/年付

ALTER TABLE `member_user`
    ADD COLUMN `vip_expire_time` datetime NULL DEFAULT NULL COMMENT 'VIP 到期时间（试用与付费共用）' AFTER `group_id`,
    ADD COLUMN `vip_trial_used` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已发放过试用' AFTER `vip_expire_time`;

CREATE TABLE IF NOT EXISTS `member_vip_package` (
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`          varchar(64)  NOT NULL COMMENT '套餐名',
    `price`         int          NOT NULL COMMENT '价格，单位：分',
    `duration_days` int          NOT NULL COMMENT '时长天数',
    `status`        tinyint      NOT NULL DEFAULT 0 COMMENT '状态：0开启 1关闭',
    `sort`          int          NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
    `creator`       varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`     bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员 VIP 套餐';

CREATE TABLE IF NOT EXISTS `member_vip_order` (
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`          bigint       NOT NULL COMMENT '用户编号',
    `package_id`       bigint       NOT NULL COMMENT '套餐编号',
    `package_name`     varchar(64)  NOT NULL COMMENT '套餐名快照',
    `price`            int          NOT NULL COMMENT '支付金额，单位：分',
    `duration_days`    int          NOT NULL COMMENT '开通天数快照',
    `pay_status`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否已支付',
    `pay_order_id`     bigint       DEFAULT NULL COMMENT '支付订单编号',
    `pay_time`         datetime     DEFAULT NULL COMMENT '支付时间',
    `pay_channel_code` varchar(32)  DEFAULT NULL COMMENT '支付渠道',
    `creator`          varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`        bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pay_order_id` (`pay_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员 VIP 开通订单';

-- 预置月付 / 年付（价格可按业务再改）
INSERT INTO `member_vip_package` (`name`, `price`, `duration_days`, `status`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
    ('月会员', 15000, 30, 0, 1, '1', NOW(), '1', NOW(), b'0', 1);

-- 支付应用（回调到会员模块）
INSERT INTO `pay_app` (`name`, `app_key`, `status`, `remark`, `order_notify_url`, `refund_notify_url`, `transfer_notify_url`,
                       `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES ('会员VIP', 'vip', 0, '价值截流等 VIP 开通',
        'http://127.0.0.1:48080/app-api/member/vip-order/update-paid',
        '',
        '',
        '1', NOW(), '1', NOW(), b'0', 1);

-- VIP 应用支付渠道（app_id 按 SELECT id FROM pay_app WHERE app_key='vip' 自动关联）
INSERT INTO `pay_channel` (`app_id`, `code`, `status`, `fee_rate`, `remark`, `config`,
                           `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, 'mock', 0, 0, 'VIP 测试用模拟支付',
       '{"@class":"cn.iocoder.yudao.framework.pay.core.client.impl.NonePayClientConfig","name":"mock-conf"}',
       '1', NOW(), '1', NOW(), b'0', 1
FROM `pay_app` WHERE `app_key` = 'vip' LIMIT 1;

-- VIP 余额（钱包）支付：用充值余额开通/续费
INSERT INTO `pay_channel` (`app_id`, `code`, `status`, `fee_rate`, `remark`, `config`,
                           `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, 'wallet', 0, 0, 'VIP 钱包余额支付',
       '{"@class":"cn.iocoder.yudao.framework.pay.core.client.impl.NonePayClientConfig","name":"wallet-conf"}',
       '1', NOW(), '1', NOW(), b'0', 1
FROM `pay_app` WHERE `app_key` = 'vip' LIMIT 1;
