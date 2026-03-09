package com.supermarket.dashboard.controller;

import com.supermarket.common.result.Result;
import com.supermarket.dashboard.service.DashboardService;
import com.supermarket.dashboard.vo.DashboardOverviewVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public Result<DashboardOverviewVO> overview(@RequestParam(required = false, defaultValue = "7d") String rangeType,
                                                @RequestParam(required = false, defaultValue = "10") Integer topN,
                                                @RequestParam(required = false, defaultValue = "7") Integer nearExpiryDays) {
        return Result.success(dashboardService.getOverview(rangeType, topN, nearExpiryDays));
    }
}
