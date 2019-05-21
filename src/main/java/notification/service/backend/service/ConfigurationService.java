package notification.service.backend.service;

import notification.service.backend.domain.MainNodeConfiguration;
import notification.service.utils.YamlConfigUtils;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    private final MainNodeConfiguration mainNodeConfiguration;

    public ConfigurationService() {
        this.mainNodeConfiguration = Objects.requireNonNull(YamlConfigUtils
                        .loadYamlConfig(getConfigPath(), MainNodeConfiguration.class),
                "Node configuration can't be null.");

        logger.debug("Application configuration: {}.", mainNodeConfiguration);
    }

    public MainNodeConfiguration getMainNodeConfiguration() {
        return mainNodeConfiguration;
    }

    private static String getConfigPath() {
        String configPath = System.getProperty("application.config.path");

        if (!StringUtils.hasText(configPath)) {
            throw new IllegalStateException("Can't start application without config.");
        }
        return configPath;
    }
}
