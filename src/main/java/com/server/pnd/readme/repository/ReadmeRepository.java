package com.server.pnd.readme.repository;

import com.server.pnd.domain.Readme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadmeRepository extends JpaRepository<Readme, Long> {

    Optional<Readme> findByTitleAndContent(String title, String content);

    List<Readme> findByUserId(Long id);
}
