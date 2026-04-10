package com.shopflow.auth.service;

import com.shopflow.auth.domain.User;
import com.shopflow.auth.dto.UserRequest;
import com.shopflow.auth.dto.UserResponse;
import com.shopflow.auth.repository.UserRepository;
import com.shopflow.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shopflow.auth.dto.LoginRequest;
import com.shopflow.auth.dto.TokenResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email cadastrado");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.valueOf(request.getRole() != null ? request.getRole() : "CUSTOMER"))
                .build();

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.generateToken(user);
        return new TokenResponse(token, "Bearer", user.getId(), user.getEmail(), user.getRole().name());
    }
}
