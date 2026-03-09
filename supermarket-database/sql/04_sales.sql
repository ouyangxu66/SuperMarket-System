/*
 * 数据库脚本：销售与收银模块
 * 包含表：销售主单表、销售明细表
 */

USE `supermarket_db`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 销售主单表 (sale_order)
-- 记录一次收银行为
-- ----------------------------
DROP TABLE IF EXISTS `sale_order`;
CREATE TABLE `sale_order` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `order_no` varchar(32) NOT NULL COMMENT '销售单号 (唯一, 如: XS20231010001)',
                              `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额 (原价)',
                              `real_amount` decimal(10,2) NOT NULL COMMENT '实收金额 (优惠后)',
                              `payment_type` tinyint(1) DEFAULT 1 COMMENT '支付方式 (1:现金, 2:微信, 3:支付宝)',
                              `cashier_id` bigint(20) DEFAULT NULL COMMENT '收银员ID (关联 sys_user)',
                              `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
                              `member_no` varchar(32) DEFAULT NULL COMMENT '会员编号快照',
                              `member_name` varchar(50) DEFAULT NULL COMMENT '会员姓名快照',
                              `member_phone` varchar(20) DEFAULT NULL COMMENT '会员手机号快照',
                              `point_earned` int(11) DEFAULT 0 COMMENT '本单获得积分',
                              `point_deducted` int(11) DEFAULT 0 COMMENT '本单抵扣积分（预留）',
                              `point_deduct_amount` decimal(10,2) DEFAULT 0.00 COMMENT '积分抵扣金额（预留）',

                              `status` tinyint(1) DEFAULT 1 COMMENT '状态 (1:已支付, -1:已退款)',
                              `remark` varchar(255) DEFAULT NULL COMMENT '备注',

                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `is_deleted` tinyint(1) DEFAULT 0,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
                              KEY `idx_sale_member_id` (`member_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售主单表';

-- ----------------------------
-- 2. 销售明细表 (sale_detail)
-- ----------------------------
DROP TABLE IF EXISTS `sale_detail`;
CREATE TABLE `sale_detail` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `order_id` bigint(20) NOT NULL COMMENT '关联销售单ID',
                               `product_id` bigint(20) NOT NULL COMMENT '商品ID',
                               `product_name` varchar(128) NOT NULL COMMENT '商品名称快照',
                               `price` decimal(10,2) NOT NULL COMMENT '销售单价快照',
                               `quantity` int(11) NOT NULL COMMENT '购买数量',
                               `amount` decimal(10,2) NOT NULL COMMENT '小计金额',

                               PRIMARY KEY (`id`),
                               KEY `idx_order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售明细表';

SET FOREIGN_KEY_CHECKS = 1;