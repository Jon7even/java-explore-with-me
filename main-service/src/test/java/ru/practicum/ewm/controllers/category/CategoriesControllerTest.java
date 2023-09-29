package ru.practicum.ewm.controllers.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.events.dto.Location;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.setup.GenericControllerTest;
import ru.practicum.ewm.users.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.constants.EndpointsPaths.CATEGORY_ADMIN;
import static ru.practicum.ewm.constants.EndpointsPaths.CATEGORY_PUBLIC;
import static ru.practicum.ewm.constants.NamesExceptions.CATEGORY_ALREADY_USED;
import static ru.practicum.ewm.constants.NamesExceptions.DUPLICATE_CATEGORY;

public class CategoriesControllerTest extends GenericControllerTest {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    protected UserService userService;

    @Autowired
    private EventService eventService;

    @BeforeEach
    void setUp() {
        initNewCategoryDto();
    }

    @Test
    @DisplayName("Новая категория должна создаться с релевантными полями [create]")
    void shouldCreateCategory_thenStatus201() throws Exception {
        mockMvc.perform(post(CATEGORY_ADMIN)
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(firstIdInteger))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));
    }

    @Test
    @DisplayName("Новая категория не должна создаться [create]")
    void shouldNotCreateUser_thenStatus400And409() throws Exception {
        thirdNewCategoryDto.setName("testtesttesttesttesttesttesttesttesttesttesttesttest");
        mockMvc.perform(post(CATEGORY_ADMIN)
                        .content(objectMapper.writeValueAsString(thirdNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: name. Error: must not go beyond. Min=1, Max=50 symbols"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        secondNewCategoryDto.setName(" ");
        mockMvc.perform(post(CATEGORY_ADMIN)
                        .content(objectMapper.writeValueAsString(secondNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: name. Error: must not be blank. Value: null"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        categoryService.createCategory(firstNewCategoryDto);
        mockMvc.perform(post(CATEGORY_ADMIN)
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(DUPLICATE_CATEGORY))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Категория должна удалиться по [ID] [removeById]")
    void shouldDeleteCategoryById_thenStatus204And404And409() throws Exception {
        categoryService.createCategory(firstNewCategoryDto);
        Integer idFirstCategory = categoryService.getCategoryById(firstIdInteger).getId();

        mockMvc.perform(delete(CATEGORY_ADMIN + "/{catId}", secondIdInteger))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Category with id=" + secondIdInteger + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(delete(CATEGORY_ADMIN + "/{catId}", idFirstCategory))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(CATEGORY_ADMIN + "/{catId}", idFirstCategory))
                .andExpect(status().isNotFound());

        initNewUserRequest();
        userService.createUser(firstNewUserRequest);
        categoryService.createCategory(secondNewCategoryDto);
        Location location = Location.builder()
                .lat(55.4401)
                .lon(37.3518)
                .build();
        NewEventDto newEventDtoFieldsDefault = NewEventDto.builder()
                .annotation("Test annotation for annotation Default")
                .category(secondIdInteger)
                .description("Test description for description Default")
                .eventDate(LocalDateTime.now().plusMonths(1))
                .location(location)
                .title("Test Title Default")
                .build();
        eventService.createEvent(newEventDtoFieldsDefault, firstId);
        mockMvc.perform(delete(CATEGORY_ADMIN + "/{catId}", secondId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(CATEGORY_ALREADY_USED))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Категория должна обновить имя [updateById]")
    void shouldUpdateCategory_thenStatus200() throws Exception {
        categoryService.createCategory(firstNewCategoryDto);

        firstNewCategoryDto.setName("UpdatedCategory");
        mockMvc.perform(patch(CATEGORY_ADMIN + "/{catId}", firstIdInteger)
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(firstIdInteger))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));
        firstNewCategoryDto.setName("UpdatedCategory");

        firstNewCategoryDto.setName("Set new name");
        mockMvc.perform(patch(CATEGORY_ADMIN + "/{catId}", firstIdInteger)
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(firstIdInteger))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));
    }

    @Test
    @DisplayName("Получить все категории [getByPage]")
    void shouldGetAllCategories_thenStatus200() throws Exception {
        CategoryDto category1 = categoryService.createCategory(firstNewCategoryDto);
        CategoryDto category2 = categoryService.createCategory(secondNewCategoryDto);
        CategoryDto category3 = categoryService.createCategory(thirdNewCategoryDto);

        mockMvc.perform(get(CATEGORY_PUBLIC))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(category1.getId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[1].id").value(category2.getId()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()))
                .andExpect(jsonPath("$[2].id").value(category3.getId()))
                .andExpect(jsonPath("$[2].name").value(category3.getName()));

        mockMvc.perform(get("/categories?from={from}&size={size}", 0, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(category1.getId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()));
    }

    @Test
    @DisplayName("Поиск категории по [ID] [getById]")
    void shouldGetCategoryById_thenStatus200AndStatus404() throws Exception {
        categoryService.createCategory(firstNewCategoryDto);

        mockMvc.perform(get("/categories/{id}", firstIdInteger))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(firstIdInteger))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));

        mockMvc.perform(get("/categories/{id}", secondIdInteger))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Category with id=" + secondIdInteger + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

}
