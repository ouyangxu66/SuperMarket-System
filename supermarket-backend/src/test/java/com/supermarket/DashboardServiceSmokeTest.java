package com.supermarket;

import com.supermarket.dashboard.service.DashboardService;
import com.supermarket.dashboard.vo.DashboardOverviewVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DashboardServiceSmokeTest {

    @Autowired
    private DashboardService dashboardService;

    @Test
    void shouldBuildOverviewWithFallbackValues() {
        DashboardOverviewVO overview = dashboardService.getOverview("7d", 10, 7);
        assertNotNull(overview);
        assertNotNull(overview.getSales());
        assertNotNull(overview.getMembers());
        assertNotNull(overview.getInventory());
        assertNotNull(overview.getMeta());
    }
}
