package com.supermarket.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.product.entity.ProductCategory;
import com.supermarket.product.mapper.ProductCategoryMapper;
import com.supermarket.product.service.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 商品分类服务实现类
 * 负责分类的树形结构构建和安全删除等功能
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    // 构造器注入 (如果后续需要引入 ProductService 来检查分类下是否有商品，可以在这里注入)
    public ProductCategoryServiceImpl() {
    }

    @Override
    public List<ProductCategory> getCategoryTree() {
        // 1. 查询所有未删除的分类，按排序字段升序
        // 只查需要的字段，提高效率 (可选)
        List<ProductCategory> allList = this.list(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getStatus, 1) // 只查启用的
                .orderByAsc(ProductCategory::getSort));

        // 2. 使用 Stream 流构建树形结构
        // 找到所有一级分类 (parentId = 0)
        return allList.stream()
                .filter(category -> category.getParentId() == 0)
                .map(category -> {
                    category.setChildren(getChildren(category, allList));
                    return category;
                })
                .collect(Collectors.toList());
    }

    /**
     * 自定义删除方法：包含校验逻辑
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeCategoryById(Long id) {
        // 1. 检查是否存在子分类
        LambdaQueryWrapper<ProductCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductCategory::getParentId, id);

        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException("存在子分类，不允许删除！请先删除或移动子分类。");
        }

        // 2. TODO: 严格来说，还需要检查该分类下是否关联了商品
        // LambdaQueryWrapper<Product> productWrapper = ...
        // if (productCount > 0) throw new BusinessException("该分类下包含商品，无法删除");

        // 3. 执行逻辑删除
        return this.removeById(id);
    }

    /**
     * 递归查找子节点
     */
    private List<ProductCategory> getChildren(ProductCategory root, List<ProductCategory> all) {
        return all.stream()
                .filter(category -> category.getParentId().equals(root.getId()))
                .map(category -> {
                    category.setChildren(getChildren(category, all));
                    return category;
                })
                .collect(Collectors.toList());
    }
}