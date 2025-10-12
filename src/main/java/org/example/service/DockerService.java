package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;


@Service
public class DockerService{
    private final Logger logger;
    private final DockerClient dockerClient;

    @Value("${aws.access.key.id}")
    private String accessKeyId;

    @Value("${aws.secret.access.key}")
    private String secretAccessKey;

    @Value("${aws.default.region}")
    private String region;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public DockerService(DockerClient dockerClient){
        this.logger = LoggerFactory.getLogger(DockerService.class);
        this.dockerClient = dockerClient;
    }

    String captureContainerLogs(String containerId){
        logger.info("Attempting to capture logs for container: {}", containerId);

        final StringBuilder logOutput = new StringBuilder();

        try{
            ResultCallback.Adapter<Frame> callback = new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame){
                        String logLine = new String(frame.getPayload());
                        logger.debug("Captured log line: {}", logLine.trim());
                        logOutput.append(logLine);
                    }
            };

            dockerClient.logContainerCmd(containerId)
            .withStdOut(true)
            .withStdErr(true)
            .withTailAll()
            .exec(callback)
            .awaitCompletion(10,TimeUnit.SECONDS); 

            String logs = logOutput.toString().trim();
            logger.info("Total logs captured: {} characters", logs.length());
            logger.info("Logs preview: {}", logs.isEmpty() ? "EMPTY" : logs.substring(0, Math.min(200, logs.length())));

            return logs;
        } catch(Exception e){
            logger.error("Failed to capture logs for container {}: {}", containerId, e.getMessage());
            return "Error: "+e.getMessage();
        }
    }

    String extractLastJson(String logs){
        if (logs == null || logs.trim().isEmpty()) {
            logger.warn("No logs to extract JSON from");
            return null;
        }

        String[] lines = logs.split("\n");
        logger.info("Searching for JSON in {} lines of logs", lines.length);

       
        for(int i = lines.length - 1; i >= Math.max(0, lines.length - 20); i--){
            String line = lines[i].trim();
            if (!line.isEmpty() && line.startsWith("{") && line.endsWith("}")) {
                logger.info("Found JSON result: {}", line);
                return line;
            }
        }

        logger.warn("No JSON found in container logs. Logs content: {}",
                   logs.length() > 500 ? logs.substring(0, 500) + "..." : logs);
        return null;
    }
    public void createAndRunJudgeContainer(String userId, String questionId, String language){
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = String.valueOf((int)(Math.random() * 10000));
        String containerName = "Judge-" + userId + "-" + questionId + "-" + timestamp + "-" + randomSuffix;
        logger.info("Container Name - "+containerName);
        try{
            logger.info("Creating fresh container: {}", containerName);

            String containerId = dockerClient.createContainerCmd("contest-judge")
                    .withName(containerName)
                    .withWorkingDir("/home/judgeuser/workspace")
                    .withEnv("USER_ID="+userId,
                            "QUESTION_ID="+questionId,
                            "LANGUAGE="+language,
                            "AWS_ACCESS_KEY_ID=" + accessKeyId,
                            "AWS_SECRET_ACCESS_KEY=" + secretAccessKey,
                            "AWS_DEFAULT_REGION=" + region,
                            "AWS_S3_BUCKET_NAME=" + bucketName)
                    .exec()
                    .getId();

            logger.info("Container {} created successfully with ID: {}", containerName, containerId);

            dockerClient.startContainerCmd(containerId).exec();
            logger.info("Container {} started successfully", containerName);

           
            dockerClient.waitContainerCmd(containerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);

            logger.info("Container {} completed, capturing logs", containerName);

            
            String logs = captureContainerLogs(containerId);
            logger.info("Captured logs: {}", logs.isEmpty() ? "EMPTY LOGS" : logs);

            
            String resultJson = extractLastJson(logs);
            logger.info("Extracted JSON result: {}", resultJson != null ? resultJson : "NULL");

            try {
                dockerClient.stopContainerCmd(containerId).exec();
                logger.info("Container "+containerName+" stopped.");
            }  catch (Exception e) {
                logger.warn("Stop failed: {}", e.getMessage());
            }
            try {
                dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .withRemoveVolumes(true)
                    .exec();
                    logger.info("Container "+containerName+" removed.");
            }  catch (Exception e) {
                logger.warn("Remove failed: {}", e.getMessage());
            }

        } catch(Exception e){
            logger.error("Failed to create and start container {} - {}", containerName, e.getMessage(), e);
            throw new RuntimeException("The container could not be created or started: " + e.getMessage(), e);
        }
        
        
    }
}