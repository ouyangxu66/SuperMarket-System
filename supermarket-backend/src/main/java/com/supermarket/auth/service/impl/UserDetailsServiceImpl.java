package com.supermarket.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.user.entity.User;
import com.supermarket.user.mapper.UserMapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 自定义用户加载逻辑
 * 作用：Spring Security 在登录时，会调用这个 loadUserByUsername 方法，
 *我们需要在这里查询数据库，验证用户是否存在。
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询数据库中的用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 2. 将我们的 User 对象转换为 Spring Security 需要的 UserDetails 对象
        // 这里暂时给空权限列表 (new ArrayList<>())，后续做权限控制时再补充
        // org.springframework.security.core.userdetails.User 是 Security 内置的实现类
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // enabled: 是否启用
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                new ArrayList<>() // authorities: 权限列表
        );
    }
}