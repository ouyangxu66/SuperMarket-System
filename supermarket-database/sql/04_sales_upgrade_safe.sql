/*
 * 数据库增量升级脚本：销售与收银模块（安全版）
 * 说明：
 * 1. 不删除原有表结构与历史数据
 * 2. 仅为 sale_order 补充会员联动与积分相关字段
 * 3. 适用于已有 sale_order / sale_detail 数据的环境
 * 4. 若某个字段或索引已存在，请手动跳过对应 SQL 语句后继续执行
 */

USE `supermarket_db`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 为 sale_order 补充会员与积分字段
-- ----------------------------
ALTER TABLE `sale_order` ADD COLUMN `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID' AFTER `cashier_id`;
ALTER TABLE `sale_order` ADD COLUMN `member_no` varchar(32) DEFAULT NULL COMMENT '会员编号快照' AFTER `member_id`;
ALTER TABLE `sale_order` ADD COLUMN `member_name` varchar(50) DEFAULT NULL COMMENT '会员姓名快照' AFTER `member_no`;
ALTER TABLE `sale_order` ADD COLUMN `member_phone` varchar(20) DEFAULT NULL COMMENT '会员手机号快照' AFTER `member_name`;
ALTER TABLE `sale_order` ADD COLUMN `point_earned` int(11) DEFAULT 0 COMMENT '本单获得积分' AFTER `member_phone`;
ALTER TABLE `sale_order` ADD COLUMN `point_deducted` int(11) DEFAULT 0 COMMENT '本单抵扣积分（预留）' AFTER `point_earned`;
ALTER TABLE `sale_order` ADD COLUMN `point_deduct_amount` decimal(10,2) DEFAULT 0.00 COMMENT '积分抵扣金额（预留）' AFTER `point_deducted`;

-- ----------------------------
-- 2. 为 sale_order 补充索引
-- 若索引已存在，请跳过本语句
-- ----------------------------
CREATE INDEX `idx_sale_member_id` ON `sale_order` (`member_id`);

SET FOREIGN_KEY_CHECKS = 1;
