package com.filehandling.config;

import com.filehandling.service.FileService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private FileService fileService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Job deleteFilesJob(JobExecutionListener listener, Step step1) {
        return new JobBuilder("deleteFilesJob", jobRepository)
                .start(step1)
                .listener(listener)
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet(deleteFilesTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet deleteFilesTasklet() {
        return (contribution, chunkContext) -> {
            fileService.deleteOldFiles();
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobExecutionListenerSupport() {
        };
    }
}
