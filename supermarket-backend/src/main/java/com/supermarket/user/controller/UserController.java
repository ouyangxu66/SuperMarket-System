package com.supermarket.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.result.Result;
import com.supermarket.user.entity.User;
import com.supermarket.user.service.UserService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 用户控制器
 * 提供用户的增删改查接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // --- 构造器注入 ---
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表
     * GET /user/page?pageNum=1&pageSize=10&username=zhang
     */
    @GetMapping("/page")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {

        // 1. 构建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 如果 username 不为空，则进行模糊查询 (like)
        wrapper.like(StringUtils.hasText(username), User::getUsername, username);
        // 按创建时间倒序排列
        wrapper.orderByDesc(User::getCreateTime);

        // 3. 执行查询
        Page<User> result = userService.page(page, wrapper);

        // 4. 脱敏处理：不返回密码字段给前端
        result.getRecords().forEach(u -> u.setPassword(null));

        return Result.success(result);
    }

    /**
     * 新增用户
     * POST /user
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody User user) {
        // 调用 Service 层包含加密逻辑的保存方法
        boolean flag = userService.saveUser(user);
        return flag ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改用户
     * PUT /user
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        // 这里不修改密码，密码修改应该走单独的接口
        user.setPassword(null);
        boolean flag = userService.updateById(user);
        return flag ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除用户 (逻辑删除)
     * DELETE /user/1
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean flag = userService.removeById(id);
        return flag ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量删除
     * DELETE /user/batch
     * Body: [1, 2, 3]
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean flag = userService.removeByIds(ids);
        return flag ? Result.success() : Result.error("删除失败");
    }

    /**
     * 根据ID获���用户详情
     * GET /user/1
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 脱敏
        }
        return Result.success(user);
    }
}