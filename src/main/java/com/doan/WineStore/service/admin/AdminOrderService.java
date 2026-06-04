package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.response.admin.OrderDetailResponse;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.OrderListItemResponse;

import java.util.Map;

public interface AdminOrderService {
    PageResponse<OrderListItemResponse> getOrders(int page);

    OrderDetailResponse getOrderById(Long id);

    void updateOrder(Long id, Map<String, Object> data);

    void deleteOrder(Long id);
}
