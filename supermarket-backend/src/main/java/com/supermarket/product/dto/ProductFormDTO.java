package com.supermarket.product.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品表单 DTO
 * 用于接收新增或修改商品时的参数
 */
public class ProductFormDTO implements Serializable {

    private Long id;                // 修改时必填
    private Long categoryId;        // 分类ID
    private String barcode;         // 条形码
    private String name;            // 商品名称
    private String spec;            // 规格
    private String unit;            // 单位
    private BigDecimal price;       // 售价
    private BigDecimal costPrice;   // 进价
    private Integer stock;          // 初始库存
    private Integer lowStockThreshold; // 库存预警值
    private Integer status;         // 状态 (1上架 0下架)
    private String remark;          // 备注

    // --- Getter / Setter (无 Lombok) ---

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
}