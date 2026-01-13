package com.supermarket.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.inventory.entity.InventoryCount;
import com.supermarket.inventory.entity.InventoryCountDetail;

import java.util.List;

/**
 * 库存盘点服务接口
 * 负责库存盘点相关的业务逻辑
 */
public interface InventoryCountService extends IService<InventoryCount> {

    /**
     * 分页查询盘点列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param title 盘点标题
     * @param status 状态
     * @return 分页结果
     */
    IPage<InventoryCount> queryPage(int pageNum, int pageSize, String title, String status);

    /**
     * 创建新的盘点任务
     * @param inventoryCount 盘点信息
     * @return 盘点ID
     */
    Long createInventoryCount(InventoryCount inventoryCount);

    /**
     * 开始盘点
     * @param countId 盘点ID
     */
    void startCount(Long countId);

    /**
     * 完成盘点
     * @param countId 盘点ID
     */
    void completeCount(Long countId);

    /**
     * 取消盘点
     * @param countId 盘点ID
     */
    void cancelCount(Long countId);

    /**
     * 获取盘点详情列表
     * @param countId 盘点ID
     * @return 盘点详情列表
     */
    List<InventoryCountDetail> getDetailsByCountId(Long countId);

    /**
     * 添加盘点详情
     * @param detail 盘点详情
     */
    void addDetail(InventoryCountDetail detail);

    /**
     * 更新盘点详情
     * @param detail 盘点详情
     */
    void updateDetail(InventoryCountDetail detail);

    /**
     * 根据盘点ID删除所有详情
     * @param countId 盘点ID
     */
    void deleteDetailsByCountId(Long countId);

    /**
     * 计算盘点差异
     * @param countId 盘点ID
     */
    void calculateDiscrepancies(Long countId);
}