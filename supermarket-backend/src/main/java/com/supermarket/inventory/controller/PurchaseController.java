package com.supermarket.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.supermarket.common.result.Result;

import com.supermarket.inventory.dto.PurchaseBillDTO;
import com.supermarket.inventory.entity.PurchaseBill;
import com.supermarket.inventory.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

/**
 * 进货单管理控制器
 */
@RestController
@RequestMapping("/api/store/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /**
     * 分页查询进货单
     */
    @GetMapping("/page")
    public Result<IPage<PurchaseBill>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {

        Page<PurchaseBill> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseBill> wrapper = new LambdaQueryWrapper<>();

        // 动态查询
        if (status != null) {
            wrapper.eq(PurchaseBill::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseBill::getCreateTime);

        return Result.success(purchaseService.page(page, wrapper));
    }

    /**
     * 创建进货单
     */
    @PostMapping
    public Result<?> create(@RequestBody PurchaseBillDTO dto) {
        purchaseService.createPurchaseBill(dto);
        return Result.success("进货单开单成功");
    }

    /**
     * 审核入库
     */
    @PostMapping("/{id}/audit")
    public Result<?> audit(@PathVariable Long id) {
        purchaseService.auditPurchaseBill(id);
        return Result.success("审核通过，库存已更新");
    }

    /**
     * 作废单据
     */
    @PostMapping("/{id}/void")
    public Result<?> voidBill(@PathVariable Long id) {
        purchaseService.voidPurchaseBill(id);
        return Result.success("单据已作废");
    }
}