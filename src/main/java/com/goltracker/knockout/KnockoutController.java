package com.goltracker.knockout;

import com.goltracker.knockout.dto.KnockoutBracketDto;
import com.goltracker.knockout.service.KnockoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knockout")
@RequiredArgsConstructor
public class KnockoutController {

    private final KnockoutService knockoutService;

    @GetMapping
    public KnockoutBracketDto getBracket() {
        return knockoutService.getPublicBracket();
    }
}
