package com.supermarket.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.user.entity.User;
import com.supermarket.user.mapper.UserMapper;
import com.supermarket.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUser(User user) {
        validateUser(user, true);

        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean result = this.save(user);

        if (result && user.getRoleIds() != null) {
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        validateUser(user, false);

        boolean result = this.updateById(user);

        if (result && user.getRoleIds() != null) {
            userMapper.deleteUserRoleByUserId(user.getId());
            for (Long roleId : user.getRoleIds()) {
                userMapper.insertUserRole(user.getId(), roleId);
            }
        }
        return result;
    }

    private void validateUser(User user, boolean isCreate) {
        if (user == null) {
            throw new BusinessException("员工信息不能为空");
        }
        if (!isCreate && user.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BusinessException("登录账号不能为空");
        }
        if (!StringUtils.hasText(user.getRealName())) {
            throw new BusinessException("员工姓名不能为空");
        }
        if (!StringUtils.hasText(user.getEmployeeNo())) {
            throw new BusinessException("员工工号不能为空");
        }
        if (user.getRoleIds() == null || user.getRoleIds().isEmpty()) {
            throw new BusinessException("请至少选择一个角色");
        }
        if (StringUtils.hasText(user.getPhone()) && !PHONE_PATTERN.matcher(user.getPhone()).matches()) {
            throw new BusinessException("手机号格式不正确");
        }

        user.setUsername(user.getUsername().trim());
        user.setRealName(user.getRealName().trim());
        user.setEmployeeNo(user.getEmployeeNo().trim());
        if (StringUtils.hasText(user.getJobTitle())) {
            user.setJobTitle(user.getJobTitle().trim());
        }
        if (StringUtils.hasText(user.getRemark())) {
            user.setRemark(user.getRemark().trim());
        }
        user.setNickname(user.getRealName());

        checkUsernameUnique(user.getId(), user.getUsername());
        checkEmployeeNoUnique(user.getId(), user.getEmployeeNo());
    }

    private void checkUsernameUnique(Long currentId, String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username);
        if (currentId != null) {
            wrapper.ne(User::getId, currentId);
        }
        Long count = this.count(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("登录账号已存在");
        }
    }

    private void checkEmployeeNoUnique(Long currentId, String employeeNo) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getEmployeeNo, employeeNo);
        if (currentId != null) {
            wrapper.ne(User::getId, currentId);
        }
        Long count = this.count(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("员工工号已存在");
        }
    }

    @Override
    public <E extends IPage<User>> E page(E page, com.baomidou.mybatisplus.core.conditions.Wrapper<User> queryWrapper) {
        E result = super.page(page, queryWrapper);
        if (result.getRecords() != null) {
            result.getRecords().forEach(user -> {
                user.setRoles(userMapper.selectRolesByUserId(user.getId()));
                if (user.getRoles() != null) {
                    user.setRoleIds(user.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList()));
                }
            });
        }
        return result;
    }

    @Override
    public User getById(Serializable id) {
        User user = super.getById(id);
        if (user != null) {
            user.setRoles(userMapper.selectRolesByUserId(user.getId()));
            if (user.getRoles() != null) {
                user.setRoleIds(user.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList()));
            }
        }
        return user;
    }
}