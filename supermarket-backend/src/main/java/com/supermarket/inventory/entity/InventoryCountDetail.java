package com.supermarket.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存盘点明细实体类
 * 用于记录库存盘点活动中每个商品的具体盘点信息
 */
@TableName("inventory_count_detail")
public class InventoryCountDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long countId;             // 盘点ID
    private Long productId;           // 商品ID
    private String productName;       // 商品名称
    private String productBarcode;    // 商品条码
    private String productSpec;       // 商品规格
    private String productUnit;       // 商品单位
    
    private Integer systemStock;      // 系统库存数量
    private Integer actualStock;      // 实际盘点数量
    private Integer difference;       // 差异数量（实际 - 系统）
    private String discrepancyReason; // 差异原因
    private String status;            // 状态 (NORMAL:正常, DISCREPANCY:有差异)
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
    
    public Long getCountId() { return countId; }
    public void setCountId(Long countId) { this.countId = countId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductBarcode() { return productBarcode; }
    public void setProductBarcode(String productBarcode) { this.productBarcode = productBarcode; }
    
    public String getProductSpec() { return productSpec; }
    public void setProductSpec(String productSpec) { this.productSpec = productSpec; }
    
    public String getProductUnit() { return productUnit; }
    public void setProductUnit(String productUnit) { this.productUnit = productUnit; }
    
    public Integer getSystemStock() { return systemStock; }
    public void setSystemStock(Integer systemStock) { this.systemStock = systemStock; }
    
    public Integer getActualStock() { return actualStock; }
    public void setActualStock(Integer actualStock) { this.actualStock = actualStock; }
    
    public Integer getDifference() { return difference; }
    public void setDifference(Integer difference) { 
        this.difference = difference; 
        // 根据差异数量自动设置状态
        this.status = (difference != null && difference != 0) ? "DISCREPANCY" : "NORMAL";
    }
    
    public String getDiscrepancyReason() { return discrepancyReason; }
    public void setDiscrepancyReason(String discrepancyReason) { this.discrepancyReason = discrepancyReason; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}