package com.server.pnd.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MyPayload {
    private String action;
    private String ref;
    private String sha;
    private String message;
    private int size;
    private int distinct_size;
    private List<Commit> commits; // PushEvent에만 해당
}
