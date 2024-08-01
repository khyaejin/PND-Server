package com.server.pnd.repository.repository;

import com.server.pnd.domain.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long> {
}
