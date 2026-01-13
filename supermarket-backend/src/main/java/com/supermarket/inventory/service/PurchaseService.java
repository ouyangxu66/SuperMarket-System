package com.supermarket.inventory.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.inventory.dto.PurchaseBillDTO;
import com.supermarket.inventory.entity.PurchaseBill;

/**
 * 进货单服务接口
 */
public interface PurchaseService extends IService<PurchaseBill> {

    /**
     * 创建进货单 (草稿状态)
     */
    void createPurchaseBill(PurchaseBillDTO dto);

    /**
     * 审核入库 (执行库存变更)
     * @param billId 单据ID
     */
    void auditPurchaseBill(Long billId);

    /**
     * 作废单据 (仅限待审核状态)
     * @param billId 单据ID
     */
    void voidPurchaseBill(Long billId);
}