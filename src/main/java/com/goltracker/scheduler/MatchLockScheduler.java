package com.goltracker.scheduler;

import com.goltracker.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Revisa cada minuto si algún partido tiene kickoffAt dentro de los próximos
 * 5 minutos y, de ser así, cierra automáticamente las predicciones.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchLockScheduler {

    private final AdminService adminService;

    @Scheduled(fixedDelay = 60_000)        // cada 60 segundos
    public void checkAndLock() {
        int locked = adminService.autoLockDueMatches();
        if (locked > 0) {
            log.info("[Scheduler] {} partido(s) bloqueados automáticamente.", locked);
        }
    }

    @Scheduled(fixedDelay = 3_600_000)    // cada hora
    public void checkAndNotify() {
        int notified = adminService.sendMatchNotifications();
        if (notified > 0) {
            log.info("[Scheduler] Recordatorios enviados para {} partido(s).", notified);
        }
    }
}
