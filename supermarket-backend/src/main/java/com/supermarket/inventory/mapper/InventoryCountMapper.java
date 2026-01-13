package com.supermarket.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.inventory.entity.InventoryCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存盘点 Mapper 接口
 */
@Mapper
public interface InventoryCountMapper extends BaseMapper<InventoryCount> {
}