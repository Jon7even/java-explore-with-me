package ru.practicum.ewm.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.model.RequestEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "request.created", target = "created")
    @Mapping(source = "request.event.id", target = "event")
    @Mapping(source = "request.id", target = "id")
    @Mapping(source = "request.requester.id", target = "requester")
    @Mapping(source = "request.status", target = "status")
    ParticipationRequestDto toDTOResponseFromEntity(RequestEntity request);

    @Mapping(source = "request.created", target = "created")
    @Mapping(source = "request.event.id", target = "event")
    @Mapping(source = "request.id", target = "id")
    @Mapping(source = "request.requester.id", target = "requester")
    @Mapping(source = "request.status", target = "status")
    List<ParticipationRequestDto> toDTOResponseFromEntityList(List<RequestEntity> request);


    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "rejectedRequests", target = "rejectedRequests")
    EventRequestStatusUpdateResult toDTOResponseFromDTOList(List<ParticipationRequestDto> confirmedRequests,
                                                            List<ParticipationRequestDto> rejectedRequests);
}
