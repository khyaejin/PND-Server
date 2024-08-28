package com.server.pnd.report.repository;

import com.server.pnd.domain.Repo;
import com.server.pnd.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    // 해당 userId를 가진 User와 연관된 Repo를 가진 Report 테이블 개수 리턴
    int countByRepo_User_Id(Long id);

    Optional<Report> findByRepo(Repo repo);
}
