package com.supermarket.product.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.entity.Product;
import com.supermarket.product.entity.ProductCategory;
import com.supermarket.product.mapper.ProductCategoryMapper;
import com.supermarket.product.mapper.ProductMapper;
import com.supermarket.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductCategoryMapper productCategoryMapper; // Inject mapper

    public ProductServiceImpl(ProductCategoryMapper productCategoryMapper) { // Add constructor
        this.productCategoryMapper = productCategoryMapper;
    }

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

        IPage<Product> productPage = this.page(page, wrapper);
        List<Product> records = productPage.getRecords();
        if (records != null && !records.isEmpty()) {
            List<Long> categoryIds = records.stream()
                .map(Product::getCategoryId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());

            if (!categoryIds.isEmpty()) {
                List<ProductCategory> categories = productCategoryMapper.selectBatchIds(categoryIds);
                Map<Long, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(ProductCategory::getId, ProductCategory::getName));

                records.forEach(p -> {
                    if (p.getCategoryId() != null) {
                        p.setCategoryName(categoryMap.get(p.getCategoryId()));
                    }
                });
            }
        }
        return productPage;
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
        product.setImageUrl(dto.getImageUrl());
        product.setShelfLifeDays(dto.getShelfLifeDays());
        product.setLatestProductionDate(dto.getLatestProductionDate());

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
        product.setImageUrl(dto.getImageUrl());
        product.setShelfLifeDays(dto.getShelfLifeDays());
        product.setLatestProductionDate(dto.getLatestProductionDate());

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

    @Override
    public void export(HttpServletResponse response, String name, Long categoryId, Integer status) {
        try {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(name != null && !name.trim().isEmpty(), Product::getName, name)
                   .eq(categoryId != null, Product::getCategoryId, categoryId)
                   .eq(status != null, Product::getStatus, status)
                   .eq(Product::getDeleted, 0)
                   .orderByDesc(Product::getCreateTime);

            List<Product> list = this.list(wrapper);

            // 获取分类名称映射
            List<Long> categoryIds = list.stream().map(Product::getCategoryId).distinct().collect(Collectors.toList());
            Map<Long, String> categoryMap = new java.util.HashMap<>();
            if (!categoryIds.isEmpty()) {
                List<ProductCategory> categories = productCategoryMapper.selectBatchIds(categoryIds);
                for (ProductCategory category : categories) {
                    categoryMap.put(category.getId(), category.getName());
                }
            }

            List<com.supermarket.product.vo.ProductExcelVO> excelList = new java.util.ArrayList<>();
            for (Product product : list) {
                com.supermarket.product.vo.ProductExcelVO vo = new com.supermarket.product.vo.ProductExcelVO();
                vo.setName(product.getName());
                vo.setBarcode(product.getBarcode());
                vo.setCategoryName(categoryMap.getOrDefault(product.getCategoryId(), "未知分类"));
                vo.setCostPrice(product.getCostPrice());
                vo.setPrice(product.getPrice());

                String statusStr = "未知";
                if (product.getStatus() != null) {
                    statusStr = product.getStatus() == 1 ? "上架" : "下架";
                }
                vo.setStatus(statusStr);

                excelList.add(vo);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("商品列表_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            EasyExcel.write(response.getOutputStream(), com.supermarket.product.vo.ProductExcelVO.class)
                    .sheet("商品列表")
                    .doWrite(excelList);

        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    @Override
    public void downloadImportTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("商品导入模板.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            EasyExcel.write(response.getOutputStream(), com.supermarket.product.vo.ProductImportExcelVO.class)
                    .sheet("模板")
                    .doWrite(java.util.Collections.emptyList());
        } catch (Exception e) {
            throw new RuntimeException("导出模板失败", e);
        }
    }

    @Override
    public String importProduct(MultipartFile file) {
        try {
            com.supermarket.product.listener.ProductImportListener listener = new com.supermarket.product.listener.ProductImportListener(this);
            EasyExcel.read(file.getInputStream(), com.supermarket.product.vo.ProductImportExcelVO.class, listener).sheet().doRead();

            StringBuilder sb = new StringBuilder();
            sb.append("成功导入 ").append(listener.getSuccessCount()).append(" 条，失败 ").append(listener.getFailCount()).append(" 条。");
            if (listener.getFailCount() > 0) {
                sb.append(" 失败详情: ");
                int limit = Math.min(5, listener.getErrorMsgs().size());
                for (int i = 0; i < limit; i++) {
                    sb.append(listener.getErrorMsgs().get(i)).append("; ");
                }
                if (listener.getErrorMsgs().size() > 5) {
                    sb.append("...");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("导入失败: " + e.getMessage());
        }
    }

    @Override
    public void physicalDeleteProduct(Long id) {
        baseMapper.physicalDeleteById(id);
    }
    @Override
    public boolean updateStockWithOptimisticLock(Long productId, Integer quantityToReduce, Integer expectedVersion) {
        // 使用 MyBatis Plus 的条件构造器进行乐观锁更新
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, productId)
                .eq(Product::getVersion, expectedVersion)  // 版本号匹配
                .setSql("stock = stock - " + quantityToReduce)  // 减少库存
                .set(Product::getVersion, expectedVersion + 1); // 版本号+1

        return this.update(updateWrapper);
    }
}
