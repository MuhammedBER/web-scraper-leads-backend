package com.scraping.demo.service;

import com.scraping.demo.dto.*;
import com.scraping.demo.entity.Role;
import com.scraping.demo.entity.User;
import com.scraping.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final EmailService emailService;

        public AuthResponse register(RegisterRequest request) {
                var user = User.builder()
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.USER) // Default role
                                .build();
                userRepository.save(user);

                // Return null token for registration - user needs to login
                return AuthResponse.builder()
                                .token(null)
                                .user(null)
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow();

                var jwtToken = jwtService.generateToken(user);

                var userDTO = UserDTO.builder()
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();

                return AuthResponse.builder()
                                .token(jwtToken)
                                .user(userDTO)
                                .build();
        }

        public void forgotPassword(ForgotPasswordRequest request) {
                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String code = String.format("%06d", new Random().nextInt(999999));
                user.setPasswordResetCode(code);
                user.setResetCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
                userRepository.save(user);

                emailService.sendPasswordResetCode(user.getEmail(), code);
        }

        public void resetPassword(ResetPasswordRequest request) {
                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (user.getPasswordResetCode() == null || !user.getPasswordResetCode().equals(request.getCode())) {
                        throw new RuntimeException("Invalid verification code");
                }

                if (user.getResetCodeExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("Verification code has expired");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                user.setPasswordResetCode(null);
                user.setResetCodeExpiresAt(null);
                userRepository.save(user);
        }

        public void verifyResetCode(VerifyResetCodeRequest request) {
                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (user.getPasswordResetCode() == null || !user.getPasswordResetCode().equals(request.getCode())) {
                        throw new RuntimeException("Invalid verification code");
                }

                if (user.getResetCodeExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("Verification code has expired");
                }
        }
}
