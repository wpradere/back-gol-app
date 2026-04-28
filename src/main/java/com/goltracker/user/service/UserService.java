package com.goltracker.user.service;

import com.goltracker.core.email.EmailService;
import com.goltracker.core.exception.ApiException;
import com.goltracker.user.domain.User;
import com.goltracker.user.domain.UserStatus;
import com.goltracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    // ── Registro público (con verificación de correo) ──────────────────────

    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw ApiException.conflict("El usuario '" + username + "' ya existe");
        }
        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("El correo '" + email + "' ya está registrado");
        }

        String code = String.format("%06d", new Random().nextInt(1_000_000));
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .status(UserStatus.PENDING_VERIFICATION)
                .verificationToken(code)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(1))
                .build();

        userRepository.save(user);
        emailService.sendVerificationEmail(email, username, code);
        return user;
    }

    // ── Verificación de correo por código ────────────────────────────────────

    @Transactional
    public void verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.badRequest("No existe una cuenta con ese correo"));

        if (user.getStatus() != UserStatus.PENDING_VERIFICATION) {
            throw ApiException.badRequest("Este correo ya fue verificado");
        }
        if (user.getVerificationTokenExpiry() == null ||
                user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw ApiException.badRequest("El código expiró. Registrate nuevamente.");
        }
        if (!code.equals(user.getVerificationToken())) {
            throw ApiException.badRequest("Código incorrecto");
        }

        user.setStatus(UserStatus.PENDING_APPROVAL);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    // ── Aprobación / rechazo (llamado desde AdminService) ─────────────────

    @Transactional
    public void approveUser(Long userId) {
        User user = getById(userId);
        if (user.getStatus() != UserStatus.PENDING_APPROVAL) {
            throw ApiException.badRequest("El usuario no está pendiente de aprobación");
        }
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        if (user.getEmail() != null) {
            emailService.sendApprovalEmail(user.getEmail(), user.getUsername());
        }
    }

    @Transactional
    public void rejectUser(Long userId, String reason) {
        User user = getById(userId);
        if (user.getStatus() != UserStatus.PENDING_APPROVAL) {
            throw ApiException.badRequest("El usuario no está pendiente de aprobación");
        }
        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);
        if (user.getEmail() != null) {
            emailService.sendRejectionEmail(user.getEmail(), user.getUsername(), reason);
        }
    }

    // ── Crear usuario admin directamente (seeder) ─────────────────────────

    @Transactional
    public User createAdminUser(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) return userRepository.findByUsername(username).get();
        return userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .status(UserStatus.ACTIVE)
                .role(com.goltracker.user.domain.Role.ADMIN)
                .build());
    }

    // ── Consultas ─────────────────────────────────────────────────────────

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
    }

    public List<User> findPendingApproval() {
        return userRepository.findByStatus(UserStatus.PENDING_APPROVAL);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado: " + id));
    }
}
