package com.doan.WineStore.service;

import com.doan.WineStore.entity.PasswordResetToken;
import com.doan.WineStore.entity.User;
import com.doan.WineStore.enums.Role;
import com.doan.WineStore.enums.Status;
import com.doan.WineStore.repository.PasswordResetTokenRepository;
import com.doan.WineStore.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class CustomerAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final Random random = new Random();

    public String login(String email, String password, HttpSession session) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return "Vui long nhap day du thong tin dang nhap.";
        }

        User user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) {
            return "Email hoac mat khau khong dung.";
        }

        if (user.getStatus() == Status.INACTIVE || user.getStatus() == Status.LOCKED) {
            return "Tai khoan da bi vo hieu hoa.";
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return "Email hoac mat khau khong dung.";
        }

        session.setAttribute("user", Map.of(
            "id", user.getId(),
            "name", user.getFullName(),
            "email", user.getEmail()
        ));

        return null;
    }

    @Transactional
    public String register(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        if (firstName == null || firstName.isBlank()) return "Vui long nhap ho.";
        if (lastName == null || lastName.isBlank()) return "Vui long nhap ten.";
        if (email == null || email.isBlank()) return "Vui long nhap email.";
        if (password == null || password.isBlank()) return "Vui long nhap mat khau.";
        if (confirmPassword == null || confirmPassword.isBlank()) return "Vui long nhap lai mat khau.";

        if (!password.equals(confirmPassword)) return "Mat khau xac nhan khong khop.";
        if (password.length() < 8) return "Mat khau phai co it nhat 8 ky tu.";

        if (userRepository.findByEmail(email.trim()).isPresent()) {
            return "Email nay da duoc dang ky.";
        }

        User user = new User(
            (firstName.trim() + " " + lastName.trim()).trim(),
            email.trim(),
            phone != null ? phone.trim() : null,
            Role.CUSTOMER,
            Status.ACTIVE,
            passwordEncoder.encode(password)
        );

        userRepository.save(user);
        return null;
    }

    @Transactional
    public String forgotPassword(String email) {
        if (email == null || email.isBlank()) return "Vui long nhap email.";

        User user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) return "Email nay chua duoc dang ky.";

        String otp = String.format("%06d", random.nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        PasswordResetToken token = new PasswordResetToken(email.trim(), otp, expiry);
        tokenRepository.save(token);

        emailService.sendOtp(email.trim(), otp);

        return null;
    }

    public String verifyOtp(String email, String otp) {
        if (email == null || email.isBlank()) return "Vui long nhap email.";
        if (otp == null || otp.isBlank()) return "Vui long nhap ma OTP.";

        PasswordResetToken token = tokenRepository
            .findByEmailAndOtpAndUsedFalseAndExpiryDateAfter(email.trim(), otp.trim(), LocalDateTime.now())
            .orElse(null);

        if (token == null) return "Ma OTP khong dung hoac da het han.";
        return null;
    }

    @Transactional
    public String resetPassword(String email, String token, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isBlank()) return "Vui long nhap mat khau moi.";
        if (confirmPassword == null || confirmPassword.isBlank()) return "Vui long nhap lai mat khau moi.";
        if (!newPassword.equals(confirmPassword)) return "Mat khau xac nhan khong khop.";
        if (newPassword.length() < 8) return "Mat khau phai co it nhat 8 ky tu.";

        PasswordResetToken resetToken = tokenRepository
            .findByEmailAndOtpAndUsedFalseAndExpiryDateAfter(email.trim(), token.trim(), LocalDateTime.now())
            .orElse(null);

        if (resetToken == null) return "Ma OTP khong dung hoac da het han. Vui long thu lai.";

        User user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) return "Nguoi dung khong ton tai.";

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return null;
    }
}
