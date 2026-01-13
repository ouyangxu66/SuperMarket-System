package com.supermarket.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.sale.entity.SaleDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售明细 Mapper
 * 对应表: sale_detail
 */
@Mapper
public interface SaleDetailMapper extends BaseMapper<SaleDetail> {
}