package com.raj.ecommerce.scheduler;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "batch.scheduler.enabled", havingValue = "true")
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job processOrdersJob;

    public BatchScheduler(JobLauncher jobLauncher,Job processOrdersJob){
        this.jobLauncher=jobLauncher;
        this.processOrdersJob=processOrdersJob;
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void runJob() throws Exception{
        JobParameters params=new JobParametersBuilder()
                .addLong("run.id",System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(processOrdersJob,params);
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void testLog() {
        System.out.println("Scheduler fired at " + LocalDateTime.now());
    }

}
