package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.ewm.rating.service.RatingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = EVENT_PRIVATE)
@RequiredArgsConstructor
public class RatingPrivateController {
    private final RatingService ratingService;

    @PutMapping("/{eventId}/like")
    public ResponseEntity<Void> addLikeById(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long eventId,
                                            @RequestParam @NotNull Boolean isPositive,
                                            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        ratingService.addLikeByEventId(userId, eventId, isPositive);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{eventId}/like")
    public ResponseEntity<Void> removeLikeById(@PathVariable @Positive Long userId,
                                               @PathVariable @Positive Long eventId,
                                               HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        ratingService.removeLikeByEventId(userId, eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
