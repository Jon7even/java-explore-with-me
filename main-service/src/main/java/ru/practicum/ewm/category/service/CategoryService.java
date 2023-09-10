package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Integer catId);

    CategoryDto updateCategory(NewCategoryDto newCategoryDto, Integer catId);

    List<CategoryDto> getCategoriesByPage(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);
}
