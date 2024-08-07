package com.server.pnd.report.service;

import com.server.pnd.domain.Repository;
import com.server.pnd.repository.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{
    private final RepositoryRepository repositoryRepository;

//    repositoryId으로 repositoryURL 찾아오는 예시
//    Optional<Repository> foundRepository = repositoryRepository.findByRepositoryId(repositoryId);
//    foundRepository.getHtmlUrl()

}
