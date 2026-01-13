package com.supermarket.auth.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息视图对象 (VO)
 * 作用：专门用于 /auth/info 接口返回前端所需的数据
 * 此时不包含密码等敏感信息
 */
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;            // 用户ID
    private String username;    // 账号
    private String nickname;    // 昵称
    private String avatar;      // 头像URL
    private List<String> roles; // 角色列表 (如: ["ROLE_ADMIN"])
    private List<String> perms; // 权限列表 (如: ["user:add", "user:edit"])

    // --- 手动生成 Getter / Setter (无 Lombok) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<String> getPerms() { return perms; }
    public void setPerms(List<String> perms) { this.perms = perms; }
}