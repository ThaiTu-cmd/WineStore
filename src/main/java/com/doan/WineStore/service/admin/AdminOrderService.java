package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.response.admin.OrderDetailResponse;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.OrderListItemResponse;

public interface AdminOrderService {
    PageResponse<OrderListItemResponse> getOrders(int page);

    OrderDetailResponse getOrderById(Long id);
}
