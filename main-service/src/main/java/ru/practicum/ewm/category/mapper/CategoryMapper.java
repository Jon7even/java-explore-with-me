package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.CategoryEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "newCategoryDto.name", target = "name")
    CategoryEntity toEntityFromDTOCreate(NewCategoryDto newCategoryDto);

    @Mapping(source = "categoryEntity.id", target = "id")
    @Mapping(source = "categoryEntity.name", target = "name")
    CategoryDto toDTOResponseFromEntity(CategoryEntity categoryEntity);

    @Mapping(source = "categoryEntity.id", target = "id")
    @Mapping(source = "categoryEntity.name", target = "name")
    List<CategoryDto> toDTOResponseFromEntityList(List<CategoryEntity> listCategories);

    @Mapping(source = "catId", target = "id")
    @Mapping(source = "newCategoryDto.name", target = "name")
    CategoryEntity toEntityFromDTOUpdate(NewCategoryDto newCategoryDto, Long catId);

}
