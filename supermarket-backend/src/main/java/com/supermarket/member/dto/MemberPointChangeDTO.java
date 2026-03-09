package com.supermarket.member.dto;

import java.io.Serializable;

/**
 * 会员积分变动 DTO
 */
public class MemberPointChangeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long memberId;
    private Integer changePoints;
    private String source;
    private String remark;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Integer getChangePoints() { return changePoints; }
    public void setChangePoints(Integer changePoints) { this.changePoints = changePoints; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
