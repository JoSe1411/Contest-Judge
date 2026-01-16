package org.example.controller;

import org.example.dto.SubmissionRequest;
import org.example.dto.SubmissionResponse;
import org.example.service.SubmissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.entity.Submission;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@RestController
public class SubmissionUploadController {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionUploadController.class);
    private final SubmissionService submissionService;

    public SubmissionUploadController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Contest Judge API is running! Use POST /submit to submit code.");
    }

    @PostMapping("/submit")
    public SubmissionResponse submitCode(@Valid @RequestBody SubmissionRequest request) {
        logger.info("Received submission - User: {}, Question: {}, Language: {}",
                request.getUserId(), request.getQuestionId(), request.getLanguage());

        return submissionService.submitCode(request);
    }

    @GetMapping("/submission/{id}")
    public Submission getSubmission(@PathVariable UUID id) {
        return submissionService.getSubmission(id);
    }

    @PostMapping("/run-compare")
    public org.example.dto.ExecutionResult runCompare(@Valid @RequestBody org.example.dto.RunCompareRequest request) {
        return submissionService.runCompare(request);
    }
}