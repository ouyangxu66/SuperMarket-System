package com.supermarket.dashboard.service;

import com.supermarket.dashboard.vo.DashboardOverviewVO;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public interface DashboardService {

    DashboardOverviewVO getOverview(String rangeType, Integer topN, Integer nearExpiryDays);

    /**
     * 导出销售统计数据
     * @param response
     * @param rangeType
     */
    void exportSales(HttpServletResponse response, String rangeType);

    /**
     * 导出热销商品排行Excel
     * @param rangeType 范围：today, week, month, year
     * @param limit 限制数量，如果不传则默认100，或者导出全部
     * @param response HttpServletResponse
     */
    void exportHotProducts(String rangeType, Integer limit, HttpServletResponse response) throws IOException;
}
