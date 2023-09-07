package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.CategoryEntity;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.EntityNotDeletedException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.utils.ConverterPage;

import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.constants.CommonSort.DEFAULT_SORT_BY_ID;
import static ru.practicum.ewm.constants.NamesExceptions.DUPLICATE_CATEGORY;
import static ru.practicum.ewm.constants.NamesLogsInService.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.debug("New newCategoryDto came {} [newCategoryDto={}]", SERVICE_FROM_CONTROLLER, newCategoryDto);
        checkNameOnDuplicate(newCategoryDto.getName());
        CategoryEntity category = CategoryMapper.INSTANCE.toEntityFromDTOCreate(newCategoryDto);

        log.debug("Add new entity [category={}] {}", category, SERVICE_IN_DB);
        CategoryEntity createdCategory = categoryRepository.save(category);

        log.debug("New category has returned [category={}] {}", createdCategory, SERVICE_FROM_DB);
        return CategoryMapper.INSTANCE.toDTOResponseFromEntity(createdCategory);
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long catId) {
        existDoesCategoryEntityById(catId);

        log.debug("Remove [idCategory={}] {}", catId, SERVICE_IN_DB);
        categoryRepository.deleteById(catId);
        boolean isRemoved = categoryRepository.existsById(catId);

        if (!isRemoved) {
            log.debug("Category by [id={}] has removed {}", catId, SERVICE_FROM_DB);
        } else {
            log.error("Category by [id={}] was not removed", catId);
            throw new EntityNotDeletedException(String.format("Category with id=%d was not deleted", catId));
        }
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(NewCategoryDto newCategoryDto, Long catId) {
        log.debug("Category for update came {} [newCategoryDto={}]", SERVICE_FROM_CONTROLLER, newCategoryDto);

        CategoryEntity category = CategoryMapper.INSTANCE.toEntityFromDTOUpdate(newCategoryDto, catId);
        CategoryEntity checkedCategoryFromRepository = findCategoryEntityById(catId);

        if (category.getName().equals(checkedCategoryFromRepository.getName())) {
            log.warn("No need to update category data [categoryUpdate={}] [categoryResult={}]",
                    category, checkedCategoryFromRepository);
            return CategoryMapper.INSTANCE.toDTOResponseFromEntity(category);
        } else {
            log.debug("Update category [category={}] {}", category, SERVICE_IN_DB);
            CategoryEntity updatedCategoryEntity = categoryRepository.save(category);

            log.debug("Updated category has returned [category={}] {}", updatedCategoryEntity, SERVICE_FROM_DB);
            return CategoryMapper.INSTANCE.toDTOResponseFromEntity(updatedCategoryEntity);
        }
    }

    @Override
    public List<CategoryDto> getCategoriesByPage(Integer from, Integer size) {
        log.debug("Get all categories {}", SERVICE_IN_DB);

        Pageable pageable = ConverterPage.getPageRequest(from, size, Optional.of(DEFAULT_SORT_BY_ID));
        List<CategoryEntity> listCategories = categoryRepository.findAll(pageable).getContent();

        if (listCategories.isEmpty()) {
            log.debug("Has returned empty list categories {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list categories [count={}] {}", listCategories.size(), SERVICE_FROM_DB);
        }

        return CategoryMapper.INSTANCE.toDTOResponseFromEntityList(listCategories);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        log.debug("Get category by [catId={}] {}", catId, SERVICE_IN_DB);
        Optional<CategoryEntity> foundCategory = categoryRepository.findById(catId);

        if (foundCategory.isPresent()) {
            log.debug("Found [category={}] {}", foundCategory.get(), SERVICE_FROM_DB);
            return CategoryMapper.INSTANCE.toDTOResponseFromEntity(foundCategory.get());
        } else {
            log.warn("Category by [catId={}] was not found", catId);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", catId));
        }
    }

    private void existDoesCategoryEntityById(Long idCategory) {
        log.debug("Start check exist [idCategory={}] {}", idCategory, SERVICE_IN_DB);

        if (categoryRepository.existsById(idCategory)) {
            log.debug("Check was successful found [idCategory={}] {}", idCategory, SERVICE_FROM_DB);
        } else {
            log.warn("Category by [id={}] was not found", idCategory);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", idCategory));
        }
    }

    private CategoryEntity findCategoryEntityById(Long idCategory) {
        log.debug("Get category entity for checking by [idItem={}] {}", idCategory, SERVICE_IN_DB);
        Optional<CategoryEntity> foundCheckCategory = categoryRepository.findById(idCategory);

        if (foundCheckCategory.isPresent()) {
            log.debug("Check was successful found [category={}] {}", foundCheckCategory.get(), SERVICE_FROM_DB);
            return foundCheckCategory.get();
        } else {
            log.warn("Category by [id={}] was not found", idCategory);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", idCategory));
        }
    }

    private void checkNameOnDuplicate(String name) {
        if (categoryRepository.existsByName(name)) {
            log.debug("Checked name={} fail: name category already exist in DB", name);
            throw new IntegrityConstraintException(DUPLICATE_CATEGORY);
        }
    }

}
