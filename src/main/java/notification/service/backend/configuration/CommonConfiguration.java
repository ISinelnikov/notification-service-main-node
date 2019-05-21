package notification.service.backend.configuration;

import notification.service.cache.RequestTimeCache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class CommonConfiguration {
    public static final String TASK_SCHEDULER = "Task Scheduler";

    private static final long GUARD_VALUE = Long.getLong("main.node.request.guard.value", 20);

    @Bean(TASK_SCHEDULER)
    public ThreadPoolTaskScheduler getThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(TASK_SCHEDULER);
        return threadPoolTaskScheduler;
    }

    @Bean
    public RequestTimeCache getRequestTimeCache() {
        return new RequestTimeCache(GUARD_VALUE, 10, null);
    }
}
