/*
 * 数据库脚本：商品管理模块
 * 包含表：商品分类表、商品信息表
    * 设计说明：
    商品档案：超市里的每一件商品都需要一个“身份证”。我们需要记录它的条形码（扫码枪扫出来的那个）、名称、售价、进价、单位等。
    分类管理：超市商品成千上万，必须分类（如：饮料区、零食区、日用品区），否则管理起来一团糟。
    核心字段：
    barcode (条形码)：这是商品的唯一标识，扫码枪识别的就是它。
    stock (库存)：当前货架上还剩多少。
    status (上下架状态)：有些商品暂时不卖了，需要下架。
 */

USE supermarket_db;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 商品分类表 (product_category)
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                    `parent_id` bigint(20) DEFAULT 0 COMMENT '父分类ID (0为顶级分类)',
                                    `name` varchar(64) NOT NULL COMMENT '分类名称 (如: 饮料, 零食)',
                                    `sort` int(4) DEFAULT 0 COMMENT '显示顺序',
                                    `status` tinyint(1) DEFAULT 1 COMMENT '状态 (1:正常, 0:停用)',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 2. 商品信息表 (product)
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                           `category_id` bigint(20) NOT NULL COMMENT '所属分类ID',
                           `barcode` varchar(64) NOT NULL COMMENT '条形码 (扫码枪识别码, 唯一)',
                           `name` varchar(128) NOT NULL COMMENT '商品名称',
                           `spec` varchar(64) DEFAULT NULL COMMENT '规格 (如: 500ml, 1kg)',
                           `unit` varchar(32) DEFAULT '个' COMMENT '计量单位 (如: 瓶, 包, 箱)',

                           `price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格 (元)',
                           `cost_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '进货价格 (元, 用于计算毛利)',

                           `stock` int(11) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
                           `low_stock_threshold` int(11) DEFAULT 10 COMMENT '库存预警阈值 (低于此值报警)',

                           `status` tinyint(1) DEFAULT 1 COMMENT '状态 (1:上架, 0:下架)',
                           `remark` varchar(500) DEFAULT NULL COMMENT '备注',

                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_barcode` (`barcode`) USING BTREE COMMENT '条形码必须唯一',
                           KEY `idx_category` (`category_id`) USING BTREE COMMENT '分类索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

SET FOREIGN_KEY_CHECKS = 1;