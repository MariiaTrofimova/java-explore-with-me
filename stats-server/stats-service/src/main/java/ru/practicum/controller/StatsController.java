package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHitDto endpointHitDto) {
        service.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam String start,
                                      @RequestParam String end,
                                      @RequestParam(required = false, defaultValue = "") List<String> uris,
                                      @RequestParam(required = false, defaultValue = "false") boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}