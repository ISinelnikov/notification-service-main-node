package notification.service.backend.repository;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class CommonConnection {
    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String DATABASE_URL = "jdbc:postgresql://134.209.89.72:5432/notification_database";
    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "hxXueHXDmAFMqLVScchRhnPMCLxGdtg7";

    private CommonConnection() {
    }

    private static DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(DATABASE_URL);
        dataSource.setUsername(DATABASE_USERNAME);
        dataSource.setPassword(DATABASE_PASSWORD);
        return dataSource;
    }

    public static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
