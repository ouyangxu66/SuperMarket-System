package com.supermarket.user.controller;

import com.supermarket.common.result.Result;
import com.supermarket.user.entity.Role;
import com.supermarket.user.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有角色列表
     * GET /role/list
     */
    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleService.list());
    }
}

