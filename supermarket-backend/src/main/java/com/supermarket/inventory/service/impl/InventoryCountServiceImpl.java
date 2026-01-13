package com.supermarket.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.inventory.entity.InventoryCount;
import com.supermarket.inventory.entity.InventoryCountDetail;
import com.supermarket.inventory.mapper.InventoryCountDetailMapper;
import com.supermarket.inventory.mapper.InventoryCountMapper;
import com.supermarket.inventory.service.InventoryCountService;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 库存盘点服务实现类
 * 负责库存盘点相关的业务逻辑
 */
@Service
public class InventoryCountServiceImpl extends ServiceImpl<InventoryCountMapper, InventoryCount> implements InventoryCountService {

    private final InventoryCountDetailMapper inventoryCountDetailMapper;
    private final ProductService productService;

    // 构造器注入
    public InventoryCountServiceImpl(InventoryCountDetailMapper inventoryCountDetailMapper, ProductService productService) {
        this.inventoryCountDetailMapper = inventoryCountDetailMapper;
        this.productService = productService;
    }

    @Override
    public IPage<InventoryCount> queryPage(int pageNum, int pageSize, String title, String status) {
        // 构建分页对象
        Page<InventoryCount> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        LambdaQueryWrapper<InventoryCount> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null && !title.trim().isEmpty(), InventoryCount::getTitle, title)
               .eq(status != null && !status.trim().isEmpty(), InventoryCount::getStatus, status)
               .eq(InventoryCount::getDeleted, 0)
               .orderByDesc(InventoryCount::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInventoryCount(InventoryCount inventoryCount) {
        // 设置初始状态和编号
        if (inventoryCount.getStatus() == null) {
            inventoryCount.setStatus("DRAFT"); // 草稿状态
        }
        
        if (inventoryCount.getCountNumber() == null || inventoryCount.getCountNumber().trim().isEmpty()) {
            // 生成盘点编号，格式：PC + 年月日 + 4位流水号
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            String dateStr = String.format("%d%02d%02d", 
                                          calendar.get(Calendar.YEAR),
                                          calendar.get(Calendar.MONTH) + 1,
                                          calendar.get(Calendar.DAY_OF_MONTH));
            String serialNumber = String.format("%04d", (int)(Math.random() * 10000));
            inventoryCount.setCountNumber("PC" + dateStr + serialNumber);
        }
        
        // 保存盘点主表
        this.save(inventoryCount);
        
        // 返回盘点ID
        return inventoryCount.getId();
    }

    @Override
    public void startCount(Long countId) {
        InventoryCount inventoryCount = this.getById(countId);
        if (inventoryCount == null) {
            throw new RuntimeException("盘点任务不存在");
        }
        
        if (!"DRAFT".equals(inventoryCount.getStatus())) {
            throw new RuntimeException("只有草稿状态的盘点任务才能开始");
        }
        
        // 更新状态为进行中
        inventoryCount.setStatus("IN_PROGRESS");
        inventoryCount.setStartTime(new Date());
        this.updateById(inventoryCount);
    }

    @Override
    public void completeCount(Long countId) {
        InventoryCount inventoryCount = this.getById(countId);
        if (inventoryCount == null) {
            throw new RuntimeException("盘点任务不存在");
        }
        
        if (!"IN_PROGRESS".equals(inventoryCount.getStatus())) {
            throw new RuntimeException("只有进行中的盘点任务才能完成");
        }
        
        // 计算差异
        calculateDiscrepancies(countId);
        
        // 更新状态为已完成
        inventoryCount.setStatus("COMPLETED");
        inventoryCount.setEndTime(new Date());
        this.updateById(inventoryCount);
    }

    @Override
    public void cancelCount(Long countId) {
        InventoryCount inventoryCount = this.getById(countId);
        if (inventoryCount == null) {
            throw new RuntimeException("盘点任务不存在");
        }
        
        // 更新状态为已取消
        inventoryCount.setStatus("CANCELLED");
        this.updateById(inventoryCount);
    }

    @Override
    public List<InventoryCountDetail> getDetailsByCountId(Long countId) {
        LambdaQueryWrapper<InventoryCountDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryCountDetail::getCountId, countId)
               .eq(InventoryCountDetail::getDeleted, 0)
               .orderByAsc(InventoryCountDetail::getCreateTime);
        
        return inventoryCountDetailMapper.selectList(wrapper);
    }

    @Override
    public void addDetail(InventoryCountDetail detail) {
        // 设置初始状态
        if (detail.getStatus() == null) {
            // 计算差异数量
            Integer diff = (detail.getActualStock() != null ? detail.getActualStock() : 0) - 
                          (detail.getSystemStock() != null ? detail.getSystemStock() : 0);
            detail.setDifference(diff);
            
            // 根据差异数量设置状态
            detail.setStatus(diff != 0 ? "DISCREPANCY" : "NORMAL");
        }
        
        inventoryCountDetailMapper.insert(detail);
    }

    @Override
    public void updateDetail(InventoryCountDetail detail) {
        // 计算差异数量
        Integer diff = (detail.getActualStock() != null ? detail.getActualStock() : 0) - 
                      (detail.getSystemStock() != null ? detail.getSystemStock() : 0);
        detail.setDifference(diff);
        
        // 根据差异数量设置状态
        detail.setStatus(diff != 0 ? "DISCREPANCY" : "NORMAL");
        
        inventoryCountDetailMapper.updateById(detail);
    }

    @Override
    public void deleteDetailsByCountId(Long countId) {
        LambdaQueryWrapper<InventoryCountDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryCountDetail::getCountId, countId);
        
        inventoryCountDetailMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateDiscrepancies(Long countId) {
        // 获取盘点详情列表
        List<InventoryCountDetail> details = getDetailsByCountId(countId);
        
        int discrepancyCount = 0;
        for (InventoryCountDetail detail : details) {
            // 计算差异数量
            Integer diff = (detail.getActualStock() != null ? detail.getActualStock() : 0) - 
                          (detail.getSystemStock() != null ? detail.getSystemStock() : 0);
            detail.setDifference(diff);
            
            // 根据差异数量设置状态
            String status = diff != 0 ? "DISCREPANCY" : "NORMAL";
            detail.setStatus(status);
            
            // 如果有差异，增加计数
            if ("DISCREPANCY".equals(status)) {
                discrepancyCount++;
            }
            
            // 更新盘点详情
            inventoryCountDetailMapper.updateById(detail);
            
            // 如果实际库存与系统库存不同，更新商品表中的库存
            if (!diff.equals(0)) {
                Product product = productService.getById(detail.getProductId());
                if (product != null) {
                    product.setStock(detail.getActualStock());
                    productService.updateById(product);
                }
            }
        }
        
        // 更新盘点主表的差异数量
        InventoryCount inventoryCount = this.getById(countId);
        inventoryCount.setDiscrepancyCount(discrepancyCount);
        this.updateById(inventoryCount);
    }
}