package com.supermarket.sale.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ColumnWidth(20)
public class SaleOrderExcelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @ExcelProperty("应收金额")
    private BigDecimal totalAmount;

    @ExcelProperty("实收金额")
    private BigDecimal realAmount;

    @ExcelProperty("支付方式")
    private String paymentMethod;

    @ExcelProperty("会员姓名")
    private String memberName;

    @ExcelProperty("会员手机号")
    private String memberPhone;

    @ExcelProperty("本单积分")
    private Integer pointEarned;

    @ExcelProperty("销售时间")
    @ColumnWidth(25)
    private Date createTime;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public Integer getPointEarned() {
        return pointEarned;
    }

    public void setPointEarned(Integer pointEarned) {
        this.pointEarned = pointEarned;
    }
}
