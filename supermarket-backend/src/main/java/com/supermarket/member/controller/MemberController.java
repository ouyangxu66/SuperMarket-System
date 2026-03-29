package com.supermarket.member.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.common.result.Result;
import com.supermarket.member.dto.MemberFormDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员控制器
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 分页查询会员列表
     */
    @GetMapping("/page")
    public Result<IPage<Member>> getMemberPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String memberNo,
            @RequestParam(required = false) String cardNo) {

        IPage<Member> pageResult = memberService.queryPage(pageNum, pageSize, keyword, status, phone, memberNo, cardNo);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取会员详情
     */
    @GetMapping("/{id}")
    public Result<Member> getMemberById(@PathVariable Long id) {
        Member member = memberService.getById(id);
        if (member == null || (member.getDeleted() != null && member.getDeleted() == 1)) {
            return Result.error("会员不存在");
        }
        return Result.success(member);
    }

    /**
     * 新增会员
     */
    @PostMapping
    public Result<Boolean> saveMember(@RequestBody MemberFormDTO dto) {
        memberService.addMember(dto);
        return Result.success();
    }

    /**
     * 修改会员
     */
    @PutMapping
    public Result<Boolean> updateMember(@RequestBody MemberFormDTO dto) {
        memberService.updateMember(dto);
        return Result.success();
    }

    /**
     * 删除会员（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Result.success();
    }

    /**
     * 批量删除会员（逻辑删除）
     */
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        memberService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 收银台快速查询会员
     */
    @GetMapping("/simple")
    public Result<Member> getSimpleMember(@RequestParam(required = false) String phone,
                                          @RequestParam(required = false) String name) {
        String normalizedPhone = phone == null ? null : phone.trim();
        String normalizedName = name == null ? null : name.trim();

        if ((normalizedPhone == null || normalizedPhone.isEmpty())
                && (normalizedName == null || normalizedName.isEmpty())) {
            throw new BusinessException("请至少提供手机号或者姓名进行查询");
        }
        Member member = memberService.lambdaQuery()
                .eq(normalizedPhone != null && !normalizedPhone.isEmpty(), Member::getPhone, normalizedPhone)
                .like(normalizedName != null && !normalizedName.isEmpty(), Member::getName, normalizedName)
                .eq(Member::getDeleted, 0)
                .last("LIMIT 1")
                .one();
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        return Result.success(member);
    }
}
