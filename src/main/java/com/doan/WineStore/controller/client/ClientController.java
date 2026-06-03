package com.doan.WineStore.controller.client;

import com.doan.WineStore.dto.response.client.ShopProductResponse;
import com.doan.WineStore.entity.AddressEntity;
import com.doan.WineStore.entity.OrderEntity;
import com.doan.WineStore.entity.User;
import com.doan.WineStore.repository.CategoryRepository;
import com.doan.WineStore.service.AddressService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

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
    public String checkout() {
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
        model.addAttribute("orders", orderRepository.findByUserIdOrderByCreatedAtDesc(userId));
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

        AddressEntity saved = addressService.save(userId, fullName, phone, addressLine1, addressLine2,
                city, province, country, postalCode, type, isDefault);
        if (saved != null) {
            log.info("Address added: id={} for userId={}", saved.getId(), userId);
            redirectAttributes.addFlashAttribute("addressSuccess", "Thêm địa chỉ mới thành công!");
        } else {
            log.warn("Address add failed for userId={}", userId);
            redirectAttributes.addFlashAttribute("addressError", "Thêm địa chỉ thất bại!");
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
        orderRepository.save(order);
        redirectAttributes.addFlashAttribute("orderSuccess", "Đã hủy đơn hàng thành công!");
        return "redirect:/orders";
    }

    @GetMapping("/blog")
    public String blog() {
        return "redirect:/";
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
