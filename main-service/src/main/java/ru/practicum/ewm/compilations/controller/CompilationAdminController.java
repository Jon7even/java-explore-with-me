package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.service.CompilationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static ru.practicum.ewm.constants.EndpointsPaths.COMPILATIONS_ADMIN;
import static ru.practicum.ewm.constants.NamesLogsInController.IN_CONTROLLER_METHOD;

@Slf4j
@RestController
@RequestMapping(path = COMPILATIONS_ADMIN)
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> create(@Valid @RequestBody NewCompilationDto newCompilationDto,
                                                 HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createCompilation(newCompilationDto));
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> removeById(@PathVariable @Positive Long compId, HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        compilationService.deleteCompilationById(compId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateById(@PathVariable @Positive Long compId,
                                                     @Valid @RequestBody UpdateCompilationRequest compilation,
                                                     HttpServletRequest request) {

        log.debug("On {} {} {}", request.getRequestURL(), IN_CONTROLLER_METHOD, request.getMethod());

        return ResponseEntity.ok().body(compilationService.updateCompilation(compilation, compId));
    }

}
