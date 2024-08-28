package com.server.pnd.repo.repository;

import com.server.pnd.domain.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findByUserId(Long id);

    // UserId로 생성된 문서가 하나라도 있는 Repo들 리턴
    @Query("SELECT r FROM Repo r " +
            "WHERE r.user.id = :userId " +
            "AND (EXISTS (SELECT 1 FROM Readme re WHERE re.repo.id = r.id) " +
            "OR EXISTS (SELECT 1 FROM Diagram d WHERE d.repo.id = r.id) " +
            "OR EXISTS (SELECT 1 FROM Report rep WHERE rep.repo.id = r.id))")
    List<Repo> findReposWithAnyDocumentByUserId(@Param("userId") Long userId);
}
