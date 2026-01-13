package com.supermarket.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.entity.Product;
import com.supermarket.product.mapper.ProductMapper;
import com.supermarket.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<Product> queryPage(int pageNum, int pageSize, String name, Long categoryId, Integer status) {
        // 构建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 按商品名称模糊查询
        wrapper.like(name != null && !name.trim().isEmpty(), Product::getName, name)
               // 按分类ID精确查询
               .eq(categoryId != null, Product::getCategoryId, categoryId)
               // 按状态精确查询
               .eq(status != null, Product::getStatus, status)
               // 排除已删除的记录
               .eq(Product::getDeleted, 0)
               // 按创建时间降序排列
               .orderByDesc(Product::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public void addProduct(ProductFormDTO dto) {
        // 检查条码是否已存在
        long count = this.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getBarcode, dto.getBarcode())
                .eq(Product::getDeleted, 0));
        if (count > 0) {
            throw new RuntimeException("条码已存在: " + dto.getBarcode());
        }

        // 构建商品对象
        Product product = new Product();
        product.setCategoryId(dto.getCategoryId());
        product.setBarcode(dto.getBarcode());
        product.setName(dto.getName());
        product.setSpec(dto.getSpec());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setStock(dto.getStock());
        product.setLowStockThreshold(dto.getLowStockThreshold());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        product.setRemark(dto.getRemark());

        // 如果设置了生产日期和保质期天数，计算到期日期
        if (dto.getLatestProductionDate() != null && dto.getShelfLifeDays() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(dto.getLatestProductionDate());
            calendar.add(Calendar.DAY_OF_MONTH, dto.getShelfLifeDays());
            product.setEarliestExpirationDate(calendar.getTime());
        }

        // 保存到数据库
        this.save(product);
    }

    @Override
    public void updateProduct(ProductFormDTO dto) {
        // 检查商品是否存在
        Product product = this.getById(dto.getId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查条码是否与其他商品重复
        long count = this.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getBarcode, dto.getBarcode())
                .ne(Product::getId, dto.getId())  // 排除当前商品
                .eq(Product::getDeleted, 0));
        if (count > 0) {
            throw new RuntimeException("条码已存在: " + dto.getBarcode());
        }

        // 更新商品信息
        product.setCategoryId(dto.getCategoryId());
        product.setBarcode(dto.getBarcode());
        product.setName(dto.getName());
        product.setSpec(dto.getSpec());
        product.setUnit(dto.getUnit());
        product.setPrice(dto.getPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setStock(dto.getStock());
        product.setLowStockThreshold(dto.getLowStockThreshold());
        product.setStatus(dto.getStatus());
        product.setRemark(dto.getRemark());

        // 如果设置了生产日期和保质期天数，计算到期日期
        if (dto.getLatestProductionDate() != null && dto.getShelfLifeDays() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(dto.getLatestProductionDate());
            calendar.add(Calendar.DAY_OF_MONTH, dto.getShelfLifeDays());
            product.setEarliestExpirationDate(calendar.getTime());
        }

        // 更新到数据库
        this.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = this.getById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(status);
        this.updateById(product);
    }

    @Override
    public List<Product> getExpiringSoonProducts(Date expiringDate) {
        // 查询到期日期小于等于指定日期，且大于当前日期的商品（即即将过期但未过期的商品）
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        Date currentDate = calendar.getTime();
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Product::getEarliestExpirationDate)  // 到期日期不为空
               .le(Product::getEarliestExpirationDate, expiringDate)  // 到期日期 <= 指定日期
               .ge(Product::getEarliestExpirationDate, currentDate)   // 到期日期 >= 当前日期
               .eq(Product::getDeleted, 0)                          // 未删除
               .gt(Product::getStock, 0);                           // 库存大于0
        
        return this.list(wrapper);
    }

    @Override
    public List<Product> getExpiredProducts() {
        // 查询到期日期小于当前日期的商品（即已过期的商品）
        Date currentDate = new Date();
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Product::getEarliestExpirationDate)  // 到期日期不为空
               .lt(Product::getEarliestExpirationDate, currentDate)  // 到期日期 < 当前日期
               .eq(Product::getDeleted, 0)                          // 未删除
               .gt(Product::getStock, 0);                           // 库存大于0
        
        return this.list(wrapper);
    }
}