package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.response.admin.OrderDetailResponse;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.OrderListItemResponse;
import com.doan.WineStore.service.admin.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/orders")
public class OrderAdminApiController {
    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public PageResponse<OrderListItemResponse> getOrders(@RequestParam(defaultValue = "0") int page) {
        return adminOrderService.getOrders(page);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getOrderById(@PathVariable Long id) {
        return adminOrderService.getOrderById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        adminOrderService.updateOrder(id, data);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        adminOrderService.deleteOrder(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
