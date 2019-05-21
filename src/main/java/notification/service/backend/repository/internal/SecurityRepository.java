package notification.service.backend.repository.internal;

import notification.service.backend.domain.SystemProfile;
import notification.service.backend.repository.base.AbstractDao;

import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SecurityRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(SecurityRepository.class);

    //--- Select user credentials

    private static final String SQL_SELECT_SYSTEM_PROFILE_BY_LOGIN_AND_PASSWORD =
            "select profile_id, username, password "
                    + "from system_user_profile "
                    + "where username = :username and password = :password";

    public SecurityRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Nullable
    public SystemProfile getSystemProfileByCredentials(String username, String password) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("username", username);
        parameterSource.addValue("password", password);

        try {
            return npjtTemplate.query(SQL_SELECT_SYSTEM_PROFILE_BY_LOGIN_AND_PASSWORD,
                    parameterSource, getSystemProfileRowMapper())
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (DataAccessException ex) {
            logger.error("Invoke method getSystemProfileByCredentials(username: {}, password: {}).",
                    username, password, ex);
        }
        return null;
    }

    private static RowMapper<SystemProfile> getSystemProfileRowMapper() {
        return (resultSet, i) -> new SystemProfile(
                resultSet.getLong("profile_id"),
                resultSet.getString("username"),
                resultSet.getString("password")
        );
    }
}
