package com.goltracker.scheduler;

import com.goltracker.admin.service.AdminService;
import com.goltracker.knockout.service.KnockoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchLockScheduler {

    private final AdminService adminService;
    private final KnockoutService knockoutService;

    @Scheduled(fixedDelay = 60_000)        // cada 60 segundos
    public void checkAndLock() {
        int locked = adminService.autoLockDueMatches();
        if (locked > 0) {
            log.info("[Scheduler] {} partido(s) bloqueados automáticamente.", locked);
        }
        int koLocked = knockoutService.autoLockDueMatches();
        if (koLocked > 0) {
            log.info("[Scheduler] {} partido(s) de fase finalista bloqueados automáticamente.", koLocked);
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
