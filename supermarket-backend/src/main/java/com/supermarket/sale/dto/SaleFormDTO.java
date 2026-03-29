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
    private Long memberId; // 会员ID（可选）
    private String remark;
    private Boolean usePoints; // 是否使用积分抵扣
    private Integer usedPoints; // 使用的积分数量

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
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Boolean getUsePoints() { return usePoints; }
    public void setUsePoints(Boolean usePoints) { this.usePoints = usePoints; }
    public Integer getUsedPoints() { return usedPoints; }
    public void setUsedPoints(Integer usedPoints) { this.usedPoints = usedPoints; }
    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }
}