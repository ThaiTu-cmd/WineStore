package com.doan.WineStore.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class ClientController {
    @GetMapping({ "", "/", "/home" })
    public String home() {
        return "client/views/index";
    }

    @GetMapping("/shop")
    public String shop() {
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
}
