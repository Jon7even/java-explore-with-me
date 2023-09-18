package ru.practicum.ewm.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.events.dto.Location;
import ru.practicum.ewm.events.model.LocationEntity;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    LocationEntity toEntityFromDTOCreate(Location location);

    @Mapping(source = "locationEntity.lat", target = "lat")
    @Mapping(source = "locationEntity.lon", target = "lon")
    Location toDTOResponseFromEntity(LocationEntity locationEntity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    LocationEntity toEntityFromDTOUpdate(Location location, Long id);
}
