package com.supermarket.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.inventory.entity.InventoryCountDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存盘点明细 Mapper 接口
 */
@Mapper
public interface InventoryCountDetailMapper extends BaseMapper<InventoryCountDetail> {
}