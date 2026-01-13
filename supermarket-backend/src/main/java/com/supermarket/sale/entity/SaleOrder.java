package com.supermarket.sale.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售主单实体
 */
@TableName("sale_order")
public class SaleOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;         // 订单号
    private BigDecimal totalAmount; // 总金额
    private BigDecimal realAmount;  // 实付金额
    private Integer paymentType;    // 支付方式 (1现金 2微信 3支付宝)
    private Long cashierId;         // 收银员ID
    private Integer status;         // 1已支付 -1已退款
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;

    // --- 扩展字段 ---
    @TableField(exist = false)
    private String cashierName; // 收银员姓名

    @TableField(exist = false)
    private List<SaleDetail> items; // 购物清单

    // --- Getter / Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getRealAmount() { return realAmount; }
    public void setRealAmount(BigDecimal realAmount) { this.realAmount = realAmount; }
    public Integer getPaymentType() { return paymentType; }
    public void setPaymentType(Integer paymentType) { this.paymentType = paymentType; }
    public Long getCashierId() { return cashierId; }
    public void setCashierId(Long cashierId) { this.cashierId = cashierId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }
    public List<SaleDetail> getItems() { return items; }
    public void setItems(List<SaleDetail> items) { this.items = items; }

    public void setRealPayAmount(BigDecimal bigDecimal) {
    }

}