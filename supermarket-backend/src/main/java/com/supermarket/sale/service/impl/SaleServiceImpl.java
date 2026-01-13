package com.supermarket.sale.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;
import com.supermarket.sale.dto.SaleFormDTO;
import com.supermarket.sale.entity.SaleDetail;
import com.supermarket.sale.entity.SaleOrder;
import com.supermarket.sale.mapper.SaleDetailMapper;
import com.supermarket.sale.mapper.SaleOrderMapper;
import com.supermarket.sale.service.SaleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 销售单 Service 实现类
 */
@Service
public class SaleServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleService {

    private final SaleDetailMapper saleDetailMapper; // 使用 SaleDetailMapper
    private final ProductService productService;

    public SaleServiceImpl(SaleDetailMapper saleDetailMapper, ProductService productService) {
        this.saleDetailMapper = saleDetailMapper;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkout(SaleFormDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("购物车不能为空");
        }

        // 1. 创建销售主单
        SaleOrder order = new SaleOrder();
        // 生成订单号: XS + 时间 + 随机数
        String orderNo = "XS" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + (int)((Math.random() * 9 + 1) * 1000);
        order.setOrderNo(orderNo);
        order.setPaymentType(dto.getPaymentType());
        order.setCashierId(1L); // TODO: 从 Token 获取当前登录用户 ID
        order.setStatus(1); // 已支付
        order.setRemark(dto.getRemark());

        // 暂时先存主单 (为了拿 ID)
        this.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. 处理明细 & 扣减库存
        for (SaleFormDTO.ItemDTO itemDTO : dto.getItems()) {
            Product product = productService.getById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在 ID:" + itemDTO.getProductId());
            }

            // 检查库存
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName()
                        + ", 当前库存:" + product.getStock());
            }

            // 扣减库存
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productService.updateById(product);

            // 创建明细记录 (使用 SaleDetail)
            SaleDetail detail = new SaleDetail(); // <--- 修正点：使用 SaleDetail
            detail.setOrderId(order.getId());
            detail.setProductId(product.getId());
            detail.setProductName(product.getName()); // 快照
            detail.setPrice(product.getPrice());      // 快照售价
            detail.setQuantity(itemDTO.getQuantity());

            // 计算小计
            BigDecimal amount = product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            detail.setAmount(amount);

            totalAmount = totalAmount.add(amount);

            // 保存明细
            saleDetailMapper.insert(detail);
        }

        // 3. 更新主单金额
        order.setTotalAmount(totalAmount);
        // 如果前端没传实付金额，默认等于应付金额
        order.setRealPayAmount(dto.getRealPayAmount() != null ? dto.getRealPayAmount() : totalAmount);

        this.updateById(order);

        return orderNo;
    }
}