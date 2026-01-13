package com.supermarket.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.inventory.dto.PurchaseBillDTO;
import com.supermarket.inventory.entity.PurchaseBill;
import com.supermarket.inventory.entity.PurchaseBillItem;
import com.supermarket.inventory.service.PurchaseBillItemMapper;
import com.supermarket.inventory.service.PurchaseBillMapper;
import com.supermarket.inventory.service.PurchaseService;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 进货单服务实现类
 */
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseBillMapper, PurchaseBill> implements PurchaseService {

    private final PurchaseBillItemMapper itemMapper;
    private final ProductService productService;

    // 构造器注入
    public PurchaseServiceImpl(PurchaseBillItemMapper itemMapper, ProductService productService) {
        this.itemMapper = itemMapper;
        this.productService = productService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPurchaseBill(PurchaseBillDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("进货明细不能为空");
        }

        // 1. 构建主表对象
        PurchaseBill bill = new PurchaseBill();
        // 生成单号: PO + 时间戳 + 4位随机数
        String billNo = "PO" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + (int)((Math.random() * 9 + 1) * 1000);
        bill.setBillNo(billNo);
        bill.setSupplierId(dto.getSupplierId());
        bill.setOperatorId(1L); // TODO: 暂时硬编码为1，实际应从 SecurityContext 获取当前用户ID
        bill.setRemark(dto.getRemark());
        bill.setStatus(0); // 0: 待审核

        // 先保存主表 (为了获取 ID)
        this.save(bill);

        // 2. 处理明细 & 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseBillDTO.PurchaseItemDTO itemDTO : dto.getItems()) {
            // 校验商品
            Product product = productService.getById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在 ID:" + itemDTO.getProductId());
            }

            PurchaseBillItem item = new PurchaseBillItem();
            item.setBillId(bill.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName()); // 冗余存储名称
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(itemDTO.getPrice());

            // 计算小计: price * quantity
            BigDecimal amount = itemDTO.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
            item.setAmount(amount);

            // 累加总金额
            totalAmount = totalAmount.add(amount);

            itemMapper.insert(item);
        }

        // 3. 更新主表总金额
        bill.setTotalAmount(totalAmount);
        this.updateById(bill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPurchaseBill(Long billId) {
        // 1. 获取单据
        PurchaseBill bill = this.getById(billId);
        if (bill == null) throw new BusinessException("单据不存在");

        // 校验状态
        if (bill.getStatus() != 0) {
            throw new BusinessException("单据状态不正确，只有待审核单据才能审核");
        }

        // 2. 获取明细
        List<PurchaseBillItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseBillItem>().eq(PurchaseBillItem::getBillId, billId)
        );

        // 3. 核心：遍历明细，变更库存
        for (PurchaseBillItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                // 增加库存
                int newStock = product.getStock() + item.getQuantity();
                product.setStock(newStock);

                // 更新参考进价 (简单的覆盖策略)
                product.setCostPrice(item.getPrice());

                productService.updateById(product);
            }
        }

        // 4. 更新单据状态 -> 已入库
        bill.setStatus(1);
        this.updateById(bill);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidPurchaseBill(Long billId) {
        PurchaseBill bill = this.getById(billId);
        if (bill == null) throw new BusinessException("单据不存在");

        if (bill.getStatus() != 0) {
            throw new BusinessException("只有待审核的单据才能作废");
        }

        bill.setStatus(-1); // -1: 作废
        this.updateById(bill);
    }
}