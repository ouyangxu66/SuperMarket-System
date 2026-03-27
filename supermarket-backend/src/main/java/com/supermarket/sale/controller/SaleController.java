package com.supermarket.sale.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.result.Result;
import com.supermarket.sale.dto.SaleFormDTO;
import com.supermarket.sale.entity.SaleDetail;
import com.supermarket.sale.entity.SaleOrder;
import com.supermarket.sale.mapper.SaleDetailMapper;
import com.supermarket.sale.service.SaleService;
import com.supermarket.sale.vo.SaleOrderExcelVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 销售相关接口
 */
@RestController
@RequestMapping("/sale")
public class SaleController {

    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);

    private final SaleService saleService;
    private final SaleDetailMapper saleDetailMapper;

    public SaleController(SaleService saleService, SaleDetailMapper saleDetailMapper) {
        this.saleService = saleService;
        this.saleDetailMapper = saleDetailMapper;
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
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) Integer paymentType,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {

        Page<SaleOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(orderNo), SaleOrder::getOrderNo, orderNo)
                .like(StringUtils.hasText(memberName), SaleOrder::getMemberName, memberName)
                .eq(paymentType != null, SaleOrder::getPaymentType, paymentType)
                .ge(startTime != null, SaleOrder::getCreateTime, startTime == null ? null : new Date(startTime))
                .le(endTime != null, SaleOrder::getCreateTime, endTime == null ? null : new Date(endTime))
                .orderByDesc(SaleOrder::getCreateTime);

        return Result.success(saleService.page(page, wrapper));
    }

    /**
     * 获取销售订单详情
     */
    @GetMapping("/{id}")
    public Result<SaleOrder> detail(@PathVariable Long id) {
        SaleOrder order = saleService.getById(id);
        if (order == null || (order.getDeleted() != null && order.getDeleted() == 1)) {
            return Result.error("销售订单不存在");
        }
        List<SaleDetail> details = saleDetailMapper.selectList(new LambdaQueryWrapper<SaleDetail>()
                .eq(SaleDetail::getOrderId, id));
        order.setItems(details);
        return Result.success(order);
    }

    /**
     * 导出销售流水
     * GET /api/sale/export
     */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) Integer paymentType,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            HttpServletResponse response) {

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = java.net.URLEncoder.encode("销售流水_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            List<SaleOrder> sales = saleService.list(new LambdaQueryWrapper<SaleOrder>()
                    .like(StringUtils.hasText(orderNo), SaleOrder::getOrderNo, orderNo)
                    .like(StringUtils.hasText(memberName), SaleOrder::getMemberName, memberName)
                    .eq(paymentType != null, SaleOrder::getPaymentType, paymentType)
                    .ge(startTime != null, SaleOrder::getCreateTime, startTime == null ? null : new Date(startTime))
                    .le(endTime != null, SaleOrder::getCreateTime, endTime == null ? null : new Date(endTime))
                    .orderByDesc(SaleOrder::getCreateTime));

            List<SaleOrderExcelVO> excelList = new ArrayList<>();
            for (SaleOrder order : sales) {
                SaleOrderExcelVO vo = new SaleOrderExcelVO();
                vo.setOrderNo(order.getOrderNo());
                vo.setTotalAmount(order.getTotalAmount());
                vo.setRealAmount(order.getRealAmount());
                vo.setMemberName(order.getMemberName());
                vo.setMemberPhone(order.getMemberPhone());
                vo.setPointEarned(order.getPointEarned());
                vo.setCreateTime(order.getCreateTime());

                String method = "未知";
                if (order.getPaymentType() != null) {
                    switch (order.getPaymentType()) {
                        case 1:
                            method = "现金";
                            break;
                        case 2:
                            method = "微信";
                            break;
                        case 3:
                            method = "支付宝";
                            break;
                        default:
                            method = "其他";
                    }
                }
                vo.setPaymentMethod(method);
                excelList.add(vo);
            }

            EasyExcel.write(response.getOutputStream(), SaleOrderExcelVO.class)
                    .sheet("销售流水")
                    .doWrite(excelList);

        } catch (Exception e) {
            logger.error("导出销售流水失败", e);
        }
    }
}