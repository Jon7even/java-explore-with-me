package ru.practicum.ewm.compilations.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.model.CompilationEntity;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.EventEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "events", target = "events")
    @Mapping(source = "newCompilationDto.pinned", target = "pinned")
    @Mapping(source = "newCompilationDto.title", target = "title")
    CompilationEntity toEntityFromDTOCreate(NewCompilationDto newCompilationDto, List<EventEntity> events);

    @Mapping(source = "events", target = "events")
    @Mapping(source = "compilationEntity.id", target = "id")
    @Mapping(source = "compilationEntity.pinned", target = "pinned")
    @Mapping(source = "compilationEntity.title", target = "title")
    CompilationDto toDTOResponseFromEntity(CompilationEntity compilationEntity, List<EventShortDto> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "compilation.events", target = "events")
    @Mapping(source = "compilation.pinned", target = "pinned")
    @Mapping(source = "compilation.title", target = "title")
    void updateEntityFromEntityForUpdate(CompilationEntity compilation,
                                         @MappingTarget CompilationEntity compilationForUpdate);
}
