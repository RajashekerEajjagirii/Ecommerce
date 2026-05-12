package com.raj.ecommerce.scheduler;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(prefix = "spring.batch.scheduler", name = "enabled", havingValue = "true")
public class BatchScheduler {

    private static final Duration STARTUP_DELAY = Duration.ofMinutes(2);

    private final JobOperator jobOperator;
    private final Job processOrdersJob;

    private volatile Instant notBefore = Instant.EPOCH;

    public BatchScheduler(JobOperator jobOperator, Job processOrdersJob){
        this.jobOperator = jobOperator;
        this.processOrdersJob=processOrdersJob;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // @Scheduled does not support initialDelay with cron triggers.
        // We emulate "delay after startup" by skipping triggers until this time.
        this.notBefore = Instant.now().plus(STARTUP_DELAY);
    }

    @Scheduled(cron = "0 */3 * * * ?")
    public void runJob() {
       if (Instant.now().isBefore(notBefore)) {
           return;
       }
       final JobParameters params=new JobParametersBuilder()
                .addLong("startAt",System.currentTimeMillis())
                .toJobParameters();
       try{
           System.out.println("runJob Scheduler fired at " + LocalDateTime.now());
           final JobExecution jobExecution = jobOperator.start(processOrdersJob, params);
           System.out.println("Batch job status: " + jobExecution.getStatus());
       } catch (Exception e) {
           e.printStackTrace();
       }

    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void testLog() {
        System.out.println("testLog Scheduler fired at " + LocalDateTime.now());
    }

}
