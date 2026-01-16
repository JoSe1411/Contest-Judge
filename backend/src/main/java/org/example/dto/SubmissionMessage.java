package org.example.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SubmissionMessage {
    private UUID submissionId;
    private String userId;
    private String questionId;
    private String language;
    private String s3Key;
}
