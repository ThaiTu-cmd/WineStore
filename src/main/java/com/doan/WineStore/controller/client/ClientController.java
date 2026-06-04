package com.doan.WineStore.controller.client;

import com.doan.WineStore.dto.response.client.ShopProductResponse;
import com.doan.WineStore.entity.AddressEntity;
import com.doan.WineStore.entity.OrderEntity;
import com.doan.WineStore.entity.OrderItemEntity;
import com.doan.WineStore.entity.OrderStatusHistoryEntity;
import com.doan.WineStore.entity.ShippingMethodEntity;
import com.doan.WineStore.entity.User;
import com.doan.WineStore.repository.CategoryRepository;
import com.doan.WineStore.service.AddressService;
import com.doan.WineStore.service.CheckoutService;
import com.doan.WineStore.service.CustomerAuthService;
import com.doan.WineStore.service.client.ShopService;
import com.doan.WineStore.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private static final int PAGE_SIZE = 12;

    @Autowired
    private ShopService shopService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CustomerAuthService customerAuthService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CheckoutService checkoutService;

    @GetMapping({ "", "/", "/home" })
    public String home() {
        return "client/views/index";
    }

    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String price,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        int safePage = Math.max(0, page - 1);

        String cat = blankToNull(category);
        String s = blankToNull(sort);
        String q = blankToNull(search);
        String p = blankToNull(price);

        Page<ShopProductResponse> productPage = shopService.getProducts(
                cat, s, q, p, safePage, PAGE_SIZE);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalProducts", productPage.getTotalElements());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);

        List<CategoryRepository.CategoryListProjection> categories =
                categoryRepository.findClientCategories();
        model.addAttribute("categories", categories);

        return "client/views/shop";
    }

    @GetMapping("/about")
    public String about() {
        return "client/views/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "client/views/contact";
    }

    @GetMapping("/cart")
    public String cart() {
        return "client/views/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        List<AddressEntity> addresses = checkoutService.getUserAddresses(userId);
        List<ShippingMethodEntity> shippingMethods = checkoutService.getActiveShippingMethods();

        model.addAttribute("addresses", addresses);
        model.addAttribute("shippingMethods", shippingMethods);
        model.addAttribute("shippingFeesJson", new ObjectMapper().writeValueAsString(
                shippingMethods.stream()
                        .collect(HashMap::new, (m, sm) -> m.put(sm.getId().toString(), sm.getFee()), HashMap::putAll)));
        return "client/views/checkout";
    }

    @GetMapping("/detail")
    public String detail() {
        return "client/views/detail";
    }

    @GetMapping("/auth/login")
    public String login(@RequestParam(required = false) String success, Model model) {
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "client/auth/login";
    }

    @GetMapping("/auth/register")
    public String register() {
        return "client/auth/register";
    }

    @GetMapping("/userprofile")
    public String userprofile(HttpSession session, Model model) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";

        Long userId = (Long) userMap.get("id");
        User user = customerAuthService.getUserById(userId);
        if (user == null) {
            session.invalidate();
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        return "client/views/userprofile";
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");
        List<OrderEntity> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        model.addAttribute("orders", orders);

        Map<Long, List<OrderItemEntity>> orderItemsMap = new HashMap<>();
        Map<Long, Map<Long, String>> orderProductImages = new HashMap<>();
        Set<Long> allProductIds = new HashSet<>();
        for (OrderEntity order : orders) {
            List<OrderItemEntity> items = checkoutService.getOrderItems(order.getId());
            orderItemsMap.put(order.getId(), items);
            for (OrderItemEntity item : items) {
                if (item.getProductId() != null) {
                    allProductIds.add(item.getProductId());
                }
            }
        }
        Map<Long, String> primaryImages = checkoutService.getProductPrimaryImages(new ArrayList<>(allProductIds));
        for (OrderEntity order : orders) {
            Map<Long, String> imgMap = new HashMap<>();
            List<OrderItemEntity> items = orderItemsMap.get(order.getId());
            if (items != null) {
                for (OrderItemEntity item : items) {
                    if (item.getProductId() != null && primaryImages.containsKey(item.getProductId())) {
                        imgMap.put(item.getProductId(), primaryImages.get(item.getProductId()));
                    }
                }
            }
            orderProductImages.put(order.getId(), imgMap);
        }
        model.addAttribute("orderItemsMap", orderItemsMap);
        model.addAttribute("orderProductImages", orderProductImages);
        return "client/views/orders";
    }

    @GetMapping("/address")
    public String address(HttpSession session, Model model) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");
        model.addAttribute("addresses", addressService.getAddressesByUserId(userId));
        return "client/views/address";
    }

    @PostMapping("/userprofile/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";

        Long userId = (Long) userMap.get("id");
        String error = customerAuthService.updateProfile(userId, fullName, email, phone, session);

        if (error != null) {
            redirectAttributes.addFlashAttribute("profileError", error);
        } else {
            redirectAttributes.addFlashAttribute("profileSuccess", "Cập nhật thông tin thành công!");
        }

        return "redirect:/userprofile";
    }

    @PostMapping("/userprofile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";

        Long userId = (Long) userMap.get("id");
        String error = customerAuthService.changePassword(userId, currentPassword, newPassword, confirmPassword);

        if (error != null) {
            redirectAttributes.addFlashAttribute("passwordError", error);
        } else {
            redirectAttributes.addFlashAttribute("passwordSuccess", "Đổi mật khẩu thành công!");
        }

        return "redirect:/userprofile";
    }

    @PostMapping("/address/add")
    public String addAddress(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String addressLine1,
            @RequestParam(required = false) String addressLine2,
            @RequestParam String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam String type,
            @RequestParam(required = false) Boolean isDefault,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        try {
            AddressEntity saved = addressService.save(userId, fullName, phone, addressLine1, addressLine2,
                    city, province, country, postalCode, type, isDefault);
            if (saved != null) {
                log.info("Address added: id={} for userId={}", saved.getId(), userId);
                redirectAttributes.addFlashAttribute("addressSuccess", "Thêm địa chỉ mới thành công!");
            } else {
                log.warn("Address add failed (null) for userId={}", userId);
                redirectAttributes.addFlashAttribute("addressError", "Thêm địa chỉ thất bại!");
            }
        } catch (Exception e) {
            log.error("Address add error for userId={}: {}", userId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("addressError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/address";
    }

    @PostMapping("/address/update")
    public String updateAddress(
            @RequestParam Long id,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String addressLine1,
            @RequestParam(required = false) String addressLine2,
            @RequestParam String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam String type,
            @RequestParam(required = false) Boolean isDefault,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        AddressEntity updated = addressService.update(id, userId, fullName, phone,
                addressLine1, addressLine2, city, province, country, postalCode,
                type, isDefault);
        if (updated != null) {
            log.info("Address updated: id={} for userId={} (success)", id, userId);
            redirectAttributes.addFlashAttribute("addressSuccess", "Cập nhật địa chỉ thành công!");
        } else {
            log.warn("Address update failed: id={} for userId={} (not found or no ownership)", id, userId);
            redirectAttributes.addFlashAttribute("addressError", "Không tìm thấy địa chỉ hoặc không có quyền chỉnh sửa!");
        }
        return "redirect:/address";
    }

    @PostMapping("/address/delete")
    public String deleteAddress(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        boolean deleted = addressService.delete(id, userId);
        if (deleted) {
            log.info("Address deleted: id={} for userId={} (success)", id, userId);
            redirectAttributes.addFlashAttribute("addressSuccess", "Đã xóa địa chỉ thành công!");
        } else {
            log.warn("Address delete failed: id={} for userId={} (not found or no ownership)", id, userId);
            redirectAttributes.addFlashAttribute("addressError", "Không tìm thấy địa chỉ!");
        }
        return "redirect:/address";
    }

    @PostMapping("/address/set-default")
    public String setDefaultAddress(
            @RequestParam Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        addressService.setDefault(id, userId);
        log.info("Address set default: id={} for userId={}", id, userId);
        redirectAttributes.addFlashAttribute("addressSuccess", "Đã đặt làm địa chỉ mặc định!");
        return "redirect:/address";
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @RequestParam Long addressId,
            @RequestParam Long shippingMethodId,
            @RequestParam(required = false) String note,
            @RequestParam String cartData,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> items = mapper.readValue(cartData, new TypeReference<List<Map<String, Object>>>() {});

            OrderEntity order = checkoutService.placeOrder(userId, addressId,
                    shippingMethodId, note, items);
            redirectAttributes.addFlashAttribute("orderSuccess",
                    "Đặt hàng thành công! Mã đơn hàng: " + order.getOrderCode());
            return "redirect:/orders";
        } catch (Exception e) {
            log.error("Place order failed", e);
            redirectAttributes.addFlashAttribute("checkoutError",
                    "Đặt hàng thất bại: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @PostMapping("/orders/cancel")
    public String cancelOrder(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");
        OrderEntity order = orderRepository.findById(id).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            redirectAttributes.addFlashAttribute("orderError", "Không tìm thấy đơn hàng!");
            return "redirect:/orders";
        }
        if ("completed".equalsIgnoreCase(order.getStatus()) || "cancelled".equalsIgnoreCase(order.getStatus())) {
            redirectAttributes.addFlashAttribute("orderError", "Đơn hàng không thể hủy!");
            return "redirect:/orders";
        }
        order.setStatus("cancelled");
        order.setCanceledAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        redirectAttributes.addFlashAttribute("orderSuccess", "Đã hủy đơn hàng thành công!");
        return "redirect:/orders";
    }

    @PostMapping("/orders/confirm-received")
    public String confirmReceived(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");
        OrderEntity order = orderRepository.findById(id).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            redirectAttributes.addFlashAttribute("orderError", "Không tìm thấy đơn hàng!");
            return "redirect:/orders";
        }
        if (!"shipping".equalsIgnoreCase(order.getStatus())) {
            redirectAttributes.addFlashAttribute("orderError", "Đơn hàng không ở trạng thái đang giao!");
            return "redirect:/orders";
        }
        order.setStatus("completed");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        redirectAttributes.addFlashAttribute("orderSuccess", "Đã xác nhận nhận hàng thành công!");
        return "redirect:/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        OrderEntity order = checkoutService.getOrderForUser(id, userId);
        if (order == null) {
            return "redirect:/orders";
        }

        List<OrderItemEntity> items = checkoutService.getOrderItems(id);
        List<Long> productIds = items.stream().map(OrderItemEntity::getProductId).toList();
        Map<Long, String> productImages = checkoutService.getProductPrimaryImages(productIds);
        ShippingMethodEntity shipping = checkoutService.getShippingMethod(order.getShippingMethodId());
        List<OrderStatusHistoryEntity> history = checkoutService.getOrderHistory(id);

        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("productImages", productImages);
        model.addAttribute("shipping", shipping);
        model.addAttribute("history", history);
        return "client/views/orderdetail";
    }

    @PostMapping("/orders/{id}/update")
    public String updateOrderRecipient(@PathVariable Long id,
                                       @RequestParam String recipientName,
                                       @RequestParam String recipientPhone,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Map<String, Object> userMap = (Map<String, Object>) session.getAttribute("user");
        if (userMap == null) return "redirect:/auth/login";
        Long userId = (Long) userMap.get("id");

        OrderEntity order = checkoutService.updateOrderRecipient(id, userId, recipientName, recipientPhone);
        if (order == null) {
            redirectAttributes.addFlashAttribute("orderError", "Không tìm thấy đơn hàng!");
        } else {
            redirectAttributes.addFlashAttribute("orderSuccess", "Cập nhật thông tin thành công!");
        }
        return "redirect:/orders/" + id;
    }

    @GetMapping("/blog")
    public String blog() {
        return "redirect:/";
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
