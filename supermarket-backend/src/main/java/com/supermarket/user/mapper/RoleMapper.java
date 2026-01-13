package com.supermarket.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 Mapper 接口
 * 继承 BaseMapper 后，自动拥有了 insert, update, delete, selectById 等方法
 * 无需编写 XML 即可完成基础操作
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}