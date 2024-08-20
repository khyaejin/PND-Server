package com.server.pnd.diagram.repository;

import com.server.pnd.domain.Diagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassDiagramRepository extends JpaRepository<Diagram, Long> {
    Optional<Diagram> findByProjectId(Long id);
}
