package com.server.pnd.markdown.repository;

import com.server.pnd.domain.Markdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkdownRepository extends JpaRepository<Markdown, Long> {
}
