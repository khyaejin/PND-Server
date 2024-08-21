package com.server.pnd.report.controller;

import com.server.pnd.report.service.ReportService;
import com.server.pnd.util.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pnd")
public class ReportController {
    final private ReportService reportService;

    @PostMapping("/report/{repoId}")
    public ResponseEntity<CustomApiResponse<?>> createReport(
            @PathVariable("repoId") Long repoId){
        return reportService.createReport(repoId);
    }

}
