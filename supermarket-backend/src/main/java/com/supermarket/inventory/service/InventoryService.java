package com.supermarket.inventory.service;

import com.supermarket.product.entity.Product;

import java.util.List;

/**
 * 库存服务接口
 * 负责库存相关的业务逻辑，包括库存预警、临期检测等功能
 */
public interface InventoryService {
    
    /**
     * 获取库存预警商品列表
     * 返回库存数量低于预警阈值的商品
     * @return 库存不足的商品列表
     */
    List<Product> getLowStockProducts();

    /**
     * 检查单个商品是否库存不足
     * @param productId 商品ID
     * @return true表示库存不足，false表示库存充足
     */
    boolean isLowStock(Long productId);

    /**
     * 获取库存详情
     * @param productId 商品ID
     * @return 库存信息
     */
    Product getInventoryDetail(Long productId);
}