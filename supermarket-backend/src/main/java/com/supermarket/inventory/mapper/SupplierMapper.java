package com.supermarket.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.inventory.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商 Mapper 接口
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}