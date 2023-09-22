package ru.practicum.ewm.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.CategoryEntity;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.LocationEntity;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.model.UserEntity;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "newEventDto.annotation", target = "annotation")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(source = "now", target = "createdOn")
    @Mapping(source = "newEventDto.description", target = "description")
    @Mapping(source = "newEventDto.eventDate", target = "eventDate")
    @Mapping(source = "user", target = "initiator")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "newEventDto.paid", target = "paid")
    @Mapping(source = "newEventDto.participantLimit", target = "participantLimit")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(source = "newEventDto.requestModeration", target = "requestModeration")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "newEventDto.title", target = "title")
    @Mapping(target = "views", ignore = true)
    EventEntity toEntityFromDTOCreate(NewEventDto newEventDto, UserEntity user, EventState state,
                                      CategoryEntity category, LocalDateTime now, LocationEntity location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "eventDto.annotation", target = "annotation")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(source = "eventDto.description", target = "description")
    @Mapping(source = "eventDto.eventDate", target = "eventDate")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(source = "eventDto.paid", target = "paid")
    @Mapping(source = "eventDto.participantLimit", target = "participantLimit")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(source = "eventDto.requestModeration", target = "requestModeration")
    @Mapping(target = "state", ignore = true)
    @Mapping(source = "eventDto.title", target = "title")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "disLikes", ignore = true)
    void updateEntityFromDTO(UpdateEventUserRequest eventDto, @MappingTarget EventEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "eventDto.annotation", target = "annotation")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(source = "eventDto.description", target = "description")
    @Mapping(source = "eventDto.eventDate", target = "eventDate")
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(source = "eventDto.paid", target = "paid")
    @Mapping(source = "eventDto.participantLimit", target = "participantLimit")
    @Mapping(source = "now", target = "publishedOn")
    @Mapping(source = "eventDto.requestModeration", target = "requestModeration")
    @Mapping(target = "state", ignore = true)
    @Mapping(source = "eventDto.title", target = "title")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "disLikes", ignore = true)
    void updateEntityFromDTO(UpdateEventAdminRequest eventDto, @MappingTarget EventEntity entity, LocalDateTime now);

    @Mapping(source = "eventEntity.annotation", target = "annotation")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "eventEntity.confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "eventEntity.createdOn", target = "createdOn")
    @Mapping(source = "eventEntity.description", target = "description")
    @Mapping(source = "eventEntity.eventDate", target = "eventDate")
    @Mapping(source = "eventEntity.id", target = "id")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "eventEntity.paid", target = "paid")
    @Mapping(source = "eventEntity.participantLimit", target = "participantLimit")
    @Mapping(source = "eventEntity.publishedOn", target = "publishedOn")
    @Mapping(source = "eventEntity.requestModeration", target = "requestModeration")
    @Mapping(source = "eventEntity.state", target = "state")
    @Mapping(source = "eventEntity.title", target = "title")
    @Mapping(source = "eventEntity.views", target = "views")
    @Mapping(source = "likes", target = "likes")
    @Mapping(source = "disLikes", target = "disLikes")
    EventFullDto toDTOFullResponseFromEntity(EventEntity eventEntity, CategoryDto category,
                                             UserShortDto initiator, Location location,
                                             Set<RatingDto> likes, Set<RatingDto> disLikes);

    @Mapping(source = "eventEntity.annotation", target = "annotation")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "eventEntity.confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "eventEntity.createdOn", target = "createdOn")
    @Mapping(source = "eventEntity.description", target = "description")
    @Mapping(source = "eventEntity.eventDate", target = "eventDate")
    @Mapping(source = "eventEntity.id", target = "id")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "location", target = "location")
    @Mapping(source = "eventEntity.paid", target = "paid")
    @Mapping(source = "eventEntity.participantLimit", target = "participantLimit")
    @Mapping(source = "eventEntity.publishedOn", target = "publishedOn")
    @Mapping(source = "eventEntity.requestModeration", target = "requestModeration")
    @Mapping(source = "eventEntity.state", target = "state")
    @Mapping(source = "eventEntity.title", target = "title")
    @Mapping(source = "eventEntity.views", target = "views")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "disLikes", ignore = true)
    EventFullDto toDTOFullResponseFromCreatedEntity(EventEntity eventEntity, CategoryDto category,
                                                    UserShortDto initiator, Location location);

    @Mapping(source = "eventEntity.annotation", target = "annotation")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "eventEntity.confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "eventEntity.eventDate", target = "eventDate")
    @Mapping(source = "eventEntity.id", target = "id")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "eventEntity.paid", target = "paid")
    @Mapping(source = "likes", target = "likes")
    @Mapping(source = "disLikes", target = "disLikes")
    EventShortDto toDTOShortResponseFromEntity(EventEntity eventEntity, CategoryDto category, UserShortDto initiator,
                                               Set<RatingDto> likes, Set<RatingDto> disLikes);
}
