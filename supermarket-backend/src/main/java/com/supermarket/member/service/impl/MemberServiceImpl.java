package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.member.dto.MemberFormDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.service.MemberService;
import com.supermarket.sale.entity.SaleOrder;
import com.supermarket.sale.mapper.SaleOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 会员服务实现类
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    private final SaleOrderMapper saleOrderMapper;

    public MemberServiceImpl(SaleOrderMapper saleOrderMapper) {
        this.saleOrderMapper = saleOrderMapper;
    }

    @Override
    public IPage<Member> queryPage(int pageNum, int pageSize, String keyword, Integer status, String phone, String memberNo, String cardNo) {
        Page<Member> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, Member::getStatus, status)
                .like(StringUtils.hasText(phone), Member::getPhone, phone)
                .like(StringUtils.hasText(memberNo), Member::getMemberNo, memberNo)
                .like(StringUtils.hasText(cardNo), Member::getCardNo, cardNo)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Member::getPhone, keyword)
                        .or()
                        .like(Member::getName, keyword)
                        .or()
                        .like(Member::getCardNo, keyword)
                        .or()
                        .like(Member::getMemberNo, keyword)
                        .or()
                        .like(Member::getNickname, keyword))
                .eq(Member::getDeleted, 0)
                .orderByDesc(Member::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(MemberFormDTO dto) {
        validateForm(dto, false);
        checkPhoneUnique(dto.getPhone(), null);
        checkCardNoUnique(dto.getCardNo(), null);

        Member member = new Member();
        member.setMemberNo(generateMemberNo());
        member.setCardNo(dto.getCardNo().trim());
        member.setName(dto.getName().trim());
        member.setNickname(trimToNull(dto.getNickname()));
        member.setPhone(dto.getPhone().trim());
        member.setGender(dto.getGender() != null ? dto.getGender() : 2);
        member.setBirthday(dto.getBirthday());
        member.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        member.setRemark(trimToNull(dto.getRemark()));
        member.setRegisterChannel(StringUtils.hasText(dto.getRegisterChannel()) ? dto.getRegisterChannel().trim() : "OFFLINE");
        member.setLevelId(dto.getLevelId());
        member.setRegisterTime(new Date());
        member.setBalance(BigDecimal.ZERO);
        member.setPoints(0);
        member.setTotalRechargeAmount(BigDecimal.ZERO);
        member.setTotalConsumeAmount(BigDecimal.ZERO);
        member.setTotalConsumeCount(0);

        this.save(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMember(MemberFormDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("会员ID不能为空");
        }
        validateForm(dto, true);

        Member member = this.getById(dto.getId());
        if (member == null || (member.getDeleted() != null && member.getDeleted() == 1)) {
            throw new BusinessException("会员不存在");
        }

        checkPhoneUnique(dto.getPhone(), dto.getId());
        checkCardNoUnique(dto.getCardNo(), dto.getId());

        member.setCardNo(dto.getCardNo().trim());
        member.setName(dto.getName().trim());
        member.setNickname(trimToNull(dto.getNickname()));
        member.setPhone(dto.getPhone().trim());
        member.setGender(dto.getGender() != null ? dto.getGender() : member.getGender());
        member.setBirthday(dto.getBirthday());
        member.setStatus(dto.getStatus() != null ? dto.getStatus() : member.getStatus());
        member.setRemark(trimToNull(dto.getRemark()));
        member.setRegisterChannel(StringUtils.hasText(dto.getRegisterChannel()) ? dto.getRegisterChannel().trim() : member.getRegisterChannel());
        member.setLevelId(dto.getLevelId());

        this.updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMember(Long id) {
        Member member = this.getById(id);
        if (member == null || (member.getDeleted() != null && member.getDeleted() == 1)) {
            throw new BusinessException("会员不存在");
        }

        validateDelete(id);
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的会员");
        }
        for (Long id : ids) {
            deleteMember(id);
        }
    }

    private void validateForm(MemberFormDTO dto, boolean isUpdate) {
        if (!isUpdate && dto == null) {
            throw new BusinessException("会员信息不能为空");
        }
        if (dto == null) {
            throw new BusinessException("会员信息不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException("会员姓名不能为空");
        }
        if (!StringUtils.hasText(dto.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }
        if (!dto.getPhone().trim().matches("^1\\d{10}$")) {
            throw new BusinessException("手机号格式不正确");
        }
        if (!StringUtils.hasText(dto.getCardNo())) {
            throw new BusinessException("会员卡号不能为空");
        }
        if (dto.getName().trim().length() > 50) {
            throw new BusinessException("会员姓名长度不能超过50个字符");
        }
        if (StringUtils.hasText(dto.getNickname()) && dto.getNickname().trim().length() > 50) {
            throw new BusinessException("会员昵称长度不能超过50个字符");
        }
        if (dto.getCardNo().trim().length() > 32) {
            throw new BusinessException("会员卡号长度不能超过32个字符");
        }
        if (StringUtils.hasText(dto.getRemark()) && dto.getRemark().trim().length() > 255) {
            throw new BusinessException("备注长度不能超过255个字符");
        }
    }

    private void checkPhoneUnique(String phone, Long excludeId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getPhone, phone.trim())
                .eq(Member::getDeleted, 0)
                .ne(excludeId != null, Member::getId, excludeId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("手机号已存在: " + phone);
        }
    }

    private void checkCardNoUnique(String cardNo, Long excludeId) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getCardNo, cardNo.trim())
                .eq(Member::getDeleted, 0)
                .ne(excludeId != null, Member::getId, excludeId);
        if (this.count(wrapper) > 0) {
            throw new BusinessException("会员卡号已存在: " + cardNo);
        }
    }

    private void validateDelete(Long memberId) {
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaleOrder::getDeleted, 0)
                .eq(SaleOrder::getMemberId, memberId);
        Long saleCount = saleOrderMapper.selectCount(wrapper);
        if (saleCount != null && saleCount > 0) {
            throw new BusinessException("该会员存在历史消费记录，不允许删除，请改为停用会员");
        }
    }

    private String generateMemberNo() {
        String prefix = "M" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String memberNo = prefix;
        int suffix = 1;
        while (this.count(new LambdaQueryWrapper<Member>().eq(Member::getMemberNo, memberNo)) > 0) {
            memberNo = prefix + String.format("%03d", suffix++);
        }
        return memberNo;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
