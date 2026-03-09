package com.supermarket.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.member.dto.MemberBalanceChangeDTO;
import com.supermarket.member.entity.MemberBalanceFlow;

/**
 * 会员余额流水服务接口
 */
public interface MemberBalanceFlowService extends IService<MemberBalanceFlow> {

    /**
     * 会员充值
     */
    void recharge(MemberBalanceChangeDTO dto);

    /**
     * 余额人工调整
     */
    void adjust(MemberBalanceChangeDTO dto);

    /**
     * 分页查询余额流水
     */
    IPage<MemberBalanceFlow> queryPage(int pageNum, int pageSize, Long memberId, String bizType);
}
