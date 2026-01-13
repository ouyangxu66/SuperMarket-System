package com.supermarket.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.inventory.service.InventoryService;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存服务实现类
 * 负责库存相关的业务逻辑，包括库存预警、临期检测等功能
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductService productService;

    // 构造器注入
    public InventoryServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public List<Product> getLowStockProducts() {
        // 查询库存小于等于预警阈值的商品，且状态正常的商品
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 库存数量 <= 预警阈值（且预警阈值非空），且商品状态正常（上架），且未删除
        wrapper.and(qw -> qw.isNotNull(Product::getLowStockThreshold) // 预警阈值不为空
                        .apply("stock <= low_stock_threshold")) // 库存 <= 预警阈值
               .eq(Product::getStatus, 1)  // 状态正常（上架）
               .eq(Product::getDeleted, 0) // 未删除
               .orderByAsc(Product::getStock); // 按库存数量升序排列
        
        return productService.list(wrapper);
    }

    @Override
    public boolean isLowStock(Long productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return false; // 商品不存在，返回false
        }
        
        // 检查库存是否低于或等于预警阈值
        Integer threshold = product.getLowStockThreshold();
        if (threshold == null) {
            return false; // 如果没有设置预警阈值，则认为不处于低库存状态
        }
        
        return product.getStock() != null && product.getStock() <= threshold;
    }

    @Override
    public Product getInventoryDetail(Long productId) {
        return productService.getById(productId);
    }
}