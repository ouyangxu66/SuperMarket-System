package com.supermarket.member.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.result.Result;
import com.supermarket.member.dto.MemberBalanceChangeDTO;
import com.supermarket.member.entity.MemberBalanceFlow;
import com.supermarket.member.service.MemberBalanceFlowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员余额控制器
 */
@RestController
@RequestMapping("/member/balance")
public class MemberBalanceController {

    private final MemberBalanceFlowService memberBalanceFlowService;

    public MemberBalanceController(MemberBalanceFlowService memberBalanceFlowService) {
        this.memberBalanceFlowService = memberBalanceFlowService;
    }

    /**
     * 会员充值
     */
    @PostMapping("/recharge")
    public Result<Boolean> recharge(@RequestBody MemberBalanceChangeDTO dto) {
        memberBalanceFlowService.recharge(dto);
        return Result.success();
    }

    /**
     * 余额人工调整
     */
    @PostMapping("/adjust")
    public Result<Boolean> adjust(@RequestBody MemberBalanceChangeDTO dto) {
        memberBalanceFlowService.adjust(dto);
        return Result.success();
    }

    /**
     * 分页查询余额流水
     */
    @GetMapping("/flow/page")
    public Result<IPage<MemberBalanceFlow>> getBalanceFlowPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String bizType) {
        return Result.success(memberBalanceFlowService.queryPage(pageNum, pageSize, memberId, bizType));
    }
}
