package com.supermarket.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.user.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.supermarket.user.entity.Role;
import java.util.List;

/**
 * 用户表 Mapper 接口
 * 继承 BaseMapper 后，自动拥有了 insert, update, delete, selectById 等方法
 * 无需编写 XML 即可完成基础操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteUserRoleByUserId(Long userId);

    @Insert("INSERT INTO sys_user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Select("SELECT r.* FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(Long userId);
}