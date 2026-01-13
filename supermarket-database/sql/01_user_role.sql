
/*
 * 数据库脚本：用户与权限模块 (RBAC模型)
 * 包含表：用户表、角色表、菜单权限表、用户角色关联表、角色菜单关联表
 *
 * 核心逻辑：
 * 1. 用户 (sys_user) 拥有多个 角色 (sys_role)
 * 2. 角色 (sys_role) 拥有多个 菜单/权限 (sys_menu)
 * 3. 最终通过用户拥有的角色，计算出用户拥有的权限
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
-- 1. 用户表 (sys_user)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` varchar(64) NOT NULL COMMENT '用户名 (登录账号)',
                            `password` varchar(100) NOT NULL COMMENT '密码 (加密存储, BCrypt格式)',
                            `nickname` varchar(64) DEFAULT NULL COMMENT '用户昵称 (显示名)',
                            `phone` varchar(11) DEFAULT NULL COMMENT '手机号码',
                            `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
                            `gender` tinyint(1) DEFAULT 1 COMMENT '性别 (1:男, 2:女, 0:未知)',
                            `status` tinyint(1) DEFAULT 1 COMMENT '帐号状态 (1:正常, 0:停用)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除 (0:未删除, 1:已删除)',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`) USING BTREE COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

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
-- 4. 用户和角色关联表 (sys_user_role)
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

INSERT INTO sys_user (username, password, nickname, status, is_deleted)
VALUES ('admin', '复制进去的加密密文', '超级管理员', 1, 0);