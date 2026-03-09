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
    private Long memberId;          // 会员ID
    private String memberNo;        // 会员编号快照
    private String memberName;      // 会员姓名快照
    private String memberPhone;     // 会员手机号快照
    private Integer pointEarned;    // 本单获得积分
    private Integer pointDeducted;  // 本单抵扣积分（预留）
    private BigDecimal pointDeductAmount; // 积分抵扣金额（预留）
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
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getMemberNo() { return memberNo; }
    public void setMemberNo(String memberNo) { this.memberNo = memberNo; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberPhone() { return memberPhone; }
    public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }
    public Integer getPointEarned() { return pointEarned; }
    public void setPointEarned(Integer pointEarned) { this.pointEarned = pointEarned; }
    public Integer getPointDeducted() { return pointDeducted; }
    public void setPointDeducted(Integer pointDeducted) { this.pointDeducted = pointDeducted; }
    public BigDecimal getPointDeductAmount() { return pointDeductAmount; }
    public void setPointDeductAmount(BigDecimal pointDeductAmount) { this.pointDeductAmount = pointDeductAmount; }
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
}
