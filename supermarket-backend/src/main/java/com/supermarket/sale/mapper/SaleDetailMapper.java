package com.supermarket.sale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.sale.entity.SaleDetail;
import com.supermarket.sale.vo.ProductSaleRecordVO;
import com.supermarket.sale.vo.ProductSaleSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 销售明细 Mapper
 * 对应表: sale_detail
 */
@Mapper
public interface SaleDetailMapper extends BaseMapper<SaleDetail> {

    Page<ProductSaleRecordVO> selectProductSaleRecords(Page<ProductSaleRecordVO> page,
                                                       @Param("productName") String productName,
                                                       @Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime);

    List<ProductSaleRecordVO> selectProductSaleRecords(@Param("productName") String productName,
                                                       @Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime);

    List<ProductSaleSummaryVO> selectProductSaleSummary(@Param("productName") String productName,
                                                        @Param("startTime") Date startTime,
                                                        @Param("endTime") Date endTime);
}