package com.doan.WineStore.service;

import com.doan.WineStore.entity.*;
import com.doan.WineStore.enums.PaymentStatus;
import com.doan.WineStore.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    public List<AddressEntity> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
    }

    public List<ShippingMethodEntity> getActiveShippingMethods() {
        return shippingMethodRepository.findByIsActiveTrue();
    }

    @Transactional
    public OrderEntity placeOrder(Long userId, Long addressId,
                                  Long shippingMethodId, String note,
                                  List<Map<String, Object>> cartItems) {
        AddressEntity address = addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId);
        if (address == null) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không hợp lệ");
        }

        ShippingMethodEntity shipping = shippingMethodRepository.findById(shippingMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Phương thức vận chuyển không hợp lệ"));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map<String, Object> item : cartItems) {
            BigDecimal price = new BigDecimal(item.get("price").toString());
            int qty = Integer.parseInt(item.get("qty").toString());
            subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(qty)));
        }

        BigDecimal shippingFee = shipping.getFee();
        if (subtotal.compareTo(new BigDecimal("500000")) >= 0) {
            shippingFee = BigDecimal.ZERO;
        }
        BigDecimal total = subtotal.add(shippingFee);

        LocalDateTime now = LocalDateTime.now();

        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setOrderCode(generateOrderCode());
        order.setShippingAddressId(addressId);
        order.setShippingMethodId(shippingMethodId);
        order.setRecipientName(address.getFullName());
        order.setRecipientPhone(address.getPhone());
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingCity(address.getCity());
        order.setShippingProvince(address.getProvince());
        order.setShippingCountry(address.getCountry());
        order.setShippingPostalCode(address.getPostalCode());
        order.setSubtotal(subtotal);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(total);
        order.setStatus("pending");
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setNote(note);
        order.setPlacedAt(now);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        OrderEntity savedOrder = orderRepository.save(order);

        for (Map<String, Object> item : cartItems) {
            Long productId = Long.valueOf(item.get("id").toString());
            String productName = (String) item.get("name");
            String productSku = item.containsKey("sku") ? (String) item.get("sku") : "SKU-" + productId;
            BigDecimal price = new BigDecimal(item.get("price").toString());
            int qty = Integer.parseInt(item.get("qty").toString());
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty));

            OrderItemEntity orderItem = new OrderItemEntity(
                    savedOrder.getId(), productId, productName, productSku,
                    qty, price, lineTotal);
            orderItemRepository.save(orderItem);
        }

        OrderStatusHistoryEntity history = new OrderStatusHistoryEntity(
                savedOrder.getId(), null, "pending", "Đơn hàng đã được đặt");
        orderStatusHistoryRepository.save(history);

        log.info("Order placed: code={}, userId={}, total={}", savedOrder.getOrderCode(), userId, total);
        return savedOrder;
    }

    public OrderEntity getOrderForUser(Long orderId, Long userId) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) return null;
        return order;
    }

    public List<OrderItemEntity> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public Map<Long, String> getProductPrimaryImages(List<Long> productIds) {
        Map<Long, String> map = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) return map;
        productImageRepository.findByProductIdInAndIsPrimaryTrue(productIds)
                .forEach(img -> map.put(img.getProductId(), img.getImageUrl()));
        return map;
    }

    public ShippingMethodEntity getShippingMethod(Long id) {
        return shippingMethodRepository.findById(id).orElse(null);
    }

    public List<OrderStatusHistoryEntity> getOrderHistory(Long orderId) {
        return orderStatusHistoryRepository.findByOrderIdOrderByChangedAtAsc(orderId);
    }

    public OrderEntity updateOrderRecipient(Long orderId, Long userId, String name, String phone) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) return null;
        order.setRecipientName(name);
        order.setRecipientPhone(phone);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private String generateOrderCode() {
        return "WS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + (int) (Math.random() * 1000);
    }
}