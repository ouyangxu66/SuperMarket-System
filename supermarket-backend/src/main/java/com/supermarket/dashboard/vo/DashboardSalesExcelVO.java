package com.supermarket.dashboard.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.io.Serializable;
import java.math.BigDecimal;

@ColumnWidth(20)
public class DashboardSalesExcelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty("日期")
    private String date;

    @ExcelProperty("销售额")
    private BigDecimal salesAmount;

    @ExcelProperty("订单数")
    private Long orderCount;

    @ExcelProperty("客单价")
    private BigDecimal averageOrderValue;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
}
