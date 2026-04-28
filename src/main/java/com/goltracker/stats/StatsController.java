package com.goltracker.stats;

import com.goltracker.user.domain.Role;
import com.goltracker.user.domain.UserStatus;
import com.goltracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final UserRepository userRepository;

    @GetMapping
    public Map<String, Long> getStats() {
        long participants = userRepository.countByStatusAndRole(UserStatus.ACTIVE, Role.USER);
        return Map.of("participants", participants);
    }
}
