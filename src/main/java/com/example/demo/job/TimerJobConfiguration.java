package com.example.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TimerJobConfiguration {

    // TODO 다른 Job이랑 병렬처리해보기
    // TODO chunk 기반으로 바꿔보기
    @Bean
    public Job timerJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("timerJob", jobRepository)
                .start(timerStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step timerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("timerStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        log.info("time: {}", i);
                    }
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
