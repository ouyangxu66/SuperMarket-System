package com.supermarket.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.result.Result;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 商品控制器
 * 提供商品的增删改查接口
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    // --- 构造器注入 ---
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 分页查询商品列表
     * GET /api/product/page?pageNum=1&pageSize=10&name=可乐&categoryId=1&status=1
     */
    @GetMapping("/page")
    public Result<IPage<Product>> getProductPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {

        IPage<Product> pageResult = productService.queryPage(pageNum, pageSize, name, categoryId, status);
        return Result.success(pageResult);
    }

    /**
     * 根据ID获取商品详情
     * GET /api/product/{id}
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 不返回已删除的商品
        if (product.getDeleted() != null && product.getDeleted() == 1) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    /**
     * 新增商品
     * POST /api/product
     */
    @PostMapping
    public Result<String> saveProduct(@RequestBody ProductFormDTO dto) {
        try {
            productService.addProduct(dto);
            return Result.success("新增商品成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改商品
     * PUT /api/product
     */
    @PutMapping
    public Result<String> updateProduct(@RequestBody ProductFormDTO dto) {
        try {
            productService.updateProduct(dto);
            return Result.success("修改商品成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改商品状态 (上架/下架)
     * PUT /api/product/{id}/status/{status}
     */
    @PutMapping("/{id}/status/{status}")
    public Result<String> updateProductStatus(@PathVariable Long id, @PathVariable Integer status) {
        try {
            productService.updateStatus(id, status);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除商品 (逻辑删除)
     * DELETE /api/product/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 逻辑删除：将 deleted 字段设为 1
        product.setDeleted(1);
        productService.updateById(product);
        return Result.success("删除成功");
    }

    /**
     * 根据条码查询商品
     * GET /api/product/barcode/{barcode}
     */
    @GetMapping("/barcode/{barcode}")
    public Result<Product> getProductByBarcode(@PathVariable String barcode) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getBarcode, barcode)
               .eq(Product::getDeleted, 0); // 只查询未删除的
        Product product = productService.getOne(wrapper);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
    
    /**
     * 获取即将过期的商品列表
     * GET /api/product/expiring-soon?days=7
     */
    @GetMapping("/expiring-soon")
    public Result<java.util.List<Product>> getExpiringSoonProducts(
            @RequestParam(defaultValue = "7") Integer days) {
        java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, days);
        Date expiringDate = calendar.getTime();
        
        java.util.List<Product> expiringSoonProducts = productService.getExpiringSoonProducts(expiringDate);
        return Result.success(expiringSoonProducts);
    }
    
    /**
     * 获取已过期的商品列表
     * GET /api/product/expired
     */
    @GetMapping("/expired")
    public Result<java.util.List<Product>> getExpiredProducts() {
        java.util.List<Product> expiredProducts = productService.getExpiredProducts();
        return Result.success(expiredProducts);
    }
}