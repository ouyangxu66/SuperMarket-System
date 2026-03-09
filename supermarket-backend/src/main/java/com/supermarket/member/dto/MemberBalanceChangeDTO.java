package com.supermarket.member.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员余额变动 DTO
 */
public class MemberBalanceChangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long memberId;
    private BigDecimal amount;
    private BigDecimal changeAmount;
    private String source;
    private String remark;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
