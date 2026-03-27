package com.supermarket.product.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.io.Serializable;
import java.math.BigDecimal;

@ColumnWidth(20)
public class ProductExcelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty("商品名称")
    @ColumnWidth(30)
    private String name;

    @ExcelProperty("条码")
    private String barcode;

    @ExcelProperty("分类")
    private String categoryName;

    @ExcelProperty("进价")
    private BigDecimal costPrice;

    @ExcelProperty("售价")
    private BigDecimal price;

    @ExcelProperty("状态")
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
