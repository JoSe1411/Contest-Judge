package org.example.dto;

import lombok.Data;

@Data
public class ExecutionResult {
    private int exitCode;
    private String output;
    private String expected;
    private boolean passed;
    private String error;
}
