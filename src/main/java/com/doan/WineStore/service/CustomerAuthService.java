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
            return "Vui lòng nhập đầy đủ thông tin đăng nhập.";
        }

        User user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) {
            return "Tài khoản chưa đăng ký.";
        }

        if (user.getStatus() == Status.INACTIVE || user.getStatus() == Status.LOCKED) {
            return "Tài khoản đã bị vô hiệu hóa.";
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return "Email hoặc mật khẩu không đúng.";
        }

        session.setAttribute("user", Map.of(
            "id", user.getId(),
            "name", user.getFullName(),
            "email", user.getEmail(),
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : "",
            "role", user.getRole().name()
        ));

        return null;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public String updateProfile(Long userId, String fullName, String email, String phone, HttpSession session) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "Người dùng không tồn tại.";

        if (fullName == null || fullName.isBlank()) return "Vui lòng nhập họ và tên.";
        if (email == null || email.isBlank()) return "Vui lòng nhập email.";

        if (!email.equals(user.getEmail()) && userRepository.findByEmail(email.trim()).isPresent()) {
            return "Email này đã được sử dụng.";
        }

        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPhone(phone != null ? phone.trim() : null);
        userRepository.save(user);

        Map<String, Object> currentSession = (Map<String, Object>) session.getAttribute("user");
        if (currentSession != null) {
            Map<String, Object> updated = new java.util.HashMap<>(currentSession);
            updated.put("name", user.getFullName());
            updated.put("email", user.getEmail());
            updated.put("phone", user.getPhone() != null ? user.getPhone() : "");
            session.setAttribute("user", updated);
        }

        return null;
    }

    public String changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "Người dùng không tồn tại.";

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return "Mật khẩu hiện tại không đúng.";
        }

        if (newPassword == null || newPassword.isBlank()) return "Vui lòng nhập mật khẩu mới.";
        if (confirmPassword == null || confirmPassword.isBlank()) return "Vui lòng nhập lại mật khẩu mới.";
        if (!newPassword.equals(confirmPassword)) return "Mật khẩu xác nhận không khớp.";
        if (newPassword.length() < 8) return "Mật khẩu phải có ít nhất 8 ký tự.";

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return null;
    }

    @Transactional
    public String register(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        if (firstName == null || firstName.isBlank()) return "Vui lòng nhập họ.";
        if (lastName == null || lastName.isBlank()) return "Vui lòng nhập tên.";
        if (email == null || email.isBlank()) return "Vui lòng nhập email.";
        if (password == null || password.isBlank()) return "Vui lòng nhập mật khẩu.";
        if (confirmPassword == null || confirmPassword.isBlank()) return "Vui lòng nhập lại mật khẩu.";

        if (!password.equals(confirmPassword)) return "Mật khẩu xác nhận không khớp.";
        if (password.length() < 8) return "Mật khẩu phải có ít nhất 8 ký tự.";

        if (userRepository.findByEmail(email.trim()).isPresent()) {
            return "Email này đã được đăng ký.";
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
        if (email == null || email.isBlank()) return "Vui lòng nhập email.";

        User user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) return "Email này chưa được đăng ký.";

        String otp = String.format("%06d", random.nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        PasswordResetToken token = new PasswordResetToken(email.trim(), otp, expiry);
        tokenRepository.save(token);

        emailService.sendOtp(email.trim(), otp);

        return null;
    }

    public String verifyOtp(String email, String otp) {
        if (email == null || email.isBlank()) return "Vui lòng nhập email.";
        if (otp == null || otp.isBlank()) return "Vui lòng nhập mã OTP.";

        PasswordResetToken token = tokenRepository
            .findByEmailAndOtpAndUsedFalseAndExpiryDateAfter(email.trim(), otp.trim(), LocalDateTime.now())
            .orElse(null);

        if (token == null) return "Mã OTP không đúng hoặc đã hết hạn.";
        return null;
    }

    @Transactional
    public String resetPassword(String email, String token, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isBlank()) return "Vui lòng nhập mật khẩu mới.";
        if (confirmPassword == null || confirmPassword.isBlank()) return "Vui lòng nhập lại mật khẩu mới.";
        if (!newPassword.equals(confirmPassword)) return "Mật khẩu xác nhận không khớp.";
        if (newPassword.length() < 8) return "Mật khẩu phải có ít nhất 8 ký tự.";

        PasswordResetToken resetToken = tokenRepository
            .findByEmailAndOtpAndUsedFalseAndExpiryDateAfter(email.trim(), token.trim(), LocalDateTime.now())
            .orElse(null);

        if (resetToken == null) return "Mã OTP không đúng hoặc đã hết hạn. Vui lòng thử lại.";

        User user = userRepository.findByEmail(email.trim()).orElse(null);
        if (user == null) return "Người dùng không tồn tại.";

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return null;
    }
}
