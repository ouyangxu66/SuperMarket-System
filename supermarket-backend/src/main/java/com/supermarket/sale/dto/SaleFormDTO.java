package com.supermarket.sale.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 收银台结算表单
 */
public class SaleFormDTO implements Serializable {

    private Integer paymentType; // 支付方式 (1现金 2微信 3支付宝)
    private BigDecimal realPayAmount; // 实付金额 (可能由收银员手动改价)
    private String remark;

    // 购物车商品列表
    private List<ItemDTO> items;

    public static class ItemDTO {
        private Long productId;
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // --- Getter / Setter ---
    public Integer getPaymentType() { return paymentType; }
    public void setPaymentType(Integer paymentType) { this.paymentType = paymentType; }
    public BigDecimal getRealPayAmount() { return realPayAmount; }
    public void setRealPayAmount(BigDecimal realPayAmount) { this.realPayAmount = realPayAmount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }
}