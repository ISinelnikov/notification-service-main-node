package notification.service.backend.repository.base;

import java.util.Objects;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractDao {
    protected NamedParameterJdbcTemplate npjtTemplate;
    protected JdbcTemplate jdbcTemplate;

    public AbstractDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "Jdbc template can't be null.");
        this.npjtTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }
}
