package org.example.service;

import java.util.UUID;
import java.util.Optional;
import java.time.LocalDateTime;
import org.example.dto.SubmissionMessage;
import org.example.dto.ExecutionResult;
import org.example.entity.Submission;
import org.example.entity.SubmissionStatus;
import org.example.repository.SubmissionRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final DockerService dockerService;
    private final SubmissionRepository submissionRepository;

    public Consumer(DockerService dockerService,
            SubmissionRepository submissionRepository) {
        this.dockerService = dockerService;
        this.submissionRepository = submissionRepository;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void consumer(SubmissionMessage message) {
        Submission submission = null;
        try {
            UUID submissionId = message.getSubmissionId();
            String userId = message.getUserId();
            String questionId = message.getQuestionId();
            String language = message.getLanguage();
            String s3Key = message.getS3Key();
            Optional<Submission> option = submissionRepository.findById(submissionId);
            if (option.isEmpty()) {
                throw new RuntimeException("Submission not found in DB.");
            }
            submission = option.get();
            submission.setStatus(SubmissionStatus.RUNNING);
            submission.setStartedAt(LocalDateTime.now());
            submission = submissionRepository.save(submission);
            ExecutionResult result = dockerService.createAndRunJudgeContainer(userId, questionId, language, s3Key,
                    null);
            submission.setExitCode(result.getExitCode());
            submission.setOutput(result.getOutput());
            submission.setExpected(result.getExpected());
            submission.setPassed(result.isPassed());
            submission.setError(result.getError());
            submission.setCompletedAt(LocalDateTime.now());
            submission.setStatus(SubmissionStatus.COMPLETED);
            submission = submissionRepository.save(submission);
            return;

        } catch (Exception e) {
            logger.error("Error Message - " + e.getMessage());
            if (submission != null) {
                submission.setStatus(SubmissionStatus.FAILED);
                submission.setCompletedAt(LocalDateTime.now());
                submission = submissionRepository.save(submission);
            }
        }

    }

}
