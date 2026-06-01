package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.OrderListItemResponse;
import com.doan.WineStore.service.admin.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/orders")
public class OrderAdminApiController {
    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public PageResponse<OrderListItemResponse> getOrders(@RequestParam(defaultValue = "0") int page) {
        return adminOrderService.getOrders(page);
    }
}
