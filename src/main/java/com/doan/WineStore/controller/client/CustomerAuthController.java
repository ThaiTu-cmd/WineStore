package com.doan.WineStore.controller.client;

import com.doan.WineStore.service.CustomerAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class CustomerAuthController {

    @Autowired
    private CustomerAuthService customerAuthService;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        HttpSession session,
                        Model model) {
        String error = customerAuthService.login(email, password, session);
        if (error != null) {
            model.addAttribute("error", error);
            return "client/auth/login";
        }
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam(required = false) String phone,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {
        String error = customerAuthService.register(firstName, lastName, email, phone, password, confirmPassword);
        if (error != null) {
            model.addAttribute("error", error);
            return "client/auth/register";
        }
        return "redirect:/auth/login?success=Dang+ky+thanh+cong!+Vui+long+dang+nhap.";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "client/auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        String error = customerAuthService.forgotPassword(email);
        if (error != null) {
            model.addAttribute("error", error);
            return "client/auth/forgot-password";
        }
        return "redirect:/auth/verify-otp?email=" + email.trim();
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtp(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "client/auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {
        String error = customerAuthService.verifyOtp(email, otp);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("email", email);
            return "client/auth/verify-otp";
        }
        return "redirect:/auth/reset-password?email=" + email.trim() + "&token=" + otp.trim();
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String email,
                                    @RequestParam String token,
                                    Model model) {
        model.addAttribute("email", email);
        model.addAttribute("token", token);
        return "client/auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String token,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                Model model) {
        String error = customerAuthService.resetPassword(email, token, password, confirmPassword);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("email", email);
            model.addAttribute("token", token);
            return "client/auth/reset-password";
        }
        return "redirect:/auth/login?success=Mat+khau+da+duoc+dat+lai+thanh+cong!+Vui+long+dang+nhap.";
    }
}
