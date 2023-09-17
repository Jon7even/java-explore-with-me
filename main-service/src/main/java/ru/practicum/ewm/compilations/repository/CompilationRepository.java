package ru.practicum.ewm.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilations.model.CompilationEntity;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {
    List<CompilationEntity> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);

    boolean existsByTitle(@Param("title") String title);
}
