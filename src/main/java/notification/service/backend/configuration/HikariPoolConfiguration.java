package notification.service.backend.configuration;

import notification.service.backend.domain.MainNodeConfiguration;
import notification.service.backend.service.ConfigurationService;

import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class HikariPoolConfiguration {
    @Value("${datasource.driverClassName}")
    private String driverClassName;

    private final MainNodeConfiguration mainNodeConfiguration;

    public HikariPoolConfiguration(ConfigurationService configurationService) {
        this.mainNodeConfiguration = Objects.requireNonNull(configurationService,
                "Configuration service can't be null.").getMainNodeConfiguration();
    }

    private static DataSource createDataSource(String driverClassName, String jdbcUrl, String jdbcUser, String jdbcPassword) {
        HikariConfig config = new HikariConfig();
        if (StringUtils.isEmpty(jdbcUrl)) {
            throw new IllegalArgumentException("Can't create data source");
        } else {
            config.setDriverClassName(driverClassName);
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(jdbcUser);
            config.setPassword(jdbcPassword);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(1);
            config.setAutoCommit(true);
        }
        // default tuning
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(
                createDataSource(driverClassName, mainNodeConfiguration.getDatabaseUrl(),
                        mainNodeConfiguration.getDatabaseUsername(), mainNodeConfiguration.getDatabasePassword())
        );
    }
}
