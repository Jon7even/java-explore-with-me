package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.HitCreateTO;
import ru.practicum.ewm.stats.dto.HitResponseTO;
import ru.practicum.ewm.stats.mapper.StatMapper;
import ru.practicum.ewm.stats.service.StatService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.stats.dto.constants.Constants.DATE_TIME_HIT;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService service;

    @PostMapping("/hit")
    public ResponseEntity<Void> createHit(@RequestBody HitCreateTO hitCreateTO, HttpServletRequest request) {

        log.debug("On {} was used method {}", request.getRequestURL(), request.getMethod());

        service.createHit(hitCreateTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitResponseTO>> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_HIT) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_HIT) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique,
            HttpServletRequest request) {

        log.debug("On {} was used method {}", request.getRequestURL(), request.getMethod());

        return ResponseEntity.ok().body(service.getStats(StatMapper.toRequestDTO(start, end, uris, unique)));
    }

}
