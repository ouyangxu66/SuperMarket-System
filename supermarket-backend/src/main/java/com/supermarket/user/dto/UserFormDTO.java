package com.supermarket.user.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 用户表单对象
 * 用于接收前端 "新增/编辑" 用户时提交的数据
 */
public class UserFormDTO implements Serializable {
    
    private Long id;            // 修改时必传
    private String username;    // 用户名
    private String nickname;    // 昵称
    private String phone;       // 手机号
    private Integer gender;     // 性别
    private Integer status;     // 状态
    private List<Long> roleIds; // 关联的角色ID列表

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<Long> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }
}