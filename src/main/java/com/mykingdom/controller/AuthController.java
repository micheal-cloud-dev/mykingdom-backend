package com.mykingdom.controller;

import com.mykingdom.dto.ApiResponse;
import com.mykingdom.dto.LoginRequest;
import com.mykingdom.dto.LoginResponse;
import com.mykingdom.dto.RefreshTokenRequest;
import com.mykingdom.model.User;
import com.mykingdom.repository.UserRepository;
import com.mykingdom.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(
                    request.getUsername());

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow();

            LoginResponse response = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();

            log.info("✅ Login successful: {}", request.getUsername());
            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", response));

        } catch (BadCredentialsException e) {
            log.warn("❌ Bad credentials for: {}", request.getUsername());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid username or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("Account deactivated. Contact admin."));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Server error: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody RefreshTokenRequest request) {
        if (tokenProvider.validateToken(request.getRefreshToken())) {
            String username = tokenProvider.getUsernameFromToken(
                    request.getRefreshToken());
            String newAccessToken =
                    tokenProvider.generateAccessTokenFromUsername(username);
            String newRefreshToken = tokenProvider.generateRefreshToken(username);

            return ResponseEntity.ok(ApiResponse.success("Token refreshed",
                    LoginResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .tokenType("Bearer")
                            .username(username)
                            .build()));
        }
        return ResponseEntity.status(401)
                .body(ApiResponse.error("Invalid or expired refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    // Temporary password reset - REMOVE AFTER TESTING
    @GetMapping("/setup")
    public ResponseEntity<String> setup() {
        try {
            userRepository.deleteAll();

            User admin = User.builder()
                    .username("admin")
                    .email("admin@mykingdom.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(User.Role.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);

            User teacher = User.builder()
                    .username("teacher1")
                    .email("teacher1@mykingdom.com")
                    .password(passwordEncoder.encode("Teacher@123"))
                    .role(User.Role.TEACHER)
                    .isActive(true)
                    .build();
            userRepository.save(teacher);

            return ResponseEntity.ok(
                    "✅ Setup complete!\n" +
                            "Admin: username=admin password=Admin@123\n" +
                            "Teacher: username=teacher1 password=Teacher@123"
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}