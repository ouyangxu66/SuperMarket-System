package com.supermarket.sale.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.exception.BusinessException;
import com.supermarket.member.dto.MemberPointChangeDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.service.MemberPointFlowService;
import com.supermarket.member.service.MemberService;
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
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 销售单 Service 实现类
 */
@Service
public class SaleServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleService {

    private final SaleDetailMapper saleDetailMapper;
    private final ProductService productService;
    private final MemberService memberService;
    private final MemberPointFlowService memberPointFlowService;

    public SaleServiceImpl(SaleDetailMapper saleDetailMapper,
                           ProductService productService,
                           MemberService memberService,
                           MemberPointFlowService memberPointFlowService) {
        this.saleDetailMapper = saleDetailMapper;
        this.productService = productService;
        this.memberService = memberService;
        this.memberPointFlowService = memberPointFlowService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String checkout(SaleFormDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("购物车不能为空");
        }

        Member member = null;
        if (dto.getMemberId() != null) {
            member = memberService.getById(dto.getMemberId());
            if (member == null || (member.getDeleted() != null && member.getDeleted() == 1)) {
                throw new BusinessException("会员不存在");
            }
            if (member.getStatus() != null && member.getStatus() == 0) {
                throw new BusinessException("会员已停用，无法绑定到销售订单");
            }
        }

        String orderNo = "XS" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + (int) ((Math.random() * 9 + 1) * 1000);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleDetail> details = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        for (SaleFormDTO.ItemDTO itemDTO : dto.getItems()) {
            Product product = productService.getById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在 ID:" + itemDTO.getProductId());
            }
            if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
                throw new BusinessException("商品数量必须大于0");
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName() + ", 当前库存:" + product.getStock());
            }

            SaleDetail detail = new SaleDetail();
            detail.setProductId(product.getId());
            detail.setProductName(product.getName());
            detail.setPrice(product.getPrice());
            detail.setQuantity(itemDTO.getQuantity());

            BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            detail.setAmount(amount);
            totalAmount = totalAmount.add(amount);
            details.add(detail);

            product.setStock(product.getStock() - itemDTO.getQuantity());
            productsToUpdate.add(product);
        }

        BigDecimal realAmount = dto.getRealPayAmount() != null ? dto.getRealPayAmount() : totalAmount;

        // 处理积分抵扣
        Integer usedPoints = 0;
        BigDecimal pointDeductAmount = BigDecimal.ZERO;
        if (member != null && Boolean.TRUE.equals(dto.getUsePoints()) && dto.getUsedPoints() != null && dto.getUsedPoints() > 0) {
            usedPoints = dto.getUsedPoints();
            if (member.getPoints() == null || member.getPoints() < usedPoints) {
                throw new BusinessException("会员积分不足");
            }
            if (usedPoints % 100 != 0) {
                throw new BusinessException("抵扣积分必须是100的整数倍");
            }
            // 100积分抵扣1元
            pointDeductAmount = new BigDecimal(usedPoints).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            // 实付金额不能小于0
            if (realAmount.compareTo(pointDeductAmount) < 0) {
                throw new BusinessException("抵扣金额不能大于实付金额");
            }
            realAmount = realAmount.subtract(pointDeductAmount);
        }

        realAmount = realAmount.setScale(2, RoundingMode.HALF_UP);
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        // ...这里去掉了不能为0的限制，如果纯积分抵扣为0元也是可以的
        if (realAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("实收金额不能小于0");
        }

        SaleOrder order = new SaleOrder();
        order.setOrderNo(orderNo);
        order.setPaymentType(dto.getPaymentType() != null ? dto.getPaymentType() : 1);
        order.setCashierId(1L);
        order.setStatus(1);
        order.setRemark(dto.getRemark());
        order.setPointEarned(0);
        order.setPointDeducted(usedPoints);
        order.setPointDeductAmount(pointDeductAmount);
        order.setTotalAmount(totalAmount);
        order.setRealAmount(realAmount);

        if (member != null) {
            order.setMemberId(member.getId());
            order.setMemberNo(member.getMemberNo());
            order.setMemberName(member.getName());
            order.setMemberPhone(member.getPhone());
        }

        this.save(order);

        for (Product product : productsToUpdate) {
            productService.updateById(product);
        }

        for (SaleDetail detail : details) {
            detail.setOrderId(order.getId());
            saleDetailMapper.insert(detail);
        }

        if (member != null) {
            int earnedPoints = realAmount.intValue();
            order.setPointEarned(earnedPoints);

            member.setTotalConsumeAmount((member.getTotalConsumeAmount() == null ? BigDecimal.ZERO : member.getTotalConsumeAmount()).add(realAmount));
            member.setTotalConsumeCount((member.getTotalConsumeCount() == null ? 0 : member.getTotalConsumeCount()) + 1);
            member.setLastConsumeTime(new Date());
            memberService.updateById(member);

            // 扣除使用的积分
            if (usedPoints > 0) {
                MemberPointChangeDTO deductDTO = new MemberPointChangeDTO();
                deductDTO.setMemberId(member.getId());
                deductDTO.setChangePoints(-usedPoints);
                deductDTO.setSource("消费抵扣");
                deductDTO.setRemark("订单号:" + orderNo);
                memberPointFlowService.adjust(deductDTO);
            }

            if (earnedPoints > 0) {
                MemberPointChangeDTO pointChangeDTO = new MemberPointChangeDTO();
                pointChangeDTO.setMemberId(member.getId());
                pointChangeDTO.setChangePoints(earnedPoints);
                pointChangeDTO.setSource("销售订单赠分");
                pointChangeDTO.setRemark("订单号:" + orderNo);
                memberPointFlowService.adjust(pointChangeDTO);
            }
            this.updateById(order);
        }

        return orderNo;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long orderId, String reason) {
        SaleOrder order = this.getById(orderId);
        if (order == null || (order.getDeleted() != null && order.getDeleted() == 1)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() == -1) {
            throw new BusinessException("订单已退款，不能重复退货");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态异常，无法退货");
        }

        List<SaleDetail> details = saleDetailMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SaleDetail>()
                        .eq(SaleDetail::getOrderId, orderId)
        );

        if (details == null || details.isEmpty()) {
            throw new BusinessException("订单明细不存在");
        }

        for (SaleDetail detail : details) {
            Product product = productService.getById(detail.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + detail.getQuantity());
                productService.updateById(product);
            }
        }

        if (order.getMemberId() != null) {
            Member member = memberService.getById(order.getMemberId());
            if (member != null) {
                if (order.getPointEarned() != null && order.getPointEarned() > 0) {
                    MemberPointChangeDTO deductDTO = new MemberPointChangeDTO();
                    deductDTO.setMemberId(member.getId());
                    deductDTO.setChangePoints(-order.getPointEarned());
                    deductDTO.setSource("退货扣回积分");
                    deductDTO.setRemark("订单号:" + order.getOrderNo() + ", 原因:" + (reason != null ? reason : ""));
                    memberPointFlowService.adjust(deductDTO);
                }

                if (order.getPointDeducted() != null && order.getPointDeducted() > 0) {
                    MemberPointChangeDTO restoreDTO = new MemberPointChangeDTO();
                    restoreDTO.setMemberId(member.getId());
                    restoreDTO.setChangePoints(order.getPointDeducted());
                    restoreDTO.setSource("退货返还积分");
                    restoreDTO.setRemark("订单号:" + order.getOrderNo() + ", 原因:" + (reason != null ? reason : ""));
                    memberPointFlowService.adjust(restoreDTO);
                }

                member.setTotalConsumeAmount(
                        (member.getTotalConsumeAmount() == null ? BigDecimal.ZERO : member.getTotalConsumeAmount())
                                .subtract(order.getRealAmount() != null ? order.getRealAmount() : BigDecimal.ZERO)
                );
                member.setTotalConsumeCount(
                        (member.getTotalConsumeCount() == null ? 0 : member.getTotalConsumeCount()) - 1
                );
                memberService.updateById(member);
            }
        }

        order.setStatus(-1);
        order.setRemark((order.getRemark() != null ? order.getRemark() + " | " : "") +
                "退货原因: " + (reason != null ? reason : "无"));
        this.updateById(order);
    }
}

