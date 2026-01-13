package com.supermarket.sale.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 销售结账 DTO
 */
public class SaleOrderDTO implements Serializable {

    private Integer paymentType; // 支付方式 (1:现金, 2:微信, 3:支付宝)
    private String remark;       // 备注
    private List<SaleItemDTO> items; // 购物清单

    // 内部类：商品明细
    public static class SaleItemDTO {
        private Long productId;
        private Integer quantity;

        // Getter/Setter
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getter/Setter
    public Integer getPaymentType() { return paymentType; }
    public void setPaymentType(Integer paymentType) { this.paymentType = paymentType; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<SaleItemDTO> getItems() { return items; }
    public void setItems(List<SaleItemDTO> items) { this.items = items; }
}