package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.member.dto.MemberPointChangeDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.MemberPointFlow;
import com.supermarket.member.mapper.MemberPointFlowMapper;
import com.supermarket.member.service.MemberPointFlowService;
import com.supermarket.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 会员积分流水服务实现类
 */
@Service
public class MemberPointFlowServiceImpl extends ServiceImpl<MemberPointFlowMapper, MemberPointFlow> implements MemberPointFlowService {

    private final MemberService memberService;

    public MemberPointFlowServiceImpl(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjust(MemberPointChangeDTO dto) {
        validateAdjust(dto);
        Member member = getValidMember(dto.getMemberId());

        int beforePoints = member.getPoints() == null ? 0 : member.getPoints();
        int changePoints = dto.getChangePoints();
        int afterPoints = beforePoints + changePoints;
        if (afterPoints < 0) {
            throw new BusinessException("积分调整后不能小于0");
        }

        member.setPoints(afterPoints);
        memberService.updateById(member);

        MemberPointFlow flow = new MemberPointFlow();
        flow.setMemberId(member.getId());
        flow.setChangeType(changePoints >= 0 ? 1 : -1);
        flow.setBizType("MANUAL_ADJUST");
        flow.setBizNo(generateBizNo());
        flow.setBeforePoints(beforePoints);
        flow.setChangePoints(Math.abs(changePoints));
        flow.setAfterPoints(afterPoints);
        flow.setOperatorId(1L);
        flow.setOperatorName("系统管理员");
        flow.setSource(dto.getSource().trim());
        flow.setRemark(StringUtils.hasText(dto.getRemark()) ? dto.getRemark().trim() : null);
        this.save(flow);
    }

    @Override
    public IPage<MemberPointFlow> queryPage(int pageNum, int pageSize, Long memberId, String bizType) {
        Page<MemberPointFlow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberPointFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(memberId != null, MemberPointFlow::getMemberId, memberId)
                .eq(StringUtils.hasText(bizType), MemberPointFlow::getBizType, bizType)
                .eq(MemberPointFlow::getDeleted, 0)
                .orderByDesc(MemberPointFlow::getCreateTime);
        return this.page(page, wrapper);
    }

    private void validateAdjust(MemberPointChangeDTO dto) {
        if (dto == null || dto.getMemberId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        if (dto.getChangePoints() == null || dto.getChangePoints() == 0) {
            throw new BusinessException("积分调整值不能为0");
        }
        if (!StringUtils.hasText(dto.getSource())) {
            throw new BusinessException("积分来源说明不能为空");
        }
    }

    private Member getValidMember(Long memberId) {
        Member member = memberService.getById(memberId);
        if (member == null || (member.getDeleted() != null && member.getDeleted() == 1)) {
            throw new BusinessException("会员不存在");
        }
        if (member.getStatus() != null && member.getStatus() == 0) {
            throw new BusinessException("会员已停用，无法进行积分操作");
        }
        return member;
    }

    private String generateBizNo() {
        return "PT" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + (int) ((Math.random() * 9 + 1) * 1000);
    }
}
