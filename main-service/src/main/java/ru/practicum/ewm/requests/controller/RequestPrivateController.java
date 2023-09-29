package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.ewm.constants.EndpointsPaths.REQUEST_PRIVATE;
import static ru.practicum.ewm.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = REQUEST_PRIVATE)
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getByUserId(@PathVariable @Positive Long userId,
                                                                     HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(requestService.getRequestsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable @Positive Long userId,
                                                          @Positive @RequestParam Long eventId,
                                                          HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(userId, eventId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelByUserId(@PathVariable @Positive Long userId,
                                                                  @PathVariable @Positive Long requestId,
                                                                  HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(requestService.cancelRequestByUser(userId, requestId));
    }

}
