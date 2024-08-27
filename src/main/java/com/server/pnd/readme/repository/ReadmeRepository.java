package com.server.pnd.readme.repository;

import com.server.pnd.domain.Readme;
import com.server.pnd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadmeRepository extends JpaRepository<Readme, Long> {
    // 해당 userId를 가진 User와 연관된 Repo를 가진 Readme 테이블 개수 리턴
    int countByRepo_User_Id(Long userId);
}
