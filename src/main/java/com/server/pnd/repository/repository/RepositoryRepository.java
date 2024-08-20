package com.server.pnd.repository.repository;

import com.server.pnd.domain.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repo, Long> {

    List<Repo> findByUserId(Long id);
}