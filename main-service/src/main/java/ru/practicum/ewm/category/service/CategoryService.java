package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId);

    List<CategoryDto> getCategoriesByPage(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
