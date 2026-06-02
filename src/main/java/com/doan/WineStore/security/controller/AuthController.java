package com.doan.WineStore.security.controller;

import com.doan.WineStore.enums.Role;
import com.doan.WineStore.security.dto.AdminAuthResponse;
import com.doan.WineStore.security.dto.AdminLoginRequest;
import com.doan.WineStore.security.jwt.JwtUtils;
import com.doan.WineStore.security.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/admin")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest loginRequest, HttpServletResponse response) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null
                || loginRequest.getUsername().isBlank() || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng nhập đầy đủ thông tin đăng nhập."));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername().trim(), loginRequest.getPassword()));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            if (userDetails.getAuthorities().stream()
                    .noneMatch(a -> a.getAuthority().equals("ROLE_" + Role.ADMIN.name()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Tài khoản không có quyền truy cập trang quản trị."));
            }

            String jwt = jwtUtils.generateJwtToken(userDetails, loginRequest.isRememberMe());

            ResponseCookie cookie = ResponseCookie.from(jwtUtils.getJwtCookieName(), jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(jwtUtils.getJwtExpirationMs(loginRequest.isRememberMe()) / 1000)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(new AdminAuthResponse(jwt, userDetails.getUsername()));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Tên đăng nhập hoặc mật khẩu không đúng."));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Đăng nhập thất bại."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(jwtUtils.getJwtCookieName(), "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công."));
    }
}