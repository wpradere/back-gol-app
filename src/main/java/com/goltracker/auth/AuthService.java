package com.goltracker.auth;

import com.goltracker.auth.dto.*;
import com.goltracker.core.email.EmailService;
import com.goltracker.core.exception.ApiException;
import com.goltracker.core.security.JwtService;
import com.goltracker.user.domain.User;
import com.goltracker.user.domain.UserStatus;
import com.goltracker.user.repository.UserRepository;
import com.goltracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService            jwtService;
    private final UserService           userService;
    private final UserRepository        userRepository;
    private final EmailService          emailService;
    private final PasswordEncoder       passwordEncoder;

    @Value("${gol-tracker.app.url:http://localhost:3000}")
    private String appUrl;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userService.findByUsername(request.username());
            log.info("[Auth] Usuario encontrado: '{}', status={}, role={}",
                    user.getUsername(), user.getStatus(), user.getRole());
            if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
                log.warn("[Auth] Login rechazado — usuario '{}' tiene status PENDING_VERIFICATION", request.username());
                throw ApiException.badRequest("Debés verificar tu correo antes de iniciar sesión.");
            }
            if (user.getStatus() == UserStatus.REJECTED) {
                log.warn("[Auth] Login rechazado — usuario '{}' tiene status REJECTED", request.username());
                throw ApiException.badRequest("Tu solicitud fue rechazada. Contactá al administrador.");
            }
        } catch (UsernameNotFoundException e) {
            log.warn("[Auth] Usuario '{}' no encontrado en la base de datos", request.username());
        }

        log.info("[Auth] Autenticando credenciales para '{}'", request.username());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userService.findByUsername(request.username());

        if (user.isForcePasswordReset()) {
            String resetToken = UUID.randomUUID().toString();
            String code       = String.format("%06d", new Random().nextInt(1_000_000));
            user.setResetToken(resetToken);
            user.setResetCode(code);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(20));
            userRepository.save(user);

            String link = appUrl + "/reset-password?token=" + resetToken;
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), code, link);

            return new AuthResponse(null, user.getUsername(), user.getRole().name(),
                    user.getStatus().name(), true);
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getRole().name(),
                user.getStatus().name(), false);
    }

    public RegisterResponse register(RegisterRequest request) {
        userService.register(request.username(), request.email(), request.password());
        return new RegisterResponse(
                "Te enviamos un correo de verificación a " + request.email() + ". Revisá tu bandeja de entrada.",
                request.username(),
                request.email()
        );
    }

    public void verifyCode(String email, String code) {
        userService.verifyCode(email, code);
    }

    @Transactional
    public PasswordResetVerifyResponse verifyResetCode(PasswordResetVerifyRequest request) {
        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> ApiException.badRequest("Enlace inválido o expirado."));

        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw ApiException.badRequest("El enlace expiró. Iniciá sesión nuevamente para recibir uno nuevo.");
        }
        if (!request.code().equals(user.getResetCode())) {
            throw ApiException.badRequest("El código es incorrecto.");
        }

        // Generar changeToken (reutilizamos resetToken con nueva expiración corta)
        String changeToken = UUID.randomUUID().toString();
        user.setResetToken(changeToken);
        user.setResetCode(null);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        return new PasswordResetVerifyResponse(changeToken);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        User user = userRepository.findByResetToken(request.changeToken())
                .orElseThrow(() -> ApiException.badRequest("Token inválido o expirado."));

        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw ApiException.badRequest("El token expiró. Iniciá sesión nuevamente.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setForcePasswordReset(false);
        user.setResetToken(null);
        user.setResetCode(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
