package com.server.pnd.repo.repository;

import com.server.pnd.domain.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findByUserId(Long id);
}
