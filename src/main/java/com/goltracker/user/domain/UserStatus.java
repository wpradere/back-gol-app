package com.goltracker.user.domain;

public enum UserStatus {
    PENDING_VERIFICATION,  // registrado, esperando verificar correo
    PENDING_APPROVAL,      // correo verificado, esperando aprobación del admin
    ACTIVE,                // aprobado, puede iniciar sesión
    REJECTED               // rechazado por el admin
}
