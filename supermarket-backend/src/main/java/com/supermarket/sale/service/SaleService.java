package com.supermarket.sale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.sale.dto.SaleFormDTO;
import com.supermarket.sale.entity.SaleOrder;

/**
 * 销售单 Service
 */
public interface SaleService extends IService<SaleOrder> {

    /**
     * 收银台结账
     * @param dto 购物车数据
     * @return 订单号
     */
    String checkout(SaleFormDTO dto);
    /**
     * 退货处理
     * @param orderId 订单ID
     * @param reason 退货原因
     */
    void refund(Long orderId, String reason);
}