package com.supermarket.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 * 继承 BaseMapper 后，自动拥有了 insert, update, delete, selectById 等方法
 * 无需编写 XML 即可完成基础操作
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 如果后续有复杂的自定义 SQL，���以在这里添加方法并在 XML 中实现
}