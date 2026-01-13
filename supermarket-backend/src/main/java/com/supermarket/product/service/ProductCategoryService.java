package com.supermarket.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.product.entity.ProductCategory;
import java.util.List;

/**
 * 商品分类服务接口
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取分类树形结构
     */
    List<ProductCategory> getCategoryTree();

    /**
     * 安全删除分类 (带校验)
     * @param id 分类ID
     * @return 是否成功
     */
    boolean removeCategoryById(Long id);
}