package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = EVENT_PRIVATE)
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getShortListByUserId(
            @PathVariable @Positive Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.getShortEventListByUserId(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable @Positive Long userId,
                                               @Valid @RequestBody NewEventDto newEventDto,
                                               HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(newEventDto, userId));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getFullById(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long eventId,
                                                    HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.getFullEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateById(@PathVariable @Positive Long userId,
                                                   @PathVariable @Positive Long eventId,
                                                   @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest,
                                                   HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.updateEventById(userId, eventId, updateEventUserRequest));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequestsByInitiator(@PathVariable @Positive Long userId,
                                                                                   @PathVariable @Positive Long eventId,
                                                                                   HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.getAllRequestsByInitiator(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> confirmRequestByInitiator(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(eventService.confirmRequestByInitiator(userId, eventId, updateRequest));
    }

    @PutMapping("/{eventId}/like")
    public ResponseEntity<Void> addLikeById(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId,
                                            @RequestParam @NotNull Boolean isPositive,
                                            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        eventService.addLikeByEventId(userId, eventId, isPositive);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<Void> removeLikeById(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId,
                                               HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        eventService.removeLikeByEventId(userId, eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
