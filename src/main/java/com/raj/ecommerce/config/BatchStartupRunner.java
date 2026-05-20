package com.raj.ecommerce.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchStartupRunner {

    @Bean
    ApplicationRunner runBatchJobsOnStartup(
            @Qualifier("processOrdersJob") Job processOrdersJob,
            @Qualifier("insertPersonsJob") Job insertPersonsJob,
            JobOperator jobOperator,
            @Value("${raj-ecommerce.batch.startup.enabled:true}") boolean enabled
    ) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                if (!enabled) {
                    return;
                }

                // Ensure each app start creates a distinct JobInstance.
                JobParameters params = new JobParametersBuilder()
                        .addLong("startup.ts", System.currentTimeMillis())
                        .toJobParameters();

                startIfNotRunning(jobOperator, processOrdersJob, params);
                startIfNotRunning(jobOperator, insertPersonsJob, params);
            }
        };
    }

    private static void startIfNotRunning(JobOperator jobOperator, Job job, JobParameters params)
            throws NoSuchJobException {
        // Prevent overlapping executions (e.g. startup + scheduler) which can cause optimistic locking failures.
        if (!jobOperator.getRunningExecutions(job.getName()).isEmpty()) {
            return;
        }
        try {
            jobOperator.start(job, params);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 InvalidJobParametersException ex) {
            // On startup, don't fail the whole app if the job can't be started.
        }
    }
}
