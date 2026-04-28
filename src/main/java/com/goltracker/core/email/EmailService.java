package com.goltracker.core.email;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${gol-tracker.app.url:http://localhost:3000}")
    private String appUrl;

    // ── Emails al usuario ──────────────────────────────────────────────────

    public void sendVerificationEmail(String to, String username, String code) {
        log.info("[EMAIL] Código de verificación para {} → {}", to, code);

        String body = """
                <div style="font-family:sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#009688">⚽ Gol!!! Tracker – Verificá tu correo</h2>
                  <p>Hola <strong>%s</strong>, gracias por registrarte.</p>
                  <p>Ingresá el siguiente código en la pantalla de verificación:</p>
                  <div style="margin:24px 0;text-align:center">
                    <span style="display:inline-block;padding:16px 32px;background:#f5f5f5;border-radius:12px;font-size:36px;font-weight:900;letter-spacing:10px;color:#111;border:2px solid #009688">
                      %s
                    </span>
                  </div>
                  <p style="color:#888;font-size:12px;margin-top:24px">
                    Este código expira en 1 hora. Si no te registraste, ignorá este mensaje.
                  </p>
                </div>
                """.formatted(username, code);

        send(to, "Código de verificación – Gol!!! Tracker", body);
    }

    public void sendPasswordResetEmail(String to, String username, String code, String resetLink) {
        log.info("[EMAIL] Reset de contraseña para {} → código {}", to, code);
        String body = """
                <div style="font-family:sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#009688">🔒 Gol!!! Tracker – Cambio de contraseña</h2>
                  <p>Hola <strong>%s</strong>, el administrador solicitó que cambies tu contraseña.</p>
                  <p>Hacé clic en el botón para continuar:</p>
                  <div style="margin:24px 0;text-align:center">
                    <a href="%s"
                       style="display:inline-block;padding:14px 32px;background:#009688;color:#fff;border-radius:12px;font-size:16px;font-weight:700;text-decoration:none">
                      Cambiar contraseña
                    </a>
                  </div>
                  <p style="margin-top:16px">Una vez en el formulario, ingresá este código de verificación:</p>
                  <div style="margin:16px 0;text-align:center">
                    <span style="display:inline-block;padding:14px 28px;background:#f5f5f5;border-radius:12px;font-size:36px;font-weight:900;letter-spacing:10px;color:#111;border:2px solid #009688">
                      %s
                    </span>
                  </div>
                  <p style="color:#888;font-size:12px;margin-top:24px">
                    Este enlace y código expiran en 20 minutos. Si no solicitaste este cambio, ignorá este mensaje.
                  </p>
                </div>
                """.formatted(username, resetLink, code);
        send(to, "Cambio de contraseña – Gol!!! Tracker", body);
    }

    public void sendApprovalEmail(String to, String username) {
        log.info("[EMAIL] Aprobación enviada a {}", to);
        String body = """
                <div style="font-family:sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#2e7d32">✅ ¡Tu cuenta fue aprobada!</h2>
                  <p>Hola <strong>%s</strong>, el administrador aprobó tu solicitud.</p>
                  <p>Ya podés iniciar sesión en <a href="%s">Gol!!! Tracker</a>.</p>
                </div>
                """.formatted(username, appUrl + "/login");

        send(to, "¡Cuenta aprobada! – Gol!!! Tracker", body);
    }

    public void sendRejectionEmail(String to, String username, String reason) {
        log.info("[EMAIL] Rechazo enviado a {}", to);
        String reasonHtml = (reason != null && !reason.isBlank())
                ? "<p>Motivo: <em>" + reason + "</em></p>"
                : "";
        String body = """
                <div style="font-family:sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#d32f2f">❌ Solicitud rechazada</h2>
                  <p>Hola <strong>%s</strong>, lamentablemente tu solicitud fue rechazada.</p>
                  %s
                  <p>Si creés que es un error, contactá al administrador.</p>
                </div>
                """.formatted(username, reasonHtml);

        send(to, "Solicitud rechazada – Gol!!! Tracker", body);
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private void send(String to, String subject, String htmlBody) {
        if (mailSender == null || fromEmail.isBlank()) {
            log.warn("[EMAIL] SMTP no configurado. Email '{}' para {} no fue enviado.", subject, to);
            return;
        }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(msg);
            log.info("[EMAIL] Enviado '{}' a {}", subject, to);
        } catch (Exception e) {
            log.error("[EMAIL] Error enviando '{}' a {}: {}", subject, to, e.getMessage());
        }
    }
}
