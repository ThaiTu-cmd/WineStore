package com.doan.WineStore.controller.client;

import com.doan.WineStore.dto.response.client.ShopProductResponse;
import com.doan.WineStore.repository.CategoryRepository;
import com.doan.WineStore.service.client.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class ClientController {

    private static final int PAGE_SIZE = 12;

    @Autowired
    private ShopService shopService;

    @Autowired
    private CategoryRepository categoryRepository;

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

    @GetMapping("/blog")
    public String blog() {
        return "redirect:/";
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
