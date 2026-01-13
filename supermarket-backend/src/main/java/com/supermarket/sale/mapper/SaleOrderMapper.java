package com.supermarket.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.sale.entity.SaleOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售主单 Mapper
 * 对应表: sale_order
 */
@Mapper
public interface SaleOrderMapper extends BaseMapper<SaleOrder> {
}