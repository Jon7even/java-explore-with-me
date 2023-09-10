package ru.practicum.ewm.setup;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.events.dto.Location;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.users.service.UserService;

import java.time.LocalDateTime;

public class GenericControllerEvents extends GenericControllerTest {
    @Autowired
    protected UserService userService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected EventService eventService;

    protected Integer zero = 0;

    protected LocalDateTime now;

    protected Location location;

    protected NewEventDto newEventDtoStandard;

    protected NewEventDto newEventDtoRequestModerationFalse;

    protected NewEventDto newEventDtoParticipantLimitTen;

    protected NewEventDto newEventDtoPaidTrue;

    protected NewEventDto newEventDtoFieldsDefault;

    @BeforeEach
    void setUp() {
        initNewUserRequest();
        initNewCategoryDto();

        categoryService.createCategory(firstNewCategoryDto);
        userService.createUser(firstNewUserRequest);
        userService.createUser(secondNewUserRequest);

        location = Location.builder()
                .lat(55.4401)
                .lon(37.3518)
                .build();

        now = LocalDateTime.now();

        initNewEventDto();
    }

    protected void initNewEventDto() {
        newEventDtoStandard = NewEventDto.builder()
                .annotation("Test annotation for annotation Standard")
                .category(FIRST_ID_INTEGER)
                .description("Test description for description Standard")
                .eventDate(now.plusHours(3))
                .location(location)
                .paid(false)
                .participantLimit(0)
                .requestModeration(true)
                .title("Test Title Standard")
                .build();

        newEventDtoRequestModerationFalse = NewEventDto.builder()
                .annotation("Test annotation for annotation RequestModerationFalse")
                .category(FIRST_ID_INTEGER)
                .description("Test description for description RequestModerationFalse")
                .eventDate(now.plusHours(10))
                .location(location)
                .paid(false)
                .participantLimit(0)
                .requestModeration(false)
                .title("Test Title RequestModerationFalse")
                .build();

        newEventDtoParticipantLimitTen = NewEventDto.builder()
                .annotation("Test annotation for annotation ParticipantLimitTen")
                .category(FIRST_ID_INTEGER)
                .description("Test description for description ParticipantLimitTen")
                .eventDate(now.plusDays(1))
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(true)
                .title("Test Title ParticipantLimitTen")
                .build();

        newEventDtoPaidTrue = NewEventDto.builder()
                .annotation("Test annotation for annotation PaidTrue")
                .category(FIRST_ID_INTEGER)
                .description("Test description for description PaidTrue")
                .eventDate(now.plusWeeks(1))
                .location(location)
                .paid(true)
                .participantLimit(0)
                .requestModeration(true)
                .title("Test Title PaidTrue")
                .build();

        newEventDtoFieldsDefault = NewEventDto.builder()
                .annotation("Test annotation for annotation Default")
                .category(FIRST_ID_INTEGER)
                .description("Test description for description Default")
                .eventDate(now.plusMonths(1))
                .location(location)
                .title("Test Title Default")
                .build();
    }

}
