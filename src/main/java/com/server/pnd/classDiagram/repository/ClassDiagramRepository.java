package com.server.pnd.classDiagram.repository;

import com.server.pnd.domain.ClassDiagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassDiagramRepository extends JpaRepository<ClassDiagram, Long> {
}
