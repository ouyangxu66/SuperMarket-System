package com.supermarket.product.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import java.math.BigDecimal;
import java.util.Date;

public class ProductImportExcelVO {

    @ExcelProperty("商品名称(*必填)")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty("条形码(*必填,建议数字)")
    @ColumnWidth(20)
    private String barcode;

    @ExcelProperty("分类ID(*必填)")
    @ColumnWidth(15)
    private Long categoryId;

    @ExcelProperty("规格")
    @ColumnWidth(15)
    private String spec;

    @ExcelProperty("单位(*必填,如:个、瓶)")
    @ColumnWidth(15)
    private String unit;

    @ExcelProperty("售价(*必填)")
    @ColumnWidth(15)
    private BigDecimal price;

    @ExcelProperty("进价(*必填)")
    @ColumnWidth(15)
    private BigDecimal costPrice;

    @ExcelProperty("初始库存(*必填)")
    @ColumnWidth(15)
    private Integer stock;

    @ExcelProperty("预警库存(*必填)")
    @ColumnWidth(15)
    private Integer lowStockThreshold;

    @ExcelProperty("状态(*必填,1:上架 0:下架)")
    @ColumnWidth(20)
    private Integer status;

    @ExcelProperty("最新生产日期(yyyy-MM-dd)")
    @ColumnWidth(25)
    private Date latestProductionDate;

    @ExcelProperty("保质期(天)")
    @ColumnWidth(15)
    private Integer shelfLifeDays;

    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

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

    public Date getLatestProductionDate() { return latestProductionDate; }
    public void setLatestProductionDate(Date latestProductionDate) { this.latestProductionDate = latestProductionDate; }

    public Integer getShelfLifeDays() { return shelfLifeDays; }
    public void setShelfLifeDays(Integer shelfLifeDays) { this.shelfLifeDays = shelfLifeDays; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
