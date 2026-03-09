package com.supermarket.member.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.result.Result;
import com.supermarket.member.dto.MemberPointChangeDTO;
import com.supermarket.member.entity.MemberPointFlow;
import com.supermarket.member.service.MemberPointFlowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员积分控制器
 */
@RestController
@RequestMapping("/member/point")
public class MemberPointController {

    private final MemberPointFlowService memberPointFlowService;

    public MemberPointController(MemberPointFlowService memberPointFlowService) {
        this.memberPointFlowService = memberPointFlowService;
    }

    /**
     * 积分人工调整
     */
    @PostMapping("/adjust")
    public Result<Boolean> adjust(@RequestBody MemberPointChangeDTO dto) {
        memberPointFlowService.adjust(dto);
        return Result.success();
    }

    /**
     * 分页查询积分流水
     */
    @GetMapping("/flow/page")
    public Result<IPage<MemberPointFlow>> getPointFlowPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String bizType) {
        return Result.success(memberPointFlowService.queryPage(pageNum, pageSize, memberId, bizType));
    }
}
