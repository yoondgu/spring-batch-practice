package com.example.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /**
     * .on() <br/>
     * - 캐치할 ExitStatus 지정 <br/>
     * - * 일 경우 모든 ExitStatus가 지정된다. <br/>
     * to() <br/>
     * - 다음으로 이동할 Step 지정 <br/>
     * from() <br/>
     * - 일종의 이벤트 리스너 역할 <br/>
     * - 상태값을 보고 일치하는 상태라면 to()에 포함된 step을 호출합니다. <br/>
     * - step1의 이벤트 캐치가 FAILED로 되있는 상태에서 추가로 이벤트 캐치하려면 from을 써야만 함 <br/>
     * end() <br/>
     * - end는 FlowBuilder를 반환하는 end와 FlowBuilder를 종료하는 end 2개가 있음 <br/>
     * - on("*") 뒤에 있는 end는 FlowBuilder를 반환하는 end <br/>
     * - build() 앞에 있는 end는 FlowBuilder를 종료하는 end <br/>
     * - FlowBuilder를 반환하는 end 사용시 계속해서 from을 이어갈 수 있음 <br/>
     * @link 출처: jojoldu님 <a href="https://github.com/jojoldu/spring-batch-in-action/blob/master/4_BATCH_JOB_FLOW.md">spring-batch-in-action</a>
     */
    @Bean
    public Job stepNextConditionalJob() {
        return new JobBuilder("stepNextConditionalJob", jobRepository)
                .start(conditionalJobStep1())
                    .on("FAILED")
                    .to(conditionalJobStep3())
                    .on("*")
                    .end()
                .from(conditionalJobStep1())
                    .on("*")
                    .to(conditionalJobStep2())
                    .next(conditionalJobStep3())
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return new StepBuilder("conditionalJobStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is conditionalJobStep1");

                    /**
                     * 해당 ExitStatus에 따라 flow가 진행된다.
                     */
                    // contribution.setExitStatus(ExitStatus.FAILED);

                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step conditionalJobStep2() {
        return new StepBuilder("conditionalJobStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is conditionalJobStep2");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return new StepBuilder("conditionalJobStep3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is conditionalJobStep3");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
