package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.example.service.S3ManagerService;
import org.example.service.DockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class SubmissionUploadController{
    private static final Logger logger = LoggerFactory.getLogger(SubmissionUploadController.class);
    private final S3ManagerService s3Manager;
    private final DockerService dockerService;

    public SubmissionUploadController( S3ManagerService s3Manager, DockerService dockerService) {
        this.s3Manager = s3Manager;
        this.dockerService = dockerService;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Contest Judge API is running! Use POST /submit to submit code.");
    }
@PostMapping("/submit")
public ResponseEntity<String> submitCode(
    @RequestParam String userId,
    @RequestParam String questionId,
    @RequestParam String language,
    @RequestParam String code
) {
    try {
        logger.info("Received submission - User: {}, Question: {}, Language: {}", userId, questionId, language);
        
        String extension = s3Manager.getFileExtension(language);
        String s3Key = String.format("Questions/%s/%s/%s-(Main.%s)",
                                  questionId, language, userId, extension);
        s3Manager.uploadCode(s3Key, code);
        logger.info("Code uploaded to S3: {}", s3Key);

        dockerService.createAndRunJudgeContainer(userId, questionId, language);
        return ResponseEntity.ok("✅ Code executed successfully! Check container logs for detailed results.");

    } catch (Exception e) {
        logger.error("Error processing submission for user {}: {}", userId, e.getMessage(), e);
        return ResponseEntity.internalServerError().body(
            String.format("❌ Error processing submission: %s", e.getMessage())
        );
    }
}

}


