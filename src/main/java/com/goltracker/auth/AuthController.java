package com.goltracker.auth;

import com.goltracker.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("[Auth] Intento de login para usuario: '{}'", request.username());
        AuthResponse response = authService.login(request);
        log.info("[Auth] Login exitoso para '{}', role={}, requiresReset={}",
                request.username(), response.role(), response.requiresPasswordReset());
        return response;
    }

    /** Registro público: envía correo de verificación, NO devuelve JWT. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /** El usuario ingresa el código de 6 dígitos recibido por correo. */
    @PostMapping("/verify")
    public RegisterResponse verify(@Valid @RequestBody VerifyRequest request) {
        authService.verifyCode(request.email(), request.code());
        return new RegisterResponse(
                "¡Correo verificado! Ya podés iniciar sesión.",
                null, null
        );
    }

    /** Verifica el token del enlace + el código enviado por email. Devuelve un changeToken. */
    @PostMapping("/reset-password/verify")
    public PasswordResetVerifyResponse verifyResetCode(@Valid @RequestBody PasswordResetVerifyRequest request) {
        return authService.verifyResetCode(request);
    }

    /** Cambia la contraseña usando el changeToken obtenido tras verificar el código. */
    @PostMapping("/reset-password/change")
    public RegisterResponse changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        authService.changePassword(request);
        return new RegisterResponse("¡Contraseña actualizada! Ya podés iniciar sesión.", null, null);
    }
}
