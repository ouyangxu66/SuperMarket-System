package com.supermarket.member.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员积分流水实体
 */
@TableName("member_point_flow")
public class MemberPointFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private Integer changeType;
    private String bizType;
    private String bizNo;
    private Integer beforePoints;
    private Integer changePoints;
    private Integer afterPoints;
    private Long operatorId;
    private String operatorName;
    private String source;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Integer getChangeType() { return changeType; }
    public void setChangeType(Integer changeType) { this.changeType = changeType; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public String getBizNo() { return bizNo; }
    public void setBizNo(String bizNo) { this.bizNo = bizNo; }
    public Integer getBeforePoints() { return beforePoints; }
    public void setBeforePoints(Integer beforePoints) { this.beforePoints = beforePoints; }
    public Integer getChangePoints() { return changePoints; }
    public void setChangePoints(Integer changePoints) { this.changePoints = changePoints; }
    public Integer getAfterPoints() { return afterPoints; }
    public void setAfterPoints(Integer afterPoints) { this.afterPoints = afterPoints; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
