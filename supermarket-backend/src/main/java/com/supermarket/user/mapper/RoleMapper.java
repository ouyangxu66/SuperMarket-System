package com.supermarket.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.user.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色表 Mapper 接口
 * 继承 BaseMapper 后，自动拥有了 insert, update, delete, selectById 等方法
 * 无需编写 XML 即可完成基础操作
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    void deleteRoleMenuByRoleId(Long roleId);

    @Insert("INSERT INTO sys_role_menu(role_id, menu_id) VALUES(#{roleId}, #{menuId})")
    void insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);
}