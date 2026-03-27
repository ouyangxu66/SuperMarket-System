package com.supermarket.dashboard.service;

import com.supermarket.dashboard.vo.DashboardOverviewVO;
import javax.servlet.http.HttpServletResponse;

public interface DashboardService {

    DashboardOverviewVO getOverview(String rangeType, Integer topN, Integer nearExpiryDays);

    /**
     * 导出销售统计数据
     * @param response
     * @param rangeType
     */
    void exportSales(HttpServletResponse response, String rangeType);
}
