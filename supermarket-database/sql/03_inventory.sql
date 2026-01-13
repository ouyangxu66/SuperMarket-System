/*
 * 数据库脚本：供应链与库存模块
 * 包含表：供应商表、入库单主表、入库单明细表
  * 说明：
        供应商管理：必须知道货是从哪里进的（如：可口可乐一级代理商），方便结算和售后。
        进货单据：进货不是简单的“库存+10”，而是一个严谨的财务过程。我们需要记录“谁、在什么时间、以什么价格、买了多少什么商品、总金额是多少”。
        库存关联：进货单审核通过后，会自动增加商品表中的库存 (stock)。
    表结构说明：
        supplier：供应商基础信息表。
        purchase_bill：进货单主表 (记录供应商、总金额、状态)。
        purchase_bill_item：进货单明细表 (记录具体的商品、进价、数量)。

 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 供应商表 (supplier)
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `name` varchar(128) NOT NULL COMMENT '供应商名称',
                            `contact_person` varchar(64) DEFAULT NULL COMMENT '联系人',
                            `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                            `address` varchar(255) DEFAULT NULL COMMENT '地址',
                            `status` tinyint(1) DEFAULT 1 COMMENT '状态 (1:正常, 0:停用)',
                            `remark` varchar(500) DEFAULT NULL COMMENT '备注',

                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- ----------------------------
-- 2. 入库单主表 (stock_in_order)
-- 一次进货操作生成一条记录
-- ----------------------------
DROP TABLE IF EXISTS `stock_in_order`;
CREATE TABLE `stock_in_order` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `order_no` varchar(32) NOT NULL COMMENT '入库单号 (唯一, 如: IN20231010001)',
                                  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
                                  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作员ID (关联 sys_user)',
                                  `total_amount` decimal(10,2) DEFAULT 0.00 COMMENT '进货总金额',
                                  `total_quantity` int(11) DEFAULT 0 COMMENT '进货总数量',

                                  `status` tinyint(1) DEFAULT 1 COMMENT '状态 (1:已入库, 2:已撤销)',
                                  `remark` varchar(500) DEFAULT NULL COMMENT '备注',

                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  `is_deleted` tinyint(1) DEFAULT 0,
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE COMMENT '单号唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单主表';

-- ----------------------------
-- 3. 入库单明细表 (stock_in_detail)
-- 记录一次进货中包含的具体商品
-- ----------------------------
DROP TABLE IF EXISTS `stock_in_detail`;
CREATE TABLE `stock_in_detail` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `order_id` bigint(20) NOT NULL COMMENT '关联入库单主表ID',
                                   `product_id` bigint(20) NOT NULL COMMENT '商品ID',
                                   `quantity` int(11) NOT NULL COMMENT '进货数量',
                                   `cost_price` decimal(10,2) NOT NULL COMMENT '单品进价 (可能与商品表中的参考进价不同)',
                                   `total_price` decimal(10,2) NOT NULL COMMENT '小计金额 (quantity * cost_price)',

                                   PRIMARY KEY (`id`),
                                   KEY `idx_order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

SET FOREIGN_KEY_CHECKS = 1;