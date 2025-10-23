package org.example.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class SubmissionRequest {
    private String questionId;
    private String userId;
    private String language;
    
    @Size(max=1024*1024,message = "Code exceeds maximum size of 1MB.")
    private String code;    
}
