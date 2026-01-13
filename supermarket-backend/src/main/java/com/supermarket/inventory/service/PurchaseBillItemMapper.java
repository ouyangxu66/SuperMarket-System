package com.supermarket.inventory.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.inventory.entity.PurchaseBillItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 进货单商品项 Mapper 接口
 */
@Mapper
public interface PurchaseBillItemMapper extends BaseMapper<PurchaseBillItem> {
}