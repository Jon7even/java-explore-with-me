package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.ParamsSortDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_ADMIN;
import static ru.practicum.ewm.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = EVENT_ADMIN)
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getByParams(
            @RequestParam(defaultValue = "#{T(java.util.Collections).emptyList()}") List<Long> users,
            @RequestParam(defaultValue = "#{T(java.util.Collections).emptyList()}") List<EventState> states,
            @RequestParam(defaultValue = "#{T(java.util.Collections).emptyList()}") List<Long> categories,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_DEFAULT) LocalDateTime rangeStart,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_DEFAULT) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.getListEventByParams(
                ParamsSortDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build())
        );
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> confirm(@PathVariable @Positive Long eventId,
                                                @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                                HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.confirmEvent(eventId, updateEventAdminRequest));
    }

}
