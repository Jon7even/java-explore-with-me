package ru.practicum.ewm.rating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.rating.model.RatingEntity;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    @Mapping(source = "liker", target = "liker")
    @Mapping(source = "rating.is_positive", target = "is_positive")
    @Mapping(source = "dateTime", target = "dateTime")
    RatingDto toDTOResponseFromEntity(RatingEntity rating, UserShortDto liker, LocalDateTime dateTime);
}
