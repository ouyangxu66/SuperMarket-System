/*
 * 数据库脚本：会员管理模块
 * 包含表：会员主表、会员等级表、余额流水表、积分流水表
 */

USE `supermarket_db`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 会员等级表 (member_level)
-- 用于预留会员等级、积分倍率、折扣权益等扩展能力
-- ----------------------------
DROP TABLE IF EXISTS `member_level`;
CREATE TABLE `member_level` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `level_code` varchar(32) NOT NULL COMMENT '等级编码',
    `level_name` varchar(50) NOT NULL COMMENT '等级名称',
    `growth_threshold` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '升级门槛（累计消费金额/成长值，首期仅预留）',
    `point_rate` decimal(6,2) NOT NULL DEFAULT 1.00 COMMENT '积分倍率，1.00 表示正常 1 倍积分',
    `discount_rate` decimal(6,2) NOT NULL DEFAULT 1.00 COMMENT '折扣倍率，1.00 表示无折扣',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态（1:启用, 0:停用）',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level_code` (`level_code`) USING BTREE,
    KEY `idx_level_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- ----------------------------
-- 2. 会员主表 (member)
-- 保存会员基础资料、余额/积分当前快照及消费统计字段
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `member_no` varchar(32) NOT NULL COMMENT '会员编号（系统内部唯一编号）',
    `card_no` varchar(32) NOT NULL COMMENT '会员卡号（实体卡/虚拟卡号）',
    `name` varchar(50) NOT NULL COMMENT '会员姓名',
    `nickname` varchar(50) DEFAULT NULL COMMENT '会员昵称',
    `phone` varchar(20) NOT NULL COMMENT '手机号',
    `gender` tinyint(1) DEFAULT 2 COMMENT '性别（1:男, 0:女, 2:未知）',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态（1:正常, 0:停用）',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `balance` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '当前储值余额',
    `points` int(11) NOT NULL DEFAULT 0 COMMENT '当前消费积分',
    `total_recharge_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计充值金额',
    `total_consume_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    `total_consume_count` int(11) NOT NULL DEFAULT 0 COMMENT '累计消费次数',
    `last_consume_time` datetime DEFAULT NULL COMMENT '最后消费时间',
    `last_recharge_time` datetime DEFAULT NULL COMMENT '最后充值时间',
    `register_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `register_channel` varchar(32) DEFAULT 'OFFLINE' COMMENT '注册渠道（OFFLINE/APP/MINI_PROGRAM 等）',
    `level_id` bigint(20) DEFAULT NULL COMMENT '会员等级ID（关联 member_level，首期可为空）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_member_no` (`member_no`) USING BTREE,
    UNIQUE KEY `uk_card_no` (`card_no`) USING BTREE,
    UNIQUE KEY `uk_phone` (`phone`) USING BTREE,
    KEY `idx_member_name` (`name`) USING BTREE,
    KEY `idx_member_status` (`status`) USING BTREE,
    KEY `idx_member_register_time` (`register_time`) USING BTREE,
    KEY `idx_member_level_id` (`level_id`) USING BTREE,
    CONSTRAINT `chk_member_balance_non_negative` CHECK (`balance` >= 0),
    CONSTRAINT `chk_member_points_non_negative` CHECK (`points` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员主表';

-- ----------------------------
-- 3. 会员余额流水表 (member_balance_flow)
-- 记录所有余额变动，如充值、消费扣款、退款返还、人工调整
-- ----------------------------
DROP TABLE IF EXISTS `member_balance_flow`;
CREATE TABLE `member_balance_flow` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `member_id` bigint(20) NOT NULL COMMENT '会员ID',
    `change_type` tinyint(1) NOT NULL COMMENT '变动方向（1:增加, -1:减少）',
    `biz_type` varchar(32) NOT NULL COMMENT '业务类型（RECHARGE/SALE_CONSUME/REFUND/MANUAL_ADJUST/OPEN_GIFT）',
    `biz_no` varchar(64) DEFAULT NULL COMMENT '业务单号（如销售单号、充值单号）',
    `before_balance` decimal(12,2) NOT NULL COMMENT '变动前余额',
    `change_amount` decimal(12,2) NOT NULL COMMENT '变动金额（正数）',
    `after_balance` decimal(12,2) NOT NULL COMMENT '变动后余额',
    `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID（关联 sys_user）',
    `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名快照',
    `source` varchar(64) NOT NULL COMMENT '变动来源说明',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_balance_member_id` (`member_id`) USING BTREE,
    KEY `idx_balance_biz_type_biz_no` (`biz_type`, `biz_no`) USING BTREE,
    KEY `idx_balance_create_time` (`create_time`) USING BTREE,
    CONSTRAINT `chk_balance_change_amount_positive` CHECK (`change_amount` >= 0),
    CONSTRAINT `chk_balance_after_non_negative` CHECK (`after_balance` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员余额流水表';

-- ----------------------------
-- 4. 会员积分流水表 (member_point_flow)
-- 记录所有积分变动，如消费赠分、积分抵扣、活动赠送、人工调整、过期扣减
-- ----------------------------
DROP TABLE IF EXISTS `member_point_flow`;
CREATE TABLE `member_point_flow` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `member_id` bigint(20) NOT NULL COMMENT '会员ID',
    `change_type` tinyint(1) NOT NULL COMMENT '变动方向（1:增加, -1:减少）',
    `biz_type` varchar(32) NOT NULL COMMENT '业务类型（CONSUME_EARN/POINT_DEDUCT/REFUND_ROLLBACK/MANUAL_ADJUST/ACTIVITY_GIFT/EXPIRE）',
    `biz_no` varchar(64) DEFAULT NULL COMMENT '业务单号（如销售单号、活动单号）',
    `before_points` int(11) NOT NULL COMMENT '变动前积分',
    `change_points` int(11) NOT NULL COMMENT '变动积分（正数）',
    `after_points` int(11) NOT NULL COMMENT '变动后积分',
    `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID（关联 sys_user）',
    `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名快照',
    `source` varchar(64) NOT NULL COMMENT '积分来源说明',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除（0:未删除, 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_point_member_id` (`member_id`) USING BTREE,
    KEY `idx_point_biz_type_biz_no` (`biz_type`, `biz_no`) USING BTREE,
    KEY `idx_point_create_time` (`create_time`) USING BTREE,
    CONSTRAINT `chk_point_change_points_positive` CHECK (`change_points` >= 0),
    CONSTRAINT `chk_point_after_non_negative` CHECK (`after_points` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分流水表';

-- ----------------------------
-- 5. 初始化默认会员等级数据
-- 首期提供基础等级，便于后续等级与统计功能扩展
-- ----------------------------
INSERT INTO `member_level` (`level_code`, `level_name`, `growth_threshold`, `point_rate`, `discount_rate`, `status`, `remark`)
VALUES
    ('NORMAL', '普通会员', 0.00, 1.00, 1.00, 1, '默认会员等级'),
    ('SILVER', '银卡会员', 1000.00, 1.20, 0.98, 1, '累计消费满 1000 升级'),
    ('GOLD', '金卡会员', 5000.00, 1.50, 0.95, 1, '累计消费满 5000 升级');

-- ----------------------------
-- 6. 销售联动改造建议（本文件先保留注释，正式接入时同步更新 04_sales.sql）
-- 建议为 sale_order 增加以下字段：
--   member_id bigint(20) DEFAULT NULL COMMENT '会员ID',
--   member_no varchar(32) DEFAULT NULL COMMENT '会员编号快照',
--   member_name varchar(50) DEFAULT NULL COMMENT '会员姓名快照',
--   member_phone varchar(20) DEFAULT NULL COMMENT '会员手机号快照',
--   member_level varchar(32) DEFAULT NULL COMMENT '会员等级快照',
--   point_earned int(11) DEFAULT 0 COMMENT '本单获得积分',
--   point_deducted int(11) DEFAULT 0 COMMENT '本单抵扣积分',
--   point_deduct_amount decimal(10,2) DEFAULT 0.00 COMMENT '积分抵扣金额'
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
