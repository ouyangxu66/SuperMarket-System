package com.supermarket.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存盘点实体类
 * 用于记录库存盘点活动的信息
 */
@TableName("inventory_count")
public class InventoryCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String countNumber;       // 盘点编号
    private String title;             // 盘点标题
    private String description;       // 盘点说明
    private Integer totalCount;       // 盘点商品种类总数
    private Integer discrepancyCount; // 盘点差异商品种类数
    private String status;            // 状态 (DRAFT:草稿, IN_PROGRESS:进行中, COMPLETED:已完成, CANCELLED:已取消)
    private Date startTime;           // 开始时间
    private Date endTime;             // 结束时间
    private Long operatorId;          // 操作员ID
    private String operatorName;      // 操作员姓名
    private String remark;            // 备注

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCountNumber() { return countNumber; }
    public void setCountNumber(String countNumber) { this.countNumber = countNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    
    public Integer getDiscrepancyCount() { return discrepancyCount; }
    public void setDiscrepancyCount(Integer discrepancyCount) { this.discrepancyCount = discrepancyCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}