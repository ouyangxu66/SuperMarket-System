package com.supermarket.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.user.entity.Role;
import com.supermarket.user.mapper.RoleMapper;
import com.supermarket.user.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}

