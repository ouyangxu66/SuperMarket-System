package com.supermarket.inventory.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.result.Result;
import com.supermarket.inventory.entity.Supplier;
import com.supermarket.inventory.service.SupplierService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供应商管理控制器
 */
@RestController
@RequestMapping("/inventory/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    // 构造器注入
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    /**
     * 分页查询
     * GET /inventory/supplier/page?name=可口
     */
    @GetMapping("/page")
    public Result<IPage<Supplier>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name) {

        Page<Supplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Supplier::getName, name);
        wrapper.orderByDesc(Supplier::getCreateTime);

        return Result.success(supplierService.page(page, wrapper));
    }

    /**
     * 获取所有可用供应商列表 (用于下拉框选择)
     * GET /inventory/supplier/list
     */
    @GetMapping("/list")
    public Result<List<Supplier>> list() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, 1); // 仅查询启用状态的
        wrapper.orderByDesc(Supplier::getId);
        return Result.success(supplierService.list(wrapper));
    }

    /**
     * 新增供应商
     * POST /inventory/supplier
     */
    @PostMapping
    public Result<?> add(@RequestBody Supplier supplier) {
        boolean success = supplierService.save(supplier);
        return success ? Result.success("新增成功") : Result.error("新增失败");
    }

    /**
     * 修改供应商
     * PUT /inventory/supplier
     */
    @PutMapping
    public Result<?> update(@RequestBody Supplier supplier) {
        boolean success = supplierService.updateById(supplier);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除供应商
     * DELETE /inventory/supplier/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = supplierService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}