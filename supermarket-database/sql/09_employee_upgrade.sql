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
