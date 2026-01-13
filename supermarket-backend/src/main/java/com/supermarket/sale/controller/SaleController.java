package com.supermarket.sale.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.result.Result;
import com.supermarket.sale.dto.SaleFormDTO;
import com.supermarket.sale.entity.SaleOrder;
import com.supermarket.sale.service.SaleService;
import org.springframework.web.bind.annotation.*;

/*
 * 销售相关接口
 */
@RestController
@RequestMapping("/api/sale")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * 收银台结算
     * POST /api/sale/checkout
     */
    @PostMapping("/checkout")
    public Result<String> checkout(@RequestBody SaleFormDTO dto) {
        String orderNo = saleService.checkout(dto);
        return Result.success(orderNo); // 返回订单号给前端打印小票
    }

    /**
     * 销售流水查询
     * GET /api/sale/page
     */
    @GetMapping("/page")
    public Result<IPage<SaleOrder>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<SaleOrder> page = new Page<>(pageNum, pageSize);
        // 按时间倒序
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SaleOrder::getCreateTime);

        return Result.success(saleService.page(page, wrapper));
    }
}