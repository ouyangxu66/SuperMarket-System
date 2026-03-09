package com.supermarket.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.member.dto.MemberPointChangeDTO;
import com.supermarket.member.entity.MemberPointFlow;

/**
 * 会员积分流水服务接口
 */
public interface MemberPointFlowService extends IService<MemberPointFlow> {

    /**
     * 积分人工调整
     */
    void adjust(MemberPointChangeDTO dto);

    /**
     * 分页查询积分流水
     */
    IPage<MemberPointFlow> queryPage(int pageNum, int pageSize, Long memberId, String bizType);
}
