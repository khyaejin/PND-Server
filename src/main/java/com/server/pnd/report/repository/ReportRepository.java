package com.server.pnd.report.repository;

import com.server.pnd.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 해당 userId를 가진 테이블 개수 리턴
    int countByUserId(Long id);
}
