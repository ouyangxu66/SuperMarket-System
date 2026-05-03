package com.supermarket.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermarket.common.result.Result;
import com.supermarket.inventory.entity.InventoryCount;
import com.supermarket.inventory.entity.InventoryCountDetail;
import com.supermarket.inventory.service.InventoryCountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存盘点控制器
 * 提供库存盘点相关的API接口
 */
@RestController
@RequestMapping("/inventory/count")
public class InventoryCountController {

    private final InventoryCountService inventoryCountService;

    // 构造器注入
    public InventoryCountController(InventoryCountService inventoryCountService) {
        this.inventoryCountService = inventoryCountService;
    }

    /**
     * 分页查询盘点列表
     * GET /inventory/count/page?pageNum=1&pageSize=10&title=盘点任务&status=DRAFT
     */
    @GetMapping("/page")
    public Result<IPage<InventoryCount>> getInventoryCountPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {

        IPage<InventoryCount> pageResult = inventoryCountService.queryPage(pageNum, pageSize, title, status);
        return Result.success(pageResult);
    }

    /**
     * 获取盘点详情
     * GET /inventory/count/{id}
     */
    @GetMapping("/{id:[0-9]+}")
    public Result<InventoryCount> getInventoryCount(@PathVariable("id") Long id) {
        InventoryCount inventoryCount = inventoryCountService.getById(id);
        if (inventoryCount == null) {
            return Result.error("盘点任务不存在");
        }
        return Result.success(inventoryCount);
    }

    /**
     * 创建盘点任务
     * POST /inventory/count
     */
    @PostMapping
    public Result<Long> createInventoryCount(@RequestBody InventoryCount inventoryCount) {
        Long countId = inventoryCountService.createInventoryCount(inventoryCount);
        return Result.success(countId);
    }

    /**
     * 开始盘点
     * PUT /inventory/count/{id}/start
     */
    @PutMapping("/{id:[0-9]+}/start")
    public Result<String> startCount(@PathVariable("id") Long id) {
        try {
            inventoryCountService.startCount(id);
            return Result.success("开始盘点成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 完成盘点
     * PUT /inventory/count/{id}/complete
     */
    @PutMapping("/{id:[0-9]+}/complete")
    public Result<String> completeCount(@PathVariable("id") Long id) {
        try {
            inventoryCountService.completeCount(id);
            return Result.success("完成盘点成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消盘点
     * PUT /inventory/count/{id}/cancel
     */
    @PutMapping("/{id:[0-9]+}/cancel")
    public Result<String> cancelCount(@PathVariable("id") Long id) {
        try {
            inventoryCountService.cancelCount(id);
            return Result.success("取消盘点成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取盘点详情列表
     * GET /inventory/count/{id}/details
     */
    @GetMapping("/{id:[0-9]+}/details")
    public Result<List<InventoryCountDetail>> getDetails(@PathVariable("id") Long id) {
        List<InventoryCountDetail> details = inventoryCountService.getDetailsByCountId(id);
        return Result.success(details);
    }

    /**
     * 添加盘点详情
     * POST /inventory/count/{countId}/detail
     */
    @PostMapping("/{countId:[0-9]+}/detail")
    public Result<String> addDetail(@PathVariable("countId") Long countId, @RequestBody InventoryCountDetail detail) {
        detail.setCountId(countId);
        inventoryCountService.addDetail(detail);
        return Result.success("添加盘点详情成功");
    }

    /**
     * 更新盘点详情
     * PUT /inventory/count/detail
     */
    @PutMapping("/detail")
    public Result<String> updateDetail(@RequestBody InventoryCountDetail detail) {
        inventoryCountService.updateDetail(detail);
        return Result.success("更新盘点详情成功");
    }

    /**
     * 删除盘点详情
     * DELETE /inventory/count/detail/{id}
     */
    @DeleteMapping("/detail/{id:[0-9]+}")
    public Result<String> deleteDetail(@PathVariable("id") Long id) {
        InventoryCountDetail detail = new InventoryCountDetail();
        detail.setId(id);
        detail.setDeleted(1); // 逻辑删除
        inventoryCountService.updateDetail(detail);
        return Result.success("删除盘点详情成功");
    }
    /**
     * 导出盘点报告
     * GET /inventory/count/{id}/export
     */
    @GetMapping("/{id:[0-9]+}/export")
    public void exportCountReport(@PathVariable("id") Long id, javax.servlet.http.HttpServletResponse response) {
        inventoryCountService.exportCountReport(response, id);
    }
}