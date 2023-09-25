package ru.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.rating.model.RatingSort;
import ru.practicum.ewm.rating.service.RatingService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PUBLIC;
import static ru.practicum.ewm.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = EVENT_PUBLIC)
@RequiredArgsConstructor
public class RatingPublicController {
    private final RatingService ratingService;

    @GetMapping("/top")
    public ResponseEntity<List<EventShortDto>> getTopBySortAndPages(
            @RequestParam @NotNull RatingSort sort,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(ratingService.getTopEventsBySortAndPages(sort, from, size));
    }

}
