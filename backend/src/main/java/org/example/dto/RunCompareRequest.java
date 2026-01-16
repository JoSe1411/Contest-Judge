package org.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RunCompareRequest extends SubmissionRequest {
    private String input;
}
