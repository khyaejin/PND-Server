package com.server.pnd.diagram.repository;

import com.server.pnd.domain.Diagram;
import com.server.pnd.domain.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagramRepository extends JpaRepository<Diagram, Long> {
    Optional<Diagram> findByRepoId(Long id);
    // 해당 userId를 가진 User와 연관된 Repo를 가진 Diagram들 중 classScript가 null이 아닌 테이블 개수 리턴
    int countByRepo_User_IdAndClassScriptIsNotNull(Long userId);
    // 해당 userId를 가진 User와 연관된 Repo를 가진 Diagram들 중 sequenceScript가 null이 아닌 테이블 개수 리턴
    int countByRepo_User_IdAndSequenceScriptIsNotNull(Long userId);
    // 해당 userId를 가진 User와 연관된 Repo를 가진 Diagram들 중 erdScript가 null이 아닌 테이블 개수 리턴
    int countByRepo_User_IdAndErdScriptIsNotNull(Long userId);

    Optional<Diagram> findByRepo(Repo repo);
}
