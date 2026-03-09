package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.member.dto.MemberBalanceChangeDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.MemberBalanceFlow;
import com.supermarket.member.mapper.MemberBalanceFlowMapper;
import com.supermarket.member.service.MemberBalanceFlowService;
import com.supermarket.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 会员余额流水服务实现类
 */
@Service
public class MemberBalanceFlowServiceImpl extends ServiceImpl<MemberBalanceFlowMapper, MemberBalanceFlow> implements MemberBalanceFlowService {

    private final MemberService memberService;

    public MemberBalanceFlowServiceImpl(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recharge(MemberBalanceChangeDTO dto) {
        validateRecharge(dto);
        Member member = getValidMember(dto.getMemberId());

        BigDecimal beforeBalance = defaultBalance(member.getBalance());
        BigDecimal amount = dto.getAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal afterBalance = beforeBalance.add(amount);

        member.setBalance(afterBalance);
        member.setTotalRechargeAmount(defaultBalance(member.getTotalRechargeAmount()).add(amount));
        member.setLastRechargeTime(new Date());
        memberService.updateById(member);

        saveFlow(member.getId(), 1, "RECHARGE", generateBizNo("RC"), beforeBalance, amount, afterBalance, dto.getSource(), dto.getRemark());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjust(MemberBalanceChangeDTO dto) {
        validateAdjust(dto);
        Member member = getValidMember(dto.getMemberId());

        BigDecimal beforeBalance = defaultBalance(member.getBalance());
        BigDecimal changeAmount = dto.getChangeAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal afterBalance = beforeBalance.add(changeAmount);
        if (afterBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("余额调整后不能小于0");
        }

        member.setBalance(afterBalance);
        if (changeAmount.compareTo(BigDecimal.ZERO) > 0) {
            member.setTotalRechargeAmount(defaultBalance(member.getTotalRechargeAmount()).add(changeAmount));
            member.setLastRechargeTime(new Date());
        }
        memberService.updateById(member);

        saveFlow(member.getId(), changeAmount.compareTo(BigDecimal.ZERO) >= 0 ? 1 : -1,
                "MANUAL_ADJUST", generateBizNo("BA"), beforeBalance, changeAmount.abs(), afterBalance, dto.getSource(), dto.getRemark());
    }

    @Override
    public IPage<MemberBalanceFlow> queryPage(int pageNum, int pageSize, Long memberId, String bizType) {
        Page<MemberBalanceFlow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberBalanceFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(memberId != null, MemberBalanceFlow::getMemberId, memberId)
                .eq(StringUtils.hasText(bizType), MemberBalanceFlow::getBizType, bizType)
                .eq(MemberBalanceFlow::getDeleted, 0)
                .orderByDesc(MemberBalanceFlow::getCreateTime);
        return this.page(page, wrapper);
    }

    private void validateRecharge(MemberBalanceChangeDTO dto) {
        if (dto == null || dto.getMemberId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        if (!StringUtils.hasText(dto.getSource())) {
            throw new BusinessException("充值来源不能为空");
        }
    }

    private void validateAdjust(MemberBalanceChangeDTO dto) {
        if (dto == null || dto.getMemberId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        if (dto.getChangeAmount() == null || dto.getChangeAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("调整金额不能为0");
        }
        if (!StringUtils.hasText(dto.getSource())) {
            throw new BusinessException("调整来源不能为空");
        }
    }

    private Member getValidMember(Long memberId) {
        Member member = memberService.getById(memberId);
        if (member == null || (member.getDeleted() != null && member.getDeleted() == 1)) {
            throw new BusinessException("会员不存在");
        }
        if (member.getStatus() != null && member.getStatus() == 0) {
            throw new BusinessException("会员已停用，无法进行余额操作");
        }
        return member;
    }

    private void saveFlow(Long memberId, Integer changeType, String bizType, String bizNo,
                          BigDecimal beforeBalance, BigDecimal changeAmount, BigDecimal afterBalance,
                          String source, String remark) {
        MemberBalanceFlow flow = new MemberBalanceFlow();
        flow.setMemberId(memberId);
        flow.setChangeType(changeType);
        flow.setBizType(bizType);
        flow.setBizNo(bizNo);
        flow.setBeforeBalance(beforeBalance);
        flow.setChangeAmount(changeAmount);
        flow.setAfterBalance(afterBalance);
        flow.setOperatorId(1L);
        flow.setOperatorName("系统管理员");
        flow.setSource(source.trim());
        flow.setRemark(StringUtils.hasText(remark) ? remark.trim() : null);
        this.save(flow);
    }

    private BigDecimal defaultBalance(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String generateBizNo(String prefix) {
        return prefix + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);
    }
}
