package com.supermarket.inventory.controller;

import com.supermarket.common.result.Result;
import com.supermarket.inventory.service.InventoryService;
import com.supermarket.product.entity.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存控制器
 * 提供库存相关的API接口，包括库存预警、库存查询等功能
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    // 构造器注入
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * 获取库存预警商品列表
     * GET /inventory/low-stock
     * 返回库存数量低于预警阈值的商品列表
     */
    @GetMapping("/low-stock")
    public Result<List<Product>> getLowStockProducts() {
        List<Product> lowStockProducts = inventoryService.getLowStockProducts();
        return Result.success(lowStockProducts);
    }

    /**
     * 检查单个商品是否库存不足
     * GET /inventory/check-low-stock/{id}
     * 返回true表示库存不足，false表示库存充足
     */
    @GetMapping("/check-low-stock/{id}")
    public Result<Boolean> checkLowStock(@PathVariable Long id) {
        boolean isLowStock = inventoryService.isLowStock(id);
        return Result.success(isLowStock);
    }

    /**
     * 获取库存详情
     * GET /inventory/detail/{id}
     * 返回指定商品的库存详情
     */
    @GetMapping("/detail/{id}")
    public Result<Product> getInventoryDetail(@PathVariable Long id) {
        Product product = inventoryService.getInventoryDetail(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
}