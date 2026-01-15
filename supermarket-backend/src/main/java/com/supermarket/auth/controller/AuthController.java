package com.supermarket.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.auth.dto.LoginDto;
import com.supermarket.auth.dto.UpdatePasswordDto;
import com.supermarket.auth.dto.UserInfoVO;
import com.supermarket.common.result.Result;
import com.supermarket.common.utils.JwtUtils;
import com.supermarket.user.entity.User;
import com.supermarket.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    // 2. 构造器注入 (Constructor Injection)
    // Spring 容器在初始化 Bean 时，会自动寻找构造器并注入依赖
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * 获取当前登录用户信息
     * GET /auth/info
     * 前置条件：请求头必须携带 Authorization: Bearer {token}
     */
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        // 1. 从 SecurityContext 中获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Result.error("获取用户信息失败");
        }

        // 获取用户名 (Principal 通常是用户名或 UserDetails 对象，getName() 方法最稳妥)
        String username = authentication.getName();

        // 2. 根据用户名查询数据库获取详细信息
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 组装 VO 对象
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        // 实际项目中头像通常存储在 OSS，这里先给个默认图
        userInfo.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");

        // TODO: 暂时硬编码角色，后续任务在 RoleService 中实现查询
        userInfo.setRoles(Collections.singletonList("ROLE_ADMIN"));
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

