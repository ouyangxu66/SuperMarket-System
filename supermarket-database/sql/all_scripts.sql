/*
 * 数据库脚本：员工与权限模块 (RBAC模型)
 * 包含表：员工账号表、角色表、菜单权限表、员工角色关联表、角色菜单关联表
 *
 * 核心逻辑：
 * 1. 员工账号 (sys_user) 拥有多个角色 (sys_role)
 * 2. 角色 (sys_role) 拥有多个菜单/权限 (sys_menu)
 * 3. 最终通过员工拥有的角色，计算出员工拥有的权限
 */
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS supermarket_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 使用该数据库
USE supermarket_db;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 员工账号表 (sys_user)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` varchar(64) NOT NULL COMMENT '登录账号',
                            `password` varchar(100) NOT NULL COMMENT '密码 (加密存储, BCrypt格式)',
                            `nickname` varchar(64) DEFAULT NULL COMMENT '昵称/显示名',
                            `real_name` varchar(64) DEFAULT NULL COMMENT '员工姓名',
                            `employee_no` varchar(32) DEFAULT NULL COMMENT '员工工号',
                            `job_title` varchar(64) DEFAULT NULL COMMENT '岗位',
                            `hire_date` datetime DEFAULT NULL COMMENT '入职时间',
                            `phone` varchar(11) DEFAULT NULL COMMENT '手机号码',
                            `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
                            `gender` tinyint(1) DEFAULT 1 COMMENT '性别 (1:男, 0:女, 2:未知)',
                            `status` tinyint(1) DEFAULT 1 COMMENT '帐号状态 (1:正常, 0:停用)',
                            `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 (0:未删除, 1:已删除)',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`) USING BTREE COMMENT '登录账号唯一索引',
                            UNIQUE KEY `uk_employee_no` (`employee_no`) USING BTREE COMMENT '员工工号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工账号表';

-- ----------------------------
-- 2. 角色表 (sys_role)
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `role_name` varchar(64) NOT NULL COMMENT '角色名称 (如: 店长, 收银员)',
                            `role_key` varchar(64) NOT NULL COMMENT '角色权限字符串 (如: ROLE_ADMIN, ROLE_CASHIER)',
                            `role_sort` int(4) DEFAULT 0 COMMENT '显示顺序',
                            `status` tinyint(1) DEFAULT 1 COMMENT '角色状态 (1:正常, 0:停用)',
                            `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 (0:未删除, 1:已删除)',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_role_key` (`role_key`) USING BTREE COMMENT '角色编码唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- 3. 菜单权限表 (sys_menu)
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `parent_id` bigint(20) DEFAULT 0 COMMENT '父菜单ID (0表示顶级菜单)',
                            `menu_name` varchar(64) NOT NULL COMMENT '菜单名称',
                            `path` varchar(200) DEFAULT NULL COMMENT '路由地址 (前端URL, 如: /product/list)',
                            `component` varchar(255) DEFAULT NULL COMMENT '组件路径 (Vue组件位置, 如: product/index)',
                            `perms` varchar(100) DEFAULT NULL COMMENT '权限标识 (后端校验用, 如: product:list, product:add)',
                            `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
                            `menu_type` char(1) DEFAULT '' COMMENT '类型 (M:目录, C:菜单, F:按钮)',
                            `order_num` int(4) DEFAULT 0 COMMENT '显示顺序',
                            `status` tinyint(1) DEFAULT 1 COMMENT '菜单状态 (1:正常, 0:停用)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 4. 员工和角色关联表 (sys_user_role)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
                                 `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                 `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ----------------------------
-- 5. 角色和菜单关联表 (sys_role_menu)
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                 `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
                                 PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO sys_user (username, password, nickname, real_name, employee_no, job_title, status, is_deleted)
VALUES ('admin', '复制进去的加密密文', '超级管理员', '系统管理员', 'EMP0001', '管理员', 1, 0);
/*
 * 数据库脚本：商品管理模块
 * 包含表：商品分类表、商品信息表
    * 设计说明：
    商品档案：超市里的每一件商品都需要一个"身份证"。我们需要记录它的条形码（扫码枪扫出来的那个）、名称、售价、进价、单位等。
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
                           `image_url` varchar(255) DEFAULT '' COMMENT '商品图片URL',

                           `price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格 (元)',
                           `cost_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '进货价格 (元, 用于计算毛利)',

                           `stock` int(11) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
                           `low_stock_threshold` int(11) DEFAULT 10 COMMENT '库存预警阈值 (低于此值报警)',

                           `status` tinyint(1) DEFAULT 1 COMMENT '状态 (1:上架, 0:下架)',
                           `remark` varchar(500) DEFAULT NULL COMMENT '备注',

                           -- 新增过期日期相关字段
                           `latest_production_date` date DEFAULT NULL COMMENT '最新生产日期',
                           `shelf_life_days` int(11) DEFAULT NULL COMMENT '保质期天数',
                           `earliest_expiration_date` date DEFAULT NULL COMMENT '最早到期日期',

                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_barcode` (`barcode`) USING BTREE COMMENT '条形码必须唯一',
                           KEY `idx_category` (`category_id`) USING BTREE COMMENT '分类索引',
                           KEY `idx_expiration_date` (`earliest_expiration_date`) USING BTREE COMMENT '到期日期索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

-- 为现有的product表添加过期日期相关字段
-- 适用于已有数据的数据库升级

USE supermarket_db;

-- 添加过期日期相关字段
ALTER TABLE `product` 
ADD COLUMN `latest_production_date` date DEFAULT NULL COMMENT '最新生产日期' AFTER `remark`,
ADD COLUMN `shelf_life_days` int(11) DEFAULT NULL COMMENT '保质期天数' AFTER `latest_production_date`,
ADD COLUMN `earliest_expiration_date` date DEFAULT NULL COMMENT '最早到期日期' AFTER `shelf_life_days`;

-- 为到期日期字段添加索引以提高查询性能
ALTER TABLE `product` ADD INDEX `idx_expiration_date` (`earliest_expiration_date`);

-- 可选：如果需要根据现有数据计算到期日期，可以执行以下更新语句
-- UPDATE `product` SET `earliest_expiration_date` = DATE_ADD(`latest_production_date`, INTERVAL `shelf_life_days` DAY) WHERE `latest_production_date` IS NOT NULL AND `shelf_life_days` IS NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;/*
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

-- ----------------------------
-- 4. 库存盘点表 (inventory_count)
-- ----------------------------
DROP TABLE IF EXISTS `inventory_count`;
CREATE TABLE `inventory_count` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `count_number` varchar(50) NOT NULL COMMENT '盘点编号',
    `title` varchar(100) DEFAULT NULL COMMENT '盘点标题',
    `description` varchar(500) DEFAULT NULL COMMENT '盘点说明',
    `total_count` int(11) DEFAULT 0 COMMENT '盘点商品种类总数',
    `discrepancy_count` int(11) DEFAULT 0 COMMENT '盘点差异商品种类数',
    `status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态 (DRAFT, IN_PROGRESS, COMPLETED, CANCELLED)',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    `operator_id` bigint(20) DEFAULT NULL COMMENT '操作员ID',
    `operator_name` varchar(50) DEFAULT NULL COMMENT '操作员姓名',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点表';

-- ----------------------------
-- 5. 库存盘点明细表 (inventory_count_detail)
-- ----------------------------
DROP TABLE IF EXISTS `inventory_count_detail`;
CREATE TABLE `inventory_count_detail` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `count_id` bigint(20) NOT NULL COMMENT '盘点ID',
    `product_id` bigint(20) NOT NULL COMMENT '商品ID',
    `product_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
    `product_barcode` varchar(50) DEFAULT NULL COMMENT '商品条码',
    `product_spec` varchar(50) DEFAULT NULL COMMENT '商品规格',
    `product_unit` varchar(20) DEFAULT NULL COMMENT '商品单位',
    `system_stock` int(11) DEFAULT 0 COMMENT '系统库存数量',
    `actual_stock` int(11) DEFAULT 0 COMMENT '实际盘点数量',
    `difference` int(11) DEFAULT 0 COMMENT '差异数量（实际 - 系统）',
    `discrepancy_reason` varchar(200) DEFAULT NULL COMMENT '差异原因',
    `status` varchar(20) DEFAULT 'NORMAL' COMMENT '状态 (NORMAL, DISCREPANCY)',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点明细表';

SET FOREIGN_KEY_CHECKS = 1;/*
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

SET FOREIGN_KEY_CHECKS = 1;/*
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
USE supermarket_db;

-- 将现有 sys_user 升级为员工账号语义，保持登录认证链路不变
-- 说明：部分 MySQL 版本不支持 ALTER TABLE ... ADD COLUMN IF NOT EXISTS，
-- 因此这里改为可在 MySQL 5.7/8.0 下执行的条件判断写法。

SET @db := DATABASE();

SET @sql := IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db
          AND TABLE_NAME = 'sys_user'
          AND COLUMN_NAME = 'real_name'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_user` ADD COLUMN `real_name` varchar(64) DEFAULT NULL COMMENT ''员工姓名'' AFTER `nickname`'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db
          AND TABLE_NAME = 'sys_user'
          AND COLUMN_NAME = 'employee_no'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_user` ADD COLUMN `employee_no` varchar(32) DEFAULT NULL COMMENT ''员工工号'' AFTER `real_name`'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db
          AND TABLE_NAME = 'sys_user'
          AND COLUMN_NAME = 'job_title'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_user` ADD COLUMN `job_title` varchar(64) DEFAULT NULL COMMENT ''岗位'' AFTER `employee_no`'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db
          AND TABLE_NAME = 'sys_user'
          AND COLUMN_NAME = 'hire_date'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_user` ADD COLUMN `hire_date` datetime DEFAULT NULL COMMENT ''入职时间'' AFTER `job_title`'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
    EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = @db
          AND TABLE_NAME = 'sys_user'
          AND COLUMN_NAME = 'remark'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_user` ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT ''备注'' AFTER `status`'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF (
    EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = @db
          AND TABLE_NAME = 'sys_user'
          AND INDEX_NAME = 'uk_employee_no'
    ),
    'SELECT 1',
    'ALTER TABLE `sys_user` ADD UNIQUE KEY `uk_employee_no` (`employee_no`)'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `sys_user`
SET `real_name` = COALESCE(`real_name`, `nickname`),
    `employee_no` = COALESCE(`employee_no`, CONCAT('EMP', LPAD(`id`, 4, '0'))),
    `job_title` = COALESCE(`job_title`, '未设置')
WHERE `is_deleted` = 0;

-- 将菜单文案升级为员工管理，保留 user:* 权限码不变
UPDATE `sys_menu` SET `menu_name` = '员工管理' WHERE `perms` = 'user:list';
UPDATE `sys_menu` SET `menu_name` = '员工新增' WHERE `perms` = 'user:add';
UPDATE `sys_menu` SET `menu_name` = '员工修改' WHERE `perms` = 'user:update';
UPDATE `sys_menu` SET `menu_name` = '员工删除' WHERE `perms` = 'user:delete';
-- 新增商品图片字段
ALTER TABLE `product`
ADD COLUMN `image_url` VARCHAR(255) DEFAULT '' COMMENT '商品图片URL' AFTER `unit`;
