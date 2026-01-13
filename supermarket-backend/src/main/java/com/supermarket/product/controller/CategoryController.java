package com.supermarket.product.controller;

import com.supermarket.common.result.Result;
import com.supermarket.product.entity.ProductCategory;
import com.supermarket.product.service.ProductCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理控制器
 */
@RestController
@RequestMapping("/product/category")
public class CategoryController {

    private final ProductCategoryService categoryService;

    public CategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<ProductCategory>> getTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody ProductCategory category) {
        return Result.success(categoryService.save(category));
    }

    /**
     * 修改分类
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody ProductCategory category) {
        return Result.success(categoryService.updateById(category));
    }

    /**
     * 删除分类
     * DELETE /product/category/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 调用我们自定义的、带安全校验的删除方法
        return Result.success(categoryService.removeCategoryById(id));
    }
}