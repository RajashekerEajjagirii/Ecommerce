package com.raj.ecommerce.scheduler;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(prefix = "spring.batch.scheduler", name = "enabled", havingValue = "true")
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job processOrdersJob;

    public BatchScheduler(JobLauncher jobLauncher,Job processOrdersJob){
        this.jobLauncher=jobLauncher;
        this.processOrdersJob=processOrdersJob;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void runJob() {
       final JobParameters params=new JobParametersBuilder()
                .addLong("startAt",System.currentTimeMillis())
                .toJobParameters();
       try{
           System.out.println("runJob Scheduler fired at " + LocalDateTime.now());
           final JobExecution jobExecution=jobLauncher.run(processOrdersJob,
                   params);
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
