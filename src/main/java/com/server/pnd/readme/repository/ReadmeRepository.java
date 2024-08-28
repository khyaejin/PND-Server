package com.server.pnd.readme.repository;

import com.server.pnd.domain.Readme;
import com.server.pnd.domain.Repo;
import com.server.pnd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadmeRepository extends JpaRepository<Readme, Long> {
    // 해당 userId를 가진 User와 연관된 Repo를 가진 Readme 테이블 개수 리턴
    int countByRepo_User_Id(Long userId);
    Optional<Readme> findByRepo(Repo repo);

    // 해당 repoId를 가진 readme 존재하는가 리턴
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Readme r WHERE r.repo.id = :repoId")
    boolean existsByRepoId(@Param("repoId") Long repoId);
}
