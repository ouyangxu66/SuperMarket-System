package com.supermarket.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.auth.dto.LoginDto;
import com.supermarket.auth.dto.RegisterDto;
import com.supermarket.auth.dto.UpdatePasswordDto;
import com.supermarket.auth.dto.UserInfoVO;
import com.supermarket.common.result.Result;
import com.supermarket.common.utils.JwtUtils;
import com.supermarket.user.entity.User;
import com.supermarket.user.entity.Role;
import com.supermarket.user.mapper.UserMapper;
import com.supermarket.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证控制器
 * 提供登录、获取用户信息、登出功能
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // 1. 定义 final 字段，确保不可变性
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    // 2. 构造器注入 (Constructor Injection)
    // Spring 容器在初始化 Bean 时，会自动寻找构造器并注入依赖
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    /**
     * 登录接口
     * POST /auth/login
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        // 创建未认证的 Token 对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 调用 AuthenticationManager 进行认证 (会自动调用 UserDetailsServiceImpl)
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 认证通过，将认证信息存入 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成 JWT Token
        String jwt = jwtUtils.generateToken(loginDto.getUsername());

        // 返回 Token
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return Result.success("登录成功", map);
    }
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDto registerDto) {
        if (registerDto.getUsername() == null || registerDto.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (registerDto.getPassword() == null || registerDto.getPassword().length() < 6) {
            return Result.error("密码长度不能少于6位");
        }

        User existUser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerDto.getUsername()));
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(registerDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        newUser.setNickname(registerDto.getUsername());
        newUser.setStatus(1);

        userService.save(newUser);

        return Result.success("注册成功");
    }
    /**
     * 获取当前登录用户信息
     * GET /auth/info
     * 前置条件：请求头必须携带 Authorization: Bearer {token}
     */
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Result.error("获取用户信息失败");
        }

        String username = authentication.getName();

        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 查询用户角色（从数据库）
        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        List<String> roleKeys = roles.stream()
                .map(Role::getRoleKey)
                .collect(Collectors.toList());

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        userInfo.setRoles(roleKeys);
        userInfo.setPerms(Collections.emptyList());

        return Result.success(userInfo);
    }


    /**
     * 退出登录
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        // JWT 是无状态的，后端无法强制让 Token 失效 (除非用 Redis 黑名单)
        // 这里直接返回成功，由前端清除本地存储的 Token
        return Result.success("退出成功");
    }

    /**
     * 修改密码
     * POST /auth/password
     */
    @PostMapping("/password")
    public Result<String> updatePassword(@RequestBody UpdatePasswordDto dto) {
        // 1. 获取当前登录用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 2. 查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return Result.error("旧密码错误");
        }

        // 4. 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.updateById(user);

        return Result.success("密码修改成功");
    }
}

