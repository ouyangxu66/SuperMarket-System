package com.supermarket.dashboard.service;

import com.supermarket.dashboard.vo.DashboardOverviewVO;

public interface DashboardService {

    DashboardOverviewVO getOverview(String rangeType, Integer topN, Integer nearExpiryDays);
}
