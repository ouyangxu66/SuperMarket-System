package com.supermarket.dashboard.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dashboard 总览返回对象
 */
public class DashboardOverviewVO {

    private SalesSection sales = new SalesSection();
    private MemberSection members = new MemberSection();
    private InventorySection inventory = new InventorySection();
    private Meta meta = new Meta();

    public SalesSection getSales() {
        return sales;
    }

    public void setSales(SalesSection sales) {
        this.sales = sales;
    }

    public MemberSection getMembers() {
        return members;
    }

    public void setMembers(MemberSection members) {
        this.members = members;
    }

    public InventorySection getInventory() {
        return inventory;
    }

    public void setInventory(InventorySection inventory) {
        this.inventory = inventory;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class SalesSection {
        private SalesSummary summary = new SalesSummary();
        private List<SalesTrendItem> trend = new ArrayList<>();
        private List<HotProductItem> hotProducts = new ArrayList<>();
        private List<PaymentDistributionItem> paymentDistribution = new ArrayList<>();

        public SalesSummary getSummary() {
            return summary;
        }

        public void setSummary(SalesSummary summary) {
            this.summary = summary;
        }

        public List<SalesTrendItem> getTrend() {
            return trend;
        }

        public void setTrend(List<SalesTrendItem> trend) {
            this.trend = trend;
        }

        public List<HotProductItem> getHotProducts() {
            return hotProducts;
        }

        public void setHotProducts(List<HotProductItem> hotProducts) {
            this.hotProducts = hotProducts;
        }

        public List<PaymentDistributionItem> getPaymentDistribution() {
            return paymentDistribution;
        }

        public void setPaymentDistribution(List<PaymentDistributionItem> paymentDistribution) {
            this.paymentDistribution = paymentDistribution;
        }
    }

    public static class MemberSection {
        private MemberSummary summary = new MemberSummary();
        private List<MemberTrendItem> newMemberTrend = new ArrayList<>();
        private ReadyBlock levelDistribution = new ReadyBlock();

        public MemberSummary getSummary() {
            return summary;
        }

        public void setSummary(MemberSummary summary) {
            this.summary = summary;
        }

        public List<MemberTrendItem> getNewMemberTrend() {
            return newMemberTrend;
        }

        public void setNewMemberTrend(List<MemberTrendItem> newMemberTrend) {
            this.newMemberTrend = newMemberTrend;
        }

        public ReadyBlock getLevelDistribution() {
            return levelDistribution;
        }

        public void setLevelDistribution(ReadyBlock levelDistribution) {
            this.levelDistribution = levelDistribution;
        }
    }

    public static class InventorySection {
        private InventorySummary summary = new InventorySummary();
        private List<LowStockItem> lowStockList = new ArrayList<>();
        private List<NearExpiryItem> nearExpiryList = new ArrayList<>();

        public InventorySummary getSummary() {
            return summary;
        }

        public void setSummary(InventorySummary summary) {
            this.summary = summary;
        }

        public List<LowStockItem> getLowStockList() {
            return lowStockList;
        }

        public void setLowStockList(List<LowStockItem> lowStockList) {
            this.lowStockList = lowStockList;
        }

        public List<NearExpiryItem> getNearExpiryList() {
            return nearExpiryList;
        }

        public void setNearExpiryList(List<NearExpiryItem> nearExpiryList) {
            this.nearExpiryList = nearExpiryList;
        }
    }

    public static class Meta {
        private String rangeType;
        private Integer topN;
        private Integer nearExpiryDays;
        private Date generatedAt;

        public String getRangeType() {
            return rangeType;
        }

        public void setRangeType(String rangeType) {
            this.rangeType = rangeType;
        }

        public Integer getTopN() {
            return topN;
        }

        public void setTopN(Integer topN) {
            this.topN = topN;
        }

        public Integer getNearExpiryDays() {
            return nearExpiryDays;
        }

        public void setNearExpiryDays(Integer nearExpiryDays) {
            this.nearExpiryDays = nearExpiryDays;
        }

        public Date getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(Date generatedAt) {
            this.generatedAt = generatedAt;
        }
    }

    public static class SalesSummary {
        private java.math.BigDecimal todaySalesAmount = java.math.BigDecimal.ZERO;
        private Long todayOrderCount = 0L;
        private java.math.BigDecimal todayAvgOrderAmount = java.math.BigDecimal.ZERO;
        private java.math.BigDecimal yesterdaySalesAmount = java.math.BigDecimal.ZERO;
        private java.math.BigDecimal salesGrowthRate = java.math.BigDecimal.ZERO;

        public java.math.BigDecimal getTodaySalesAmount() {
            return todaySalesAmount;
        }

        public void setTodaySalesAmount(java.math.BigDecimal todaySalesAmount) {
            this.todaySalesAmount = todaySalesAmount;
        }

        public Long getTodayOrderCount() {
            return todayOrderCount;
        }

        public void setTodayOrderCount(Long todayOrderCount) {
            this.todayOrderCount = todayOrderCount;
        }

        public java.math.BigDecimal getTodayAvgOrderAmount() {
            return todayAvgOrderAmount;
        }

        public void setTodayAvgOrderAmount(java.math.BigDecimal todayAvgOrderAmount) {
            this.todayAvgOrderAmount = todayAvgOrderAmount;
        }

        public java.math.BigDecimal getYesterdaySalesAmount() {
            return yesterdaySalesAmount;
        }

        public void setYesterdaySalesAmount(java.math.BigDecimal yesterdaySalesAmount) {
            this.yesterdaySalesAmount = yesterdaySalesAmount;
        }

        public java.math.BigDecimal getSalesGrowthRate() {
            return salesGrowthRate;
        }

        public void setSalesGrowthRate(java.math.BigDecimal salesGrowthRate) {
            this.salesGrowthRate = salesGrowthRate;
        }
    }

    public static class SalesTrendItem {
        private String date;
        private java.math.BigDecimal salesAmount = java.math.BigDecimal.ZERO;
        private Long orderCount = 0L;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public java.math.BigDecimal getSalesAmount() {
            return salesAmount;
        }

        public void setSalesAmount(java.math.BigDecimal salesAmount) {
            this.salesAmount = salesAmount;
        }

        public Long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Long orderCount) {
            this.orderCount = orderCount;
        }
    }

    public static class HotProductItem {
        private Long productId;
        private String productName;
        private String barcode;
        private Long salesQuantity = 0L;
        private java.math.BigDecimal salesAmount = java.math.BigDecimal.ZERO;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public Long getSalesQuantity() {
            return salesQuantity;
        }

        public void setSalesQuantity(Long salesQuantity) {
            this.salesQuantity = salesQuantity;
        }

        public java.math.BigDecimal getSalesAmount() {
            return salesAmount;
        }

        public void setSalesAmount(java.math.BigDecimal salesAmount) {
            this.salesAmount = salesAmount;
        }
    }

    public static class PaymentDistributionItem {
        private Integer paymentType;
        private String paymentTypeName;
        private Long orderCount = 0L;
        private java.math.BigDecimal amount = java.math.BigDecimal.ZERO;

        public Integer getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(Integer paymentType) {
            this.paymentType = paymentType;
        }

        public String getPaymentTypeName() {
            return paymentTypeName;
        }

        public void setPaymentTypeName(String paymentTypeName) {
            this.paymentTypeName = paymentTypeName;
        }

        public Long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Long orderCount) {
            this.orderCount = orderCount;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class MemberSummary {
        private Long memberTotal = 0L;
        private Long todayNewMemberCount = 0L;
        private Long weekNewMemberCount = 0L;
        private java.math.BigDecimal totalBalance = java.math.BigDecimal.ZERO;
        private Long totalPoints = 0L;
        private Long activeMemberCount = 0L;

        public Long getMemberTotal() {
            return memberTotal;
        }

        public void setMemberTotal(Long memberTotal) {
            this.memberTotal = memberTotal;
        }

        public Long getTodayNewMemberCount() {
            return todayNewMemberCount;
        }

        public void setTodayNewMemberCount(Long todayNewMemberCount) {
            this.todayNewMemberCount = todayNewMemberCount;
        }

        public Long getWeekNewMemberCount() {
            return weekNewMemberCount;
        }

        public void setWeekNewMemberCount(Long weekNewMemberCount) {
            this.weekNewMemberCount = weekNewMemberCount;
        }

        public java.math.BigDecimal getTotalBalance() {
            return totalBalance;
        }

        public void setTotalBalance(java.math.BigDecimal totalBalance) {
            this.totalBalance = totalBalance;
        }

        public Long getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(Long totalPoints) {
            this.totalPoints = totalPoints;
        }

        public Long getActiveMemberCount() {
            return activeMemberCount;
        }

        public void setActiveMemberCount(Long activeMemberCount) {
            this.activeMemberCount = activeMemberCount;
        }
    }

    public static class MemberTrendItem {
        private String date;
        private Long count = 0L;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    public static class ReadyBlock {
        private boolean ready;
        private String pendingReason;
        private List<Object> items = new ArrayList<>();

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        public String getPendingReason() {
            return pendingReason;
        }

        public void setPendingReason(String pendingReason) {
            this.pendingReason = pendingReason;
        }

        public List<Object> getItems() {
            return items;
        }

        public void setItems(List<Object> items) {
            this.items = items;
        }
    }

    public static class InventorySummary {
        private Long productTotal = 0L;
        private Long stockTotalQuantity = 0L;
        private Long lowStockProductCount = 0L;
        private Long nearExpiryProductCount = 0L;
        private Long expiredProductCount = 0L;
        private java.math.BigDecimal inventoryCostAmount = java.math.BigDecimal.ZERO;

        public Long getProductTotal() {
            return productTotal;
        }

        public void setProductTotal(Long productTotal) {
            this.productTotal = productTotal;
        }

        public Long getStockTotalQuantity() {
            return stockTotalQuantity;
        }

        public void setStockTotalQuantity(Long stockTotalQuantity) {
            this.stockTotalQuantity = stockTotalQuantity;
        }

        public Long getLowStockProductCount() {
            return lowStockProductCount;
        }

        public void setLowStockProductCount(Long lowStockProductCount) {
            this.lowStockProductCount = lowStockProductCount;
        }

        public Long getNearExpiryProductCount() {
            return nearExpiryProductCount;
        }

        public void setNearExpiryProductCount(Long nearExpiryProductCount) {
            this.nearExpiryProductCount = nearExpiryProductCount;
        }

        public Long getExpiredProductCount() {
            return expiredProductCount;
        }

        public void setExpiredProductCount(Long expiredProductCount) {
            this.expiredProductCount = expiredProductCount;
        }

        public java.math.BigDecimal getInventoryCostAmount() {
            return inventoryCostAmount;
        }

        public void setInventoryCostAmount(java.math.BigDecimal inventoryCostAmount) {
            this.inventoryCostAmount = inventoryCostAmount;
        }
    }

    public static class LowStockItem {
        private Long productId;
        private String productName;
        private Integer stock;
        private Integer lowStockThreshold;
        private Integer gap;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public Integer getLowStockThreshold() {
            return lowStockThreshold;
        }

        public void setLowStockThreshold(Integer lowStockThreshold) {
            this.lowStockThreshold = lowStockThreshold;
        }

        public Integer getGap() {
            return gap;
        }

        public void setGap(Integer gap) {
            this.gap = gap;
        }
    }

    public static class NearExpiryItem {
        private Long productId;
        private String productName;
        private Date earliestExpirationDate;
        private Integer remainingDays;
        private Integer stock;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Date getEarliestExpirationDate() {
            return earliestExpirationDate;
        }

        public void setEarliestExpirationDate(Date earliestExpirationDate) {
            this.earliestExpirationDate = earliestExpirationDate;
        }

        public Integer getRemainingDays() {
            return remainingDays;
        }

        public void setRemainingDays(Integer remainingDays) {
            this.remainingDays = remainingDays;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }
}
