package com.elinter.data.migrate.config;

import lombok.Data;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ConfigurationProperties(prefix = "async.executor.thread")
@Data
@Slf4j
public class DataMigrateThreadPool {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    private String namePrefix;

    @Bean(name="asyncDataMigrate")
    public Executor asynServiceExecutor(){
        log.info("数据迁移线程池创建");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(namePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
