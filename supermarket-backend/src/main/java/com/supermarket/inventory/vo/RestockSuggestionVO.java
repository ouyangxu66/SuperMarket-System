package com.supermarket.inventory.vo;

import java.math.BigDecimal;

/**
 * 智能补货建议VO
 */
public class RestockSuggestionVO {

    private Long productId;
    private String productName;
    private String barcode;
    private String spec;
    private String unit;
    private Integer currentStock;
    private Integer lowStockThreshold;
    private BigDecimal avgDailySales;
    private Integer suggestedQuantity;
    private Integer restockDays;
    private String urgencyLevel;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public BigDecimal getAvgDailySales() {
        return avgDailySales;
    }

    public void setAvgDailySales(BigDecimal avgDailySales) {
        this.avgDailySales = avgDailySales;
    }

    public Integer getSuggestedQuantity() {
        return suggestedQuantity;
    }

    public void setSuggestedQuantity(Integer suggestedQuantity) {
        this.suggestedQuantity = suggestedQuantity;
    }

    public Integer getRestockDays() {
        return restockDays;
    }

    public void setRestockDays(Integer restockDays) {
        this.restockDays = restockDays;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
}
