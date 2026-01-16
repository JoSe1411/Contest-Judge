package org.example.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class SubmissionResponse {
    private UUID submissionId;
    private String status;
    private LocalDateTime createdAt;
}
