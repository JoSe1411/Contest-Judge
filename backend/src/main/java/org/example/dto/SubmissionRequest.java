package org.example.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class SubmissionRequest {
     @NotBlank(message = "Question ID is required")
    private String questionId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(java|python|cpp|c\\+\\+|py)$",
             message = "Unsupported language")
    private String language;

    @NotBlank(message = "Code is required")
    @Size(max = 1024 * 1024, message = "Code exceeds maximum size of 1MB")
    private String code;    
}
