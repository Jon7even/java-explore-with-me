package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.service.EventService;

import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PUBLIC;

@Slf4j
@RestController
@RequestMapping(path = EVENT_PUBLIC)
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;
}
