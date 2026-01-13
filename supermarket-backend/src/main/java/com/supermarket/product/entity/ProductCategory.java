package com.supermarket.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品分类实体类
 * 对应表: product_category
 */
@TableName("product_category")
public class ProductCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 父分类ID (0为顶级分类) */
    private Long parentId;

    /** 分类名称 */
    private String name;

    /** 排序 */
    private Integer sort;

    /** 状态 (1:正常, 0:停用) */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("is_deleted")
    @TableLogic // 开启逻辑删除功能
    private Integer deleted;

    // --- 非数据库字段 (用于构建树形结构返回给前端) ---
    @TableField(exist = false)
    private List<ProductCategory> children;

    // --- Getter / Setter (无 Lombok) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public List<ProductCategory> getChildren() { return children; }
    public void setChildren(List<ProductCategory> children) { this.children = children; }
}