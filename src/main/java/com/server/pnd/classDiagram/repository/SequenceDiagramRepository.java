package com.server.pnd.classDiagram.repository;

import com.server.pnd.domain.SequenceDiagram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SequenceDiagramRepository extends JpaRepository<SequenceDiagram, Long> {
    Optional<SequenceDiagram> findByProjectId(Long id);
}
