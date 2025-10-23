package org.example.controller;

import org.example.dto.SubmissionRequest;
import org.example.dto.ExecutionResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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
public ExecutionResult submitCode(@RequestBody SubmissionRequest submissionRequest) {
    try {
        String userId = submissionRequest.getUserId();
        String questionId = submissionRequest.getQuestionId();
        String language = submissionRequest.getLanguage();
        String code = submissionRequest.getCode();

        logger.info("Received submission - User: {}, Question: {}, Language: {}", userId, questionId, language);
        
        String extension = s3Manager.getFileExtension(language);
        String s3Key = String.format("Questions/%s/%s/%s-(Main.%s)",
                                  questionId, language, userId, extension);
        s3Manager.uploadCode(s3Key, code);
        logger.info("Code uploaded to S3: {}", s3Key);

        ExecutionResult finalResult =  dockerService.createAndRunJudgeContainer(userId, questionId, language);
        logger.info("✅ Code executed successfully! Check container logs for detailed results.");

        return finalResult;        

    } catch (Exception e) {
        logger.error("Error processing submission for user : {}", e.getMessage(), e);
        ExecutionResult errorOutput = new ExecutionResult();
         errorOutput.setExitCode(1) ;
         errorOutput.setOutput("Error Executing the code.");
         errorOutput.setExpected("");
         errorOutput.setPassed(false);
         errorOutput.setError(e.getMessage());
        return errorOutput;
    }
}

}


