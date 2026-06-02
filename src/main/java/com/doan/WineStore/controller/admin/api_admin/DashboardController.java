package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.response.admin.DashboardStats;
import com.doan.WineStore.service.admin.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStats getStats() {
        return dashboardService.getStats();
    }
}
