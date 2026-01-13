package com.supermarket.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.product.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper 接口
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
}