package com.filehandling.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job deleteFilesJob;

    @Scheduled(fixedRate = 600000) // every 10 minutes
    public void runDeleteFilesJob() {
        try {
            jobLauncher.run(
                    deleteFilesJob,
                    new JobParametersBuilder()
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
