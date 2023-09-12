package ru.practicum.ewm.events.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.CategoryEntity;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.LocationEntity;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

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
    EventFullDto toDTOFullResponseFromEntity(EventEntity eventEntity, CategoryDto category,
                                             UserShortDto initiator, Location location);

    @Mapping(source = "eventEntity.annotation", target = "annotation")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "eventEntity.confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "eventEntity.eventDate", target = "eventDate")
    @Mapping(source = "eventEntity.id", target = "id")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "eventEntity.paid", target = "paid")
    @Mapping(source = "eventEntity.title", target = "title")
    @Mapping(source = "eventEntity.views", target = "views")
    EventShortDto toDTOShortResponseFromEntity(EventEntity eventEntity, CategoryDto category, UserShortDto initiator);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
    @Mapping(source = "users", target = "users")
    @Mapping(source = "states", target = "states")
    @Mapping(source = "categories", target = "categories")
    @Mapping(source = "rangeStart", target = "rangeStart")
    @Mapping(source = "rangeEnd", target = "rangeEnd")
    @Mapping(source = "from", target = "from")
    @Mapping(source = "size", target = "size")
    ParamsSortDto toDTOParamFromList(List<Long> users,List<EventState> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
