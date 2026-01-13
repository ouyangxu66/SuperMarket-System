package com.supermarket.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.user.entity.User;
import com.supermarket.user.mapper.UserMapper;
import com.supermarket.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    // --- 构造器注入 (替代 @Autowired) ---
    // Spring 4.3+ 以后，如果类只有一个构造器，@Autowired 可以省略
    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean saveUser(User user) {
        // 1. 如果没设密码，设置默认密码 123456
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }

        // 2. 密码加密存储
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 调用 MyBatis-Plus 的 save 方法
        return this.save(user);
    }
}