package com.supermarket.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.inventory.service.RestockSuggestionService;
import com.supermarket.inventory.vo.RestockSuggestionVO;
import com.supermarket.product.entity.Product;
import com.supermarket.product.mapper.ProductMapper;
import com.supermarket.sale.entity.SaleDetail;
import com.supermarket.sale.entity.SaleOrder;
import com.supermarket.sale.mapper.SaleDetailMapper;
import com.supermarket.sale.mapper.SaleOrderMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestockSuggestionServiceImpl implements RestockSuggestionService {

    private final ProductMapper productMapper;
    private final SaleDetailMapper saleDetailMapper;
    private final SaleOrderMapper saleOrderMapper;

    public RestockSuggestionServiceImpl(ProductMapper productMapper,
                                        SaleDetailMapper saleDetailMapper,
                                        SaleOrderMapper saleOrderMapper) {
        this.productMapper = productMapper;
        this.saleDetailMapper = saleDetailMapper;
        this.saleOrderMapper = saleOrderMapper;
    }

    @Override
    public List<RestockSuggestionVO> getRestockSuggestions(Integer restockDays) {
        if (restockDays == null || restockDays <= 0) {
            restockDays = 7;
        }

        List<Product> allProducts = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .eq(Product::getDeleted, 0)
        );

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateTime = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        List<SaleOrder> recentOrders = saleOrderMapper.selectList(
                new LambdaQueryWrapper<SaleOrder>()
                        .ge(SaleOrder::getCreateTime, startDateTime)
                        .le(SaleOrder::getCreateTime, endDateTime)
                        .eq(SaleOrder::getStatus, 1)
        );

        List<Long> orderIds = recentOrders.stream()
                .map(SaleOrder::getId)
                .collect(Collectors.toList());

        Map<Long, Integer> salesMap = new HashMap<>();
        if (!orderIds.isEmpty()) {
            List<SaleDetail> recentSales = saleDetailMapper.selectList(
                    new LambdaQueryWrapper<SaleDetail>()
                            .in(SaleDetail::getOrderId, orderIds)
            );

            for (SaleDetail detail : recentSales) {
                salesMap.merge(detail.getProductId(), detail.getQuantity(), Integer::sum);
            }
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) {
            totalDays = 1;
        }

        List<RestockSuggestionVO> suggestions = new ArrayList<>();

        for (Product product : allProducts) {
            Integer totalSold = salesMap.getOrDefault(product.getId(), 0);
            BigDecimal avgDailySales = new BigDecimal(totalSold)
                    .divide(new BigDecimal(totalDays), 2, RoundingMode.HALF_UP);

            if (avgDailySales.compareTo(BigDecimal.ZERO) <= 0 &&
                    product.getStock() > (product.getLowStockThreshold() != null ? product.getLowStockThreshold() : 0)) {
                continue;
            }

            RestockSuggestionVO vo = new RestockSuggestionVO();
            vo.setProductId(product.getId());
            vo.setProductName(product.getName());
            vo.setBarcode(product.getBarcode());
            vo.setSpec(product.getSpec());
            vo.setUnit(product.getUnit());
            vo.setCurrentStock(product.getStock());
            vo.setLowStockThreshold(product.getLowStockThreshold());
            vo.setAvgDailySales(avgDailySales);

            int threshold = product.getLowStockThreshold() != null ? product.getLowStockThreshold() : 0;
            int stockNeededForDays = avgDailySales.multiply(new BigDecimal(restockDays)).intValue();
            int suggestedQuantity = Math.max(0, stockNeededForDays + threshold - product.getStock());

            vo.setSuggestedQuantity(suggestedQuantity);
            vo.setRestockDays(restockDays);

            if (product.getStock() <= threshold) {
                vo.setUrgencyLevel("URGENT");
            } else if (product.getStock() <= threshold * 1.5) {
                vo.setUrgencyLevel("WARNING");
            } else {
                vo.setUrgencyLevel("NORMAL");
            }

            if (suggestedQuantity > 0) {
                suggestions.add(vo);
            }
        }

        suggestions.sort((a, b) -> {
            int urgencyCompare = compareUrgency(a.getUrgencyLevel(), b.getUrgencyLevel());
            if (urgencyCompare != 0) {
                return urgencyCompare;
            }
            return b.getSuggestedQuantity().compareTo(a.getSuggestedQuantity());
        });

        return suggestions;
    }

    private int compareUrgency(String levelA, String levelB) {
        int priorityA = getUrgencyPriority(levelA);
        int priorityB = getUrgencyPriority(levelB);
        return priorityA - priorityB;
    }

    private int getUrgencyPriority(String level) {
        if ("URGENT".equals(level)) {
            return 1;
        } else if ("WARNING".equals(level)) {
            return 2;
        } else {
            return 3;
        }
    }
}
