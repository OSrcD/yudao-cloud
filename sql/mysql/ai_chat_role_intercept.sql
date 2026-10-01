-- ----------------------------------------------------
-- AI 截流评论角色与终端划分 SQL（复用现有 ai_chat_role 表）
-- ----------------------------------------------------

-- 1. 清理历史旧表与废弃菜单（如之前曾执行过 comment_template 脚本）
DROP TABLE IF EXISTS `ai_comment_template`;
DELETE FROM `system_menu` WHERE `id` BETWEEN 6130 AND 6134;
DELETE FROM `system_role_menu` WHERE `menu_id` BETWEEN 6130 AND 6134;

-- 2. 为 ai_chat_role 与 ai_model 增加 client_type 字段（适用终端：APP 移动端 / PC 网页端 / ALL 全部通用）
ALTER TABLE `ai_chat_role` 
  ADD COLUMN `client_type` VARCHAR(32) NOT NULL DEFAULT 'PC' COMMENT '适用终端：ALL 全部 / APP 移动端 / PC 网页端';

ALTER TABLE `ai_model` 
  ADD COLUMN `client_type` VARCHAR(32) NOT NULL DEFAULT 'ALL' COMMENT '适用终端：ALL 全部 / APP 移动端 / PC 网页端';

-- 3. 清理历史废弃预设角色并确保官方预设角色（仅保留“价值评论”与“deepseek-v4-pro-价值评论”）
DELETE FROM `ai_chat_role` WHERE `name` IN ('小红书共情截流手', '专业解答种草官', '幽默神评老司机');

-- 确保官方预设角色状态与终端类型正常（id=33 价值评论，id=39 deepseek-v4-pro-价值评论）
UPDATE `ai_chat_role` SET `client_type` = 'APP', `category` = 'APP截流', `public_status` = 1, `status` = 0, `deleted` = 0, `sort` = 1 WHERE `id` = 33;
UPDATE `ai_chat_role` SET `client_type` = 'APP', `category` = 'APP截流', `public_status` = 1, `status` = 0, `deleted` = 0, `sort` = 2 WHERE `id` = 34;

-- 4. 确保历史截流角色 client_type 与 tenant_id 正确
UPDATE `ai_chat_role` SET `client_type` = 'APP' WHERE `category` = 'APP截流';
UPDATE `ai_chat_role` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

-- 5. 添加自定义 API Key 字段（支持每个角色独立绑定大模型直连密钥）
ALTER TABLE `ai_chat_role` ADD COLUMN `custom_api_key` VARCHAR(512) DEFAULT NULL COMMENT '自定义 API Key (直连专属大模型密钥)';

-- 6. 添加官方模板衍生与业务信息字段
ALTER TABLE `ai_chat_role` 
  ADD COLUMN `template_role_id` BIGINT DEFAULT NULL COMMENT '引用的官方模板角色编号',
  ADD COLUMN `business_info` TEXT DEFAULT NULL COMMENT '业务信息 JSON（行业、身份、主营业务等）',
  ADD COLUMN `user_message` TEXT DEFAULT NULL COMMENT '自定义用户提示词模版 (User Message)';

-- 7. 为 ai_model 增加 is_default 字段并配置默认 APP 模型
ALTER TABLE `ai_model` ADD COLUMN `is_default` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否默认模型';
UPDATE `ai_model` SET `is_default` = 1, `sort` = 1 WHERE `id` = 70;
UPDATE `ai_model` SET `is_default` = 0, `sort` = 2 WHERE `id` = 71;


