package ru.practicum.ewm.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.model.RatingEntity;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    @Mapping(source = "rating.liker.id", target = "liker")
    @Mapping(source = "rating.createdOn", target = "dateTime")
    RatingDto toDTOResponseFromEntity(RatingEntity rating);

    @Mapping(source = "rating.liker.id", target = "liker")
    @Mapping(source = "rating.createdOn", target = "dateTime")
    Set<RatingDto> toListDTOResponseFromListEntity(Set<RatingEntity> rating);
}
