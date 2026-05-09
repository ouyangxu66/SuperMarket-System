package com.supermarket.inventory.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.io.Serializable;

@ColumnWidth(20)
public class InventoryCountReportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty("盘点编号")
    @ColumnWidth(25)
    private String countNumber;

    @ExcelProperty("盘点标题")
    @ColumnWidth(30)
    private String title;

    @ExcelProperty("商品名称")
    @ColumnWidth(30)
    private String productName;

    @ExcelProperty("商品条码")
    private String productBarcode;

    @ExcelProperty("规格")
    private String productSpec;

    @ExcelProperty("单位")
    private String productUnit;

    @ExcelProperty("系统库存")
    private Integer systemStock;

    @ExcelProperty("实盘库存")
    private Integer actualStock;

    @ExcelProperty("差异数量")
    private Integer difference;

    @ExcelProperty("差异原因")
    @ColumnWidth(30)
    private String discrepancyReason;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;

    public String getCountNumber() { return countNumber; }
    public void setCountNumber(String countNumber) { this.countNumber = countNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

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
    public void setDifference(Integer difference) { this.difference = difference; }

    public String getDiscrepancyReason() { return discrepancyReason; }
    public void setDiscrepancyReason(String discrepancyReason) { this.discrepancyReason = discrepancyReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
