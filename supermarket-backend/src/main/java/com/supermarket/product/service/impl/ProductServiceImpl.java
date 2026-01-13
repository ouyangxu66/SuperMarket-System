package com.supermarket.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.entity.Product;
import com.supermarket.product.mapper.ProductMapper;
import com.supermarket.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 商品服务实现类
 * 负责商品的增删改查等功能
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    // 可以在这里注入 CategoryMapper 检查分类是否存在，暂且省略

    @Override
    public IPage<Product> queryPage(int pageNum, int pageSize, String name, Long categoryId, Integer status) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 动态拼接查询条件
        wrapper.like(StringUtils.hasText(name), Product::getName, name);
        wrapper.eq(categoryId != null, Product::getCategoryId, categoryId);
        wrapper.eq(status != null, Product::getStatus, status);
        
        wrapper.orderByDesc(Product::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public void addProduct(ProductFormDTO dto) {
        // 1. 校验条码唯一性
        checkBarcodeUnique(dto.getBarcode(), null);

        // 2. DTO 转 Entity
        Product product = convertToEntity(dto);
        product.setDeleted(0); // 默认未删除

        this.save(product);
    }

    @Override
    public void updateProduct(ProductFormDTO dto) {
        Product oldProduct = this.getById(dto.getId());
        if (oldProduct == null) {
            throw new BusinessException("商品不存在");
        }

        // 1. 如果修改了条码，需要校验唯一性
        if (!oldProduct.getBarcode().equals(dto.getBarcode())) {
            checkBarcodeUnique(dto.getBarcode(), dto.getId());
        }

        // 2. 更新字段
        Product product = convertToEntity(dto);
        product.setId(dto.getId()); // 确保ID存在
        
        this.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        this.updateById(product);
    }

    /**
     * 辅助方法：校验条码唯一
     * @param barcode 条码
     * @param excludeId 排除的ID (修改时用，排除自己)
     */
    private void checkBarcodeUnique(String barcode, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getBarcode, barcode);
        if (excludeId != null) {
            wrapper.ne(Product::getId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException("条形码已存在: " + barcode);
        }
    }

    /**
     * 辅助方法：DTO 转 Entity
     */
    private Product convertToEntity(ProductFormDTO dto) {
        Product p = new Product();
        p.setCategoryId(dto.getCategoryId());
        p.setBarcode(dto.getBarcode());
        p.setName(dto.getName());
        p.setSpec(dto.getSpec());
        p.setUnit(dto.getUnit());
        p.setPrice(dto.getPrice());
        p.setCostPrice(dto.getCostPrice());
        p.setStock(dto.getStock());
        p.setLowStockThreshold(dto.getLowStockThreshold());
        p.setStatus(dto.getStatus());
        p.setRemark(dto.getRemark());
        return p;
    }
}