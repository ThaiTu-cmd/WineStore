package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.response.admin.OrderDetailResponse;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.OrderListItemResponse;
import com.doan.WineStore.entity.OrderEntity;
import com.doan.WineStore.entity.OrderStatusHistoryEntity;
import com.doan.WineStore.repository.OrderRepository;
import com.doan.WineStore.repository.OrderStatusHistoryRepository;
import com.doan.WineStore.service.admin.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

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
    @Transactional
    public void updateOrder(Long id, Map<String, Object> data) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (data.containsKey("status")) {
            String oldStatus = entity.getStatus();
            String newStatus = (String) data.get("status");
            entity.setStatus(newStatus);

            String note;
            switch (newStatus != null ? newStatus.toLowerCase(Locale.ROOT) : "") {
                case "processing":
                    note = "Đơn hàng đã được xác nhận";
                    break;
                case "shipping":
                    note = "Đơn hàng đang được giao";
                    break;
                case "completed":
                    note = "Đơn hàng đã hoàn thành";
                    break;
                case "cancelled":
                    note = "Đơn hàng đã bị hủy";
                    break;
                default:
                    note = "Trạng thái thay đổi thành " + (newStatus != null ? newStatus : "");
                    break;
            }

            OrderStatusHistoryEntity history = new OrderStatusHistoryEntity(
                    entity.getId(), oldStatus, newStatus, note);
            orderStatusHistoryRepository.save(history);
        }
        if (data.containsKey("subtotal")) {
            entity.setSubtotal(new BigDecimal(data.get("subtotal").toString()));
        }
        if (data.containsKey("total_amount")) {
            entity.setTotalAmount(new BigDecimal(data.get("total_amount").toString()));
        }
        if (data.containsKey("shipping_address_id")) {
            Object val = data.get("shipping_address_id");
            entity.setShippingAddressId(val != null ? Long.valueOf(val.toString()) : null);
        }
        if (data.containsKey("payment_method_id")) {
            Object val = data.get("payment_method_id");
            entity.setPaymentMethodId(val != null ? Long.valueOf(val.toString()) : null);
        }
        if (data.containsKey("shipping_method_id")) {
            Object val = data.get("shipping_method_id");
            entity.setShippingMethodId(val != null ? Long.valueOf(val.toString()) : null);
        }
        if (data.containsKey("discount_code_id")) {
            Object val = data.get("discount_code_id");
            entity.setDiscountCodeId(val != null ? Long.valueOf(val.toString()) : null);
        }
        entity.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found");
        }
        orderRepository.deleteById(id);
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
                item.getCreatedAt(),
                item.getShippingAddressId(),
                item.getPaymentMethodId(),
                item.getShippingMethodId(),
                item.getDiscountCodeId(),
                item.getSubtotal(),
                item.getTotalAmount());
    }
}
