package com.example.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    @Bean
    public Job simpleJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("simpleJob", jobRepository)
                .start(simpleStep1(null, jobRepository, transactionManager))
                .build();
    }

    /**
     * 메타데이터 테이블의 BATCH_JOB_INSTANCE 테이블은 Job Parameter에 따라 생성된다.
     * 같은 Batch Job이라도 Job Parameter가 다르면 새로운 행으로 기록되지만, 같다면 기록되지 않는다.
     * IntelliJ에서 Program argument에 인수를 설정, 실행 후 직접 테이블을 조회하여 확인해보자.
     * (동일한 Job Parameter로 성공한 기록이 있을 때만 재수행되지 않는다.)
     * JobParameter를 인수로 받기 위해서는 @JobScope로 빈 스코프를 정의해주어야 한다.
     * @link <a href="https://github.com/jojoldu/spring-batch-in-action/blob/master/3_%EB%A9%94%ED%83%80%ED%85%8C%EC%9D%B4%EB%B8%94%EC%97%BF%EB%B3%B4%EA%B8%B0.md">참고 링크 jojoldu님 spring-batch-in-action 리포지토리</a>
     */
    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("simpleStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step1");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
