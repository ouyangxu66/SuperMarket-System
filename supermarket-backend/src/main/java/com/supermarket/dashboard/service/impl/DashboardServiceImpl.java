package com.supermarket.dashboard.service.impl;

import com.supermarket.dashboard.mapper.DashboardMapper;
import com.supermarket.dashboard.service.DashboardService;
import com.supermarket.dashboard.vo.DashboardOverviewVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public DashboardOverviewVO getOverview(String rangeType, Integer topN, Integer nearExpiryDays) {
        int days = resolveRangeDays(rangeType);
        int safeTopN = resolveTopN(topN);
        int safeNearExpiryDays = resolveNearExpiryDays(nearExpiryDays);

        Date now = new Date();
        Date todayStart = startOfDay(now);
        Date todayEnd = endOfDay(now);
        Date yesterdayStart = addDays(todayStart, -1);
        Date yesterdayEnd = addMillis(todayStart, -1);
        Date weekStart = addDays(todayStart, -6);
        Date activeSince = addDays(todayStart, -30);
        Date trendStart = addDays(todayStart, -(days - 1));
        Date trendEnd = todayEnd;
        Date nearExpiryEnd = endOfDay(addDays(todayStart, safeNearExpiryDays));

        DashboardOverviewVO overview = new DashboardOverviewVO();

        DashboardOverviewVO.SalesSummary salesSummary = dashboardMapper.selectSalesSummary(todayStart, todayEnd, yesterdayStart, yesterdayEnd);
        if (salesSummary == null) {
            salesSummary = new DashboardOverviewVO.SalesSummary();
        }
        if (salesSummary.getTodayOrderCount() != null && salesSummary.getTodayOrderCount() > 0) {
            salesSummary.setTodayAvgOrderAmount(
                    safeDivide(salesSummary.getTodaySalesAmount(), BigDecimal.valueOf(salesSummary.getTodayOrderCount()))
            );
        }
        salesSummary.setSalesGrowthRate(calculateGrowthRate(salesSummary.getTodaySalesAmount(), salesSummary.getYesterdaySalesAmount()));

        List<DashboardOverviewVO.SalesTrendItem> salesTrend = fillSalesTrend(
                dashboardMapper.selectSalesTrend(trendStart, trendEnd), trendStart, days
        );

        DashboardOverviewVO.SalesSection salesSection = new DashboardOverviewVO.SalesSection();
        salesSection.setSummary(salesSummary);
        salesSection.setTrend(salesTrend);
        salesSection.setHotProducts(dashboardMapper.selectHotProducts(trendStart, trendEnd, safeTopN));
        salesSection.setPaymentDistribution(dashboardMapper.selectPaymentDistribution(trendStart, trendEnd));
        overview.setSales(salesSection);

        DashboardOverviewVO.MemberSection memberSection = new DashboardOverviewVO.MemberSection();
        DashboardOverviewVO.MemberSummary memberSummary = dashboardMapper.selectMemberSummary(todayStart, todayEnd, weekStart, activeSince);
        memberSection.setSummary(memberSummary == null ? new DashboardOverviewVO.MemberSummary() : memberSummary);
        memberSection.setNewMemberTrend(fillMemberTrend(dashboardMapper.selectMemberTrend(trendStart, trendEnd), trendStart, days));
        DashboardOverviewVO.ReadyBlock levelDistribution = new DashboardOverviewVO.ReadyBlock();
        levelDistribution.setReady(false);
        levelDistribution.setPendingReason("会员等级统计待完善等级规则后开放");
        memberSection.setLevelDistribution(levelDistribution);
        overview.setMembers(memberSection);

        DashboardOverviewVO.InventorySection inventorySection = new DashboardOverviewVO.InventorySection();
        DashboardOverviewVO.InventorySummary inventorySummary = dashboardMapper.selectInventorySummary(todayStart, nearExpiryEnd);
        inventorySection.setSummary(inventorySummary == null ? new DashboardOverviewVO.InventorySummary() : inventorySummary);
        inventorySection.setLowStockList(dashboardMapper.selectLowStockList(safeTopN));
        inventorySection.setNearExpiryList(dashboardMapper.selectNearExpiryList(todayStart, nearExpiryEnd, safeTopN));
        overview.setInventory(inventorySection);

        DashboardOverviewVO.Meta meta = new DashboardOverviewVO.Meta();
        meta.setRangeType(days + "d");
        meta.setTopN(safeTopN);
        meta.setNearExpiryDays(safeNearExpiryDays);
        meta.setGeneratedAt(now);
        overview.setMeta(meta);
        return overview;
    }

    private List<DashboardOverviewVO.SalesTrendItem> fillSalesTrend(List<DashboardOverviewVO.SalesTrendItem> raw, Date startDate, int days) {
        Map<String, DashboardOverviewVO.SalesTrendItem> rawMap = new HashMap<>();
        if (raw != null) {
            for (DashboardOverviewVO.SalesTrendItem item : raw) {
                rawMap.put(item.getDate(), item);
            }
        }
        java.util.ArrayList<DashboardOverviewVO.SalesTrendItem> result = new java.util.ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < days; i++) {
            Date current = addDays(startDate, i);
            String key = format.format(current);
            DashboardOverviewVO.SalesTrendItem item = rawMap.getOrDefault(key, new DashboardOverviewVO.SalesTrendItem());
            item.setDate(key);
            if (item.getSalesAmount() == null) {
                item.setSalesAmount(BigDecimal.ZERO);
            }
            if (item.getOrderCount() == null) {
                item.setOrderCount(0L);
            }
            result.add(item);
        }
        return result;
    }

    private List<DashboardOverviewVO.MemberTrendItem> fillMemberTrend(List<DashboardOverviewVO.MemberTrendItem> raw, Date startDate, int days) {
        Map<String, DashboardOverviewVO.MemberTrendItem> rawMap = new HashMap<>();
        if (raw != null) {
            for (DashboardOverviewVO.MemberTrendItem item : raw) {
                rawMap.put(item.getDate(), item);
            }
        }
        java.util.ArrayList<DashboardOverviewVO.MemberTrendItem> result = new java.util.ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < days; i++) {
            Date current = addDays(startDate, i);
            String key = format.format(current);
            DashboardOverviewVO.MemberTrendItem item = rawMap.getOrDefault(key, new DashboardOverviewVO.MemberTrendItem());
            item.setDate(key);
            if (item.getCount() == null) {
                item.setCount(0L);
            }
            result.add(item);
        }
        return result;
    }

    private BigDecimal calculateGrowthRate(BigDecimal today, BigDecimal yesterday) {
        BigDecimal safeToday = today == null ? BigDecimal.ZERO : today;
        BigDecimal safeYesterday = yesterday == null ? BigDecimal.ZERO : yesterday;
        if (safeYesterday.compareTo(BigDecimal.ZERO) == 0) {
            return safeToday.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        return safeToday.subtract(safeYesterday)
                .multiply(BigDecimal.valueOf(100))
                .divide(safeYesterday, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeDivide(BigDecimal left, BigDecimal right) {
        if (left == null || right == null || right.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return left.divide(right, 2, RoundingMode.HALF_UP);
    }

    private int resolveRangeDays(String rangeType) {
        if ("15d".equalsIgnoreCase(rangeType)) {
            return 15;
        }
        if ("30d".equalsIgnoreCase(rangeType)) {
            return 30;
        }
        return 7;
    }

    private int resolveTopN(Integer topN) {
        if (topN == null) {
            return 10;
        }
        return Math.max(5, Math.min(topN, 20));
    }

    private int resolveNearExpiryDays(Integer nearExpiryDays) {
        if (nearExpiryDays == null) {
            return 7;
        }
        return Math.max(1, Math.min(nearExpiryDays, 30));
    }

    private Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    private Date addMillis(Date date, int millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, millis);
        return calendar.getTime();
    }
}
