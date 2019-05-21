package notification.service.backend.configuration;

import notification.service.utils.HttpUtils;
import notification.service.utils.RestTemplateUtils;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfiguration {
    private final ThreadPoolTaskScheduler taskScheduler;

    public WebConfiguration(@Qualifier(CommonConfiguration.TASK_SCHEDULER) ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "Task scheduler can't be null.");
    }

    @Bean
    @Primary
    public RestTemplate getRestTemplate() {
        return new RestTemplateUtils(taskScheduler).getRestTemplate();
    }

    @Bean
    public HttpUtils getHttpUtils(@Autowired RestTemplate template) {
        return new HttpUtils(template);
    }
}
