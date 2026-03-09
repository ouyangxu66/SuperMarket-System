package com.supermarket.dashboard.mapper;

import com.supermarket.dashboard.vo.DashboardOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DashboardMapper {

    DashboardOverviewVO.SalesSummary selectSalesSummary(@Param("todayStart") Date todayStart,
                                                        @Param("todayEnd") Date todayEnd,
                                                        @Param("yesterdayStart") Date yesterdayStart,
                                                        @Param("yesterdayEnd") Date yesterdayEnd);

    List<DashboardOverviewVO.SalesTrendItem> selectSalesTrend(@Param("startTime") Date startTime,
                                                              @Param("endTime") Date endTime);

    List<DashboardOverviewVO.HotProductItem> selectHotProducts(@Param("startTime") Date startTime,
                                                               @Param("endTime") Date endTime,
                                                               @Param("limit") Integer limit);

    List<DashboardOverviewVO.PaymentDistributionItem> selectPaymentDistribution(@Param("startTime") Date startTime,
                                                                                @Param("endTime") Date endTime);

    DashboardOverviewVO.MemberSummary selectMemberSummary(@Param("todayStart") Date todayStart,
                                                          @Param("todayEnd") Date todayEnd,
                                                          @Param("weekStart") Date weekStart,
                                                          @Param("activeSince") Date activeSince);

    List<DashboardOverviewVO.MemberTrendItem> selectMemberTrend(@Param("startTime") Date startTime,
                                                                @Param("endTime") Date endTime);

    DashboardOverviewVO.InventorySummary selectInventorySummary(@Param("today") Date today,
                                                                @Param("nearExpiryEnd") Date nearExpiryEnd);

    List<DashboardOverviewVO.LowStockItem> selectLowStockList(@Param("limit") Integer limit);

    List<DashboardOverviewVO.NearExpiryItem> selectNearExpiryList(@Param("today") Date today,
                                                                  @Param("nearExpiryEnd") Date nearExpiryEnd,
                                                                  @Param("limit") Integer limit);
}
