package com.supermarket.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 员工账号实体类
 * 对应数据库表: sys_user
 * 说明：当前实体同时承载登录账号与员工基础档案信息
 */
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 昵称/显示名
     */
    private String nickname;

    /**
     * 员工姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 员工工号
     */
    @TableField("employee_no")
    private String employeeNo;

    /**
     * 岗位
     */
    @TableField("job_title")
    private String jobTitle;

    /**
     * 入职时间
     */
    @TableField("hire_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date hireDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 性别 (0:女, 1:男)
     */
    private Integer gender;

    /**
     * 状态 (0:禁用, 1:启用)
     */
    private Integer status;

    /**
     * 逻辑删除 (0:未删除, 1:已删除)
     * 映射数据库字段名 is_deleted
     */
    @TableField("is_deleted")
    private Integer deleted;

    /**
     * 创建时间
     * fill = FieldFill.INSERT: 仅在插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     * fill = FieldFill.INSERT_UPDATE: 在插入和更新时都自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 角色列表 (用于前端显示)
     * exist = false: 表示该字段不是数据库表字段
     */
    @TableField(exist = false)
    private List<Role> roles;

    /**
     * 角色ID列表 (用于接收前端参数)
     * exist = false: 表示该字段不是数据库表字段
     */
    @TableField(exist = false)
    private List<Long> roleIds;

    // --- 以下为手动生成的 Getter 和 Setter 方法 (无 Lombok) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", employeeNo='" + employeeNo + '\'' +
                ", realName='" + realName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", status=" + status +
                '}';
    }
}