package com.server.pnd.diagram.repository;

import com.server.pnd.domain.Diagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagramRepository extends JpaRepository<Diagram, Long> {
    Optional<Diagram> findByRepoId(Long id);

    // 해당 userId에 해당하는 테이블들 중 ClassScript가 채워져 있는 테이블의 개수
    int countByUserIdAndClassScriptIsNotNull(Long userId);

    // 해당 userId에 해당하는 테이블들 중 SequenceScript가 채워져 있는 테이블의 개수
    int countByUserIdAndSequenceScriptIsNotNull(Long id);

    // 해당 userId에 해당하는 테이블들 중 ErdScript가 채워져 있는 테이블의 개수
    int countByUserIdAndErdScriptIsNotNull(Long id);
}
