package com.supermarket.member.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.member.dto.MemberFormDTO;
import com.supermarket.member.entity.Member;

import java.util.List;

/**
 * 会员服务接口
 */
public interface MemberService extends IService<Member> {

    /**
     * 分页查询会员
     */
    IPage<Member> queryPage(int pageNum, int pageSize, String keyword, Integer status, String phone, String memberNo, String cardNo);

    /**
     * 新增会员
     */
    void addMember(MemberFormDTO dto);

    /**
     * 修改会员
     */
    void updateMember(MemberFormDTO dto);

    /**
     * 删除会员（逻辑删除）
     */
    void deleteMember(Long id);

    /**
     * 批量删除会员（逻辑删除）
     */
    void deleteBatch(List<Long> ids);
}
