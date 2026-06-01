package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.response.admin.OrderDetailResponse;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.OrderListItemResponse;
import com.doan.WineStore.repository.OrderRepository;
import com.doan.WineStore.service.admin.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public PageResponse<OrderListItemResponse> getOrders(int page) {
        int safePage = Math.max(0, page);
        Page<OrderListItemResponse> dtoPage = orderRepository
                .findAdminOrders(PageRequest.of(safePage, PAGE_SIZE))
                .map(item -> new OrderListItemResponse(
                        item.getId(),
                        item.getOrderCode(),
                        item.getUsername(),
                        item.getUserId(),
                        item.getTotal(),
                        item.getStatus() == null ? null : item.getStatus().toLowerCase(Locale.ROOT),
                        item.getCreatedAt()));
        return PageResponse.fromPage(dtoPage);
    }

    @Override
    public OrderDetailResponse getOrderById(Long id) {
        var item = orderRepository.findAdminOrderById(id);
        if (item == null) {
            throw new IllegalArgumentException("Order not found");
        }
        return new OrderDetailResponse(
                item.getId(),
                item.getOrderCode(),
                item.getUsername(),
                item.getUserId(),
                item.getTotal(),
                item.getStatus() == null ? null : item.getStatus().toLowerCase(Locale.ROOT),
                item.getCreatedAt());
    }
}
