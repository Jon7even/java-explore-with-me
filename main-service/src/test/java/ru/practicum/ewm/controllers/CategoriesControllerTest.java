package ru.practicum.ewm.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.setup.GenericControllerTest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.constants.NamesExceptions.DUPLICATE_CATEGORY;

public class CategoriesControllerTest extends GenericControllerTest {
    @Autowired
    protected CategoryService categoryService;

    @BeforeEach
    void setUp() {
        initNewCategoryDto();
    }

    @Test
    @DisplayName("Новая категория должна создаться с релевантными полями [createCategory]")
    void shouldCreateCategory_thenStatus201() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));
    }

    @Test
    @DisplayName("Новая категория не должна создаться [createCategory]")
    void shouldNotCreateUser_thenStatus400And409() throws Exception {
        thirdNewCategoryDto.setName("testtesttesttesttesttesttesttesttesttesttesttesttest");
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(thirdNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: name. Error: must not go beyond. Min=1, Max=50 symbols"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        secondNewCategoryDto.setName(" ");
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(secondNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: name. Error: must not be blank. Value: null"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        categoryService.createCategory(firstNewCategoryDto);
        mockMvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(DUPLICATE_CATEGORY))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Категория должна удалиться по [ID] [removeCategoryById]")
    void shouldDeleteCategoryById_thenStatus204And404() throws Exception {
        categoryService.createCategory(firstNewCategoryDto);
        Long idFirstCategory = categoryService.getCategoryById(FIRST_ID).getId();

        mockMvc.perform(delete("/admin/categories/{catId}", SECOND_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Category with id=" + SECOND_ID + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(delete("/admin/categories/{catId}", idFirstCategory))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/admin/categories/{catId}", idFirstCategory))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Категория должна обновить имя [updateCategoryById]")
    void shouldUpdateCategory_thenStatus200() throws Exception {
        categoryService.createCategory(firstNewCategoryDto);

        firstNewCategoryDto.setName("UpdatedCategory");
        mockMvc.perform(patch("/admin/categories/{catId}", FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));
        firstNewCategoryDto.setName("UpdatedCategory");

        mockMvc.perform(patch("/admin/categories/{catId}", FIRST_ID)
                        .content(objectMapper.writeValueAsString(firstNewCategoryDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));
    }

    @Test
    @DisplayName("Получить все категории [getCategoriesByPage]")
    void shouldGetAllCategories_thenStatus200() throws Exception {
        CategoryDto category1 = categoryService.createCategory(firstNewCategoryDto);
        CategoryDto category2 = categoryService.createCategory(secondNewCategoryDto);
        CategoryDto category3 = categoryService.createCategory(thirdNewCategoryDto);

        mockMvc.perform(get("/categories"))
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
    @DisplayName("Поиск категории по [ID] [getCategoryById]")
    void shouldGetCategoryById_thenStatus200AndStatus404() throws Exception {
        categoryService.createCategory(firstNewCategoryDto);

        mockMvc.perform(get("/categories/{id}", FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("name").value(firstNewCategoryDto.getName()));

        mockMvc.perform(get("/categories/{id}", SECOND_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Category with id=" + SECOND_ID + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

}
