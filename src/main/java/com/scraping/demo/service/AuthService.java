package com.scraping.demo.service;

import com.scraping.demo.dto.AuthResponse;
import com.scraping.demo.dto.LoginRequest;
import com.scraping.demo.dto.RegisterRequest;
import com.scraping.demo.dto.UserDTO;
import com.scraping.demo.entity.Role;
import com.scraping.demo.entity.User;
import com.scraping.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;

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
}
