package com.supermarket.product.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.service.ProductService;
import com.supermarket.product.vo.ProductImportExcelVO;

import java.util.ArrayList;
import java.util.List;

public class ProductImportListener implements ReadListener<ProductImportExcelVO> {

    private final ProductService productService;
    private int successCount = 0;
    private int failCount = 0;
    private final List<String> errorMsgs = new ArrayList<>();

    public ProductImportListener(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void invoke(ProductImportExcelVO data, AnalysisContext context) {
        try {
            // 基本必填检查
            if (data.getName() == null || data.getBarcode() == null || data.getCategoryId() == null ||
                data.getUnit() == null || data.getPrice() == null || data.getCostPrice() == null ||
                data.getStock() == null || data.getLowStockThreshold() == null || data.getStatus() == null) {
                failCount++;
                errorMsgs.add("第" + context.readRowHolder().getRowIndex() + "行: 必填字段缺失");
                return;
            }

            ProductFormDTO dto = new ProductFormDTO();
            dto.setName(data.getName());
            dto.setBarcode(data.getBarcode());
            dto.setCategoryId(data.getCategoryId());
            dto.setSpec(data.getSpec());
            dto.setUnit(data.getUnit());
            dto.setPrice(data.getPrice());
            dto.setCostPrice(data.getCostPrice());
            dto.setStock(data.getStock());
            dto.setLowStockThreshold(data.getLowStockThreshold());
            dto.setStatus(data.getStatus());
            dto.setLatestProductionDate(data.getLatestProductionDate());
            dto.setShelfLifeDays(data.getShelfLifeDays());
            dto.setRemark(data.getRemark());

            productService.addProduct(dto);
            successCount++;
        } catch (Exception e) {
            failCount++;
            errorMsgs.add("第" + context.readRowHolder().getRowIndex() + "行: " + e.getMessage());
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 解析完成
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public List<String> getErrorMsgs() {
        return errorMsgs;
    }
}
