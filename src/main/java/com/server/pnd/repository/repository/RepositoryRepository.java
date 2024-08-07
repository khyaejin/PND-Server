package com.server.pnd.repository.repository;

import com.server.pnd.domain.Repository;
import com.server.pnd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long> {

}