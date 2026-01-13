package com.supermarket.inventory.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 进货单提交对象
 */
public class PurchaseBillDTO implements Serializable {

    private Long id;            // 修改时必填
    private Long supplierId;    // 供应商ID
    private String remark;      // 备注

    // 明细列表
    private List<PurchaseItemDTO> items;

    // --- 内部静态类：明细 DTO ---
    public static class PurchaseItemDTO implements Serializable {
        private Long productId;     // 商品ID
        private Integer quantity;   // 进货数量
        private BigDecimal price;   // 进货单价

        // Getter/Setter
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<PurchaseItemDTO> getItems() { return items; }
    public void setItems(List<PurchaseItemDTO> items) { this.items = items; }
}