-- ----------------------------------------------------
-- Command Code 平台与 JEV 决策大模型集成更新脚本
-- ----------------------------------------------------

-- 1. 添加 Command Code AI 平台字典数据（所属平台下拉框支持 Command Code）
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `updater`, `deleted`)
SELECT 19, 'Command Code', 'CommandCode', 'ai_platform', 0, 'primary', '', 'Command Code 官方平台及 JEV 决策模型', '1', '1', 0
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'ai_platform' AND `value` = 'CommandCode'
);

-- 2. 检查更新状态
SELECT * FROM `system_dict_data` WHERE `dict_type` = 'ai_platform' AND `value` = 'CommandCode';
