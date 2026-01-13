package com.supermarket.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.user.entity.User;

/**
 * 用户服务接口
 * 继承 IService 以获得 MyBatis-Plus 提供的通用 CRUD 能力
 */
public interface UserService extends IService<User> {

    /**
     * 新增用户 (包含密码加密逻辑)
     * @param user 用户对象
     * @return 是否成功
     */
    boolean saveUser(User user);
}