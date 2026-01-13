package com.supermarket.inventory.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.inventory.entity.PurchaseBill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 进货单 Mapper 接口
 */
@Mapper
public interface PurchaseBillMapper extends BaseMapper<PurchaseBill> {
}