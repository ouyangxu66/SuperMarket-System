# 数据字典

## 1. `sys_user` 员工账号表

> 说明：当前系统已将“用户管理”业务语义升级为“员工管理”。
> 为保持 Spring Security、JWT 与角色权限链路稳定，底层仍沿用 `sys_user` 表名。

| 字段名 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | bigint | 主键ID |
| `username` | varchar(64) | 登录账号 |
| `password` | varchar(100) | 登录密码（BCrypt） |
| `nickname` | varchar(64) | 昵称/显示名，兼容历史数据 |
| `real_name` | varchar(64) | 员工姓名 |
| `employee_no` | varchar(32) | 员工工号 |
| `job_title` | varchar(64) | 岗位 |
| `hire_date` | datetime | 入职时间 |
| `phone` | varchar(11) | 手机号 |
| `email` | varchar(128) | 邮箱 |
| `gender` | tinyint | 性别（1:男, 0:女, 2:未知） |
| `status` | tinyint | 账号状态（1:正常, 0:停用） |
| `remark` | varchar(500) | 备注 |
| `create_time` | datetime | 创建时间 |
| `update_time` | datetime | 更新时间 |
| `is_deleted` | tinyint | 逻辑删除标记 |

## 2. `sys_role` 角色表

用于定义员工角色，如管理员、店长、收银员、库管员、采购员。

## 3. `sys_menu` 菜单权限表

用于定义菜单、页面入口和按钮权限。员工管理模块仍沿用 `user:*` 权限码以兼容旧数据。

## 4. 关系表

- `sys_user_role`：员工与角色关联表
- `sys_role_menu`：角色与菜单权限关联表
