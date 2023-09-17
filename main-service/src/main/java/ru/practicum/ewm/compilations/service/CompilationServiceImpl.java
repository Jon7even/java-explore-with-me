package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.CompilationEntity;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.EntityNotDeletedException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.users.mapper.UserMapper;
import ru.practicum.ewm.utils.ConverterPage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constants.CommonSort.DEFAULT_SORT_BY_ID;
import static ru.practicum.ewm.constants.NamesExceptions.DUPLICATE_TITLE;
import static ru.practicum.ewm.constants.NamesLogsInService.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.debug("New newCompilationDto came {} [newCompilationDto={}]", SERVICE_FROM_CONTROLLER, newCompilationDto);
        checkTitleOnDuplicate(newCompilationDto.getTitle());

        List<EventEntity> foundEvents = eventRepository.findAllById(newCompilationDto.getEvents());
        log.debug("Found [count={}] events for create new compilation: {} {}",
                foundEvents.size(), foundEvents, SERVICE_FROM_DB);

        if (foundEvents.isEmpty()) {
            log.debug("List events for compilation is empty");
        }

        CompilationEntity compilationEntity = CompilationMapper.INSTANCE.toEntityFromDTOCreate(
                newCompilationDto, foundEvents
        );

        log.debug("Add new entity [compilation={}] {}", compilationEntity, SERVICE_IN_DB);
        CompilationEntity createdCompilation = compilationRepository.save(compilationEntity);

        log.debug("New compilation has returned [compilation={}] {}", createdCompilation, SERVICE_FROM_DB);
        return CompilationMapper.INSTANCE.toDTOResponseFromEntity(createdCompilation,
                createdCompilation.getEvents().stream()
                        .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                                CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                                UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator())
                        )))
                        .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void deleteCompilationById(Long compId) {
        existDoesCompilationEntityById(compId);

        log.debug("Remove compilation with [compId={}] {}", compId, SERVICE_IN_DB);
        compilationRepository.deleteById(compId);
        boolean isRemoved = compilationRepository.existsById(compId);

        if (!isRemoved) {
            log.debug("Compilation by [id={}] has removed {}", compId, SERVICE_FROM_DB);
        } else {
            log.error("Compilation by [id={}] was not removed", compId);
            throw new EntityNotDeletedException(String.format("Compilation with id=%d was not deleted", compId));
        }
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        log.debug("UpdateCompilationRequest for update came {} [updateCompilationRequest={}]",
                SERVICE_FROM_CONTROLLER, updateCompilationRequest);
        Optional<CompilationEntity> foundCompilation = compilationRepository.findById(compId);

        if (foundCompilation.isPresent()) {
            CompilationEntity compilationForUpdate = foundCompilation.get();
            CompilationEntity compilation = CompilationEntity.builder().id(compId).build();

            if (!updateCompilationRequest.getEvents().isEmpty()) {
                List<EventEntity> eventsForUpdate = eventRepository.findAllById(updateCompilationRequest.getEvents());
                log.debug("Found [count={}] events for update compilation: {} {}",
                        eventsForUpdate.size(), eventsForUpdate, SERVICE_FROM_DB);
                compilationForUpdate.setEvents(eventsForUpdate);
            } else {
                log.debug("List of events to update is empty");
            }

            if (updateCompilationRequest.getPinned() != null) {
                compilation.setPinned(updateCompilationRequest.getPinned());
            }

            if (updateCompilationRequest.getTitle() != null) {
                checkTitleOnDuplicate(updateCompilationRequest.getTitle());
                compilation.setTitle(updateCompilationRequest.getTitle());
            }

            CompilationMapper.INSTANCE.updateEntityFromEntityForUpdate(compilation, compilationForUpdate);

            log.debug("Updated [updatedCompilation={}] {}", compilationForUpdate, SERVICE_IN_DB);
            CompilationEntity updatedCompilation = compilationRepository.save(compilationForUpdate);

            log.debug("Updated Compilation has returned [compilation={}] {}", updatedCompilation, SERVICE_FROM_DB);
            return CompilationMapper.INSTANCE.toDTOResponseFromEntity(updatedCompilation,
                    updatedCompilation.getEvents().stream()
                            .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator())
                            )))
                            .collect(Collectors.toList()));
        } else {
            log.warn("Compilation by [compId={}] was not found", compId);
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
    }

    @Override
    public List<CompilationDto> getCompilationsByPage(Boolean pinned, Integer from, Integer size) {
        log.debug("Get all Compilation by pages {}", SERVICE_IN_DB);

        Pageable pageable = ConverterPage.getPageRequest(from, size, Optional.of(DEFAULT_SORT_BY_ID));
        List<CompilationEntity> listCompilations = compilationRepository.findAllByPinned(pinned, pageable);

        if (listCompilations.isEmpty()) {
            log.debug("Has returned empty list compilations {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list compilations [count={}] {}", listCompilations.size(), SERVICE_FROM_DB);
        }

        return listCompilations.stream()
                .map(compilationEntity -> CompilationMapper.INSTANCE.toDTOResponseFromEntity(compilationEntity,
                        compilationEntity.getEvents().stream()
                                .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                                        CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                                        UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator())
                                )))
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.debug("Get compilation by [compId={}] {}", compId, SERVICE_IN_DB);
        Optional<CompilationEntity> foundCompilation = compilationRepository.findById(compId);

        if (foundCompilation.isPresent()) {
            CompilationEntity compilation = foundCompilation.get();
            log.debug("Found [compilation={}] {}", foundCompilation.get(), SERVICE_FROM_DB);
            return CompilationMapper.INSTANCE.toDTOResponseFromEntity(compilation,
                    compilation.getEvents().stream()
                            .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator())
                            )))
                            .collect(Collectors.toList()));
        } else {
            log.warn("Compilation by [compId={}] was not found", compId);
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
    }

    private void checkTitleOnDuplicate(String title) {
        if (compilationRepository.existsByTitle(title)) {
            log.debug("Checked title={} fail: title compilation already exist in DB", title);
            throw new IntegrityConstraintException(DUPLICATE_TITLE);
        }
    }

    private void existDoesCompilationEntityById(Long compId) {
        log.debug("Start check exist [compId={}] {}", compId, SERVICE_IN_DB);

        if (compilationRepository.existsById(compId)) {
            log.debug("Check was successful found [compId={}] {}", compId, SERVICE_FROM_DB);
        } else {
            log.warn("Compilation by [id={}] was not found", compId);
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
    }

}
