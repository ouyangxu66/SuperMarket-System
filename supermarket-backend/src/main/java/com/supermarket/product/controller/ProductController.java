package com.supermarket.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.result.Result;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理控制器
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    // 构造器注入
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 分页查询
     * GET /api/product/page?name=可乐&status=1
     */
    @GetMapping("/page")
    public Result<IPage<Product>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        IPage<Product> page = productService.queryPage(pageNum, pageSize, name, categoryId, status);
        return Result.success(page);
    }

    /**
     * 新增商品
     * POST /api/product
     */
    @PostMapping
    public Result<?> add(@RequestBody ProductFormDTO dto) {
        productService.addProduct(dto);
        return Result.success("新增商品成功");
    }

    /**
     * 修改商品
     * PUT /api/product
     */
    @PutMapping
    public Result<?> update(@RequestBody ProductFormDTO dto) {
        productService.updateProduct(dto);
        return Result.success("修改商品成功");
    }

    /**
     * 修改状态 (上架/下架)
     * PUT /api/product/{id}/status/{status}
     */
    @PutMapping("/{id}/status/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        productService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 删除商品
     * DELETE /api/product/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 根据条码查询商品 (用于收银台扫码)
     * GET /api/product/barcode/{barcode}
     */
    @GetMapping("/barcode/{barcode}")
    public Result<Product> getByBarcode(@PathVariable String barcode) {
        Product product = productService.query().eq("barcode", barcode).one();
        if (product == null) {
            return Result.error("未找到该商品");
        }
        return Result.success(product);
    }
}