package com.supermarket.product.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表单对象
 * 用于接收前端 "新增/编辑" 商品时提交的数据
 */
public class ProductFormDTO implements Serializable {
    
    private Long id;                    // 修改时必传
    private Long categoryId;            // 分类ID
    private String barcode;             // 条形码
    private String name;                // 商品名称
    private String spec;                // 规格
    private String unit;                // 计量单位
    private BigDecimal price;           // 销售价格
    private BigDecimal costPrice;       // 进货价格
    private Integer stock;              // 当前库存数量
    private Integer lowStockThreshold;  // 库存预警阈值
    private Integer status;             // 状态 (1:上架, 0:下架)
    private String remark;              // 备注
    
    // 新增过期日期相关字段
    private Date latestProductionDate;  // 最新生产日期
    private Integer shelfLifeDays;      // 保质期天数

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    // 新增字段的getter/setter
    public Date getLatestProductionDate() { return latestProductionDate; }
    public void setLatestProductionDate(Date latestProductionDate) { this.latestProductionDate = latestProductionDate; }
    
    public Integer getShelfLifeDays() { return shelfLifeDays; }
    public void setShelfLifeDays(Integer shelfLifeDays) { this.shelfLifeDays = shelfLifeDays; }
}