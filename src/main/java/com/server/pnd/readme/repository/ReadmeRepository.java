package com.server.pnd.readme.repository;

import com.server.pnd.domain.Readme;
import com.server.pnd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadmeRepository extends JpaRepository<Readme, Long> {
}
