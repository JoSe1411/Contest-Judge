package org.example.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class SubmissionResultResponse {
    private UUID submissionId;
    private String status;
    private Integer exitCode;
    private String output;
    private String expected;
    private Boolean passed;
    private String error;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}