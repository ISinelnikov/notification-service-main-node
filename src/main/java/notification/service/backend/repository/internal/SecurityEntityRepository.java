package notification.service.backend.repository.internal;

import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.repository.base.AbstractDao;
import notification.service.backend.repository.base.ModelModificationException;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SecurityEntityRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(SecurityEntityRepository.class);

    //--- Select all security entity

    private static final String SQL_SELECT_ALL_SECURITY_ENTITY =
            "select row_id, port, ip_address, domain_name, connection_type, security_type, "
                    + "application_name, security_token from security_node";

    //--- Select security entity by id

    private static final String SQL_SELECT_SECURITY_ENTITY_BY_ID =
            SQL_SELECT_ALL_SECURITY_ENTITY + " where row_id = :row_id";

    //--- Select security entity by token value

    private static final String SQL_SELECT_SECURITY_ENTITY_BY_TOKEN_VALUE =
            SQL_SELECT_ALL_SECURITY_ENTITY + " where security_token = :security_token";

    //--- Select security entity by application name

    private static final String SQL_SELECT_SECURITY_ENTITY_BY_APPLICATION_NAME =
            SQL_SELECT_ALL_SECURITY_ENTITY + " where application_name = :application_name";

    //--- Insert security entity

    private static final String SQL_INSERT_SECURITY_ENTITY = "insert into security_node "
            + "(row_id, connection_type, ip_address, domain_name, port, security_type, application_name, security_token) "
            + "values "
            + "(:row_id, :connection_type, :ip_address, :domain_name, :port, :security_type, :application_name, :security_token)";

    //--- Update security entity

    private static final String SQL_UPDATE_SECURITY_ENTITY = "update security_node set "
            + "ip_address = :ip_address, security_type = :security_type, "
            + "application_name = :application_name, security_token = :security_token, "
            + "port = :port, connection_type = :connection_type, domain_name = :domain_name "
            + "where row_id = :row_id";

    //--- Delete security entity

    private static final String SQL_DELETE_SECURITY_ENTITY = "delete from security_node where row_id = :row_id";

    public SecurityEntityRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    private static MapSqlParameterSource getParameterSourceByEntity(SecurityEntity entity) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue("row_id", entity.getRowId());
        parameterSource.addValue("ip_address", entity.getIpAddress());
        parameterSource.addValue("security_type", entity.getSecurityType().getAlias());
        parameterSource.addValue("application_name", entity.getApplicationName());
        parameterSource.addValue("domain_name", entity.getDomain());
        parameterSource.addValue("connection_type", entity.getConnectionType().getAlias());
        parameterSource.addValue("port", entity.getPort());
        parameterSource.addValue("security_token", entity.getSecurityToken());

        return parameterSource;
    }

    @Nullable
    public SecurityEntity getEntityByTokenValue(String tokenValue) {
        return getSecurityEntityByQueryAndParameterSource(SQL_SELECT_SECURITY_ENTITY_BY_TOKEN_VALUE,
                new MapSqlParameterSource("security_token", tokenValue));
    }

    public void addSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        MapSqlParameterSource parameterSource = getParameterSourceByEntity(entity);

        try {
            npjtTemplate.update(SQL_INSERT_SECURITY_ENTITY, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method addSecurityEntity({}).", entity, ex);
            throw new ModelModificationException();
        }
    }

    public void updateSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        MapSqlParameterSource parameterSource = getParameterSourceByEntity(entity);

        try {
            npjtTemplate.update(SQL_UPDATE_SECURITY_ENTITY, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method updateSecurityEntity({}).", entity, ex);
            throw new ModelModificationException();
        }
    }

    public void removeSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("row_id", entity.getRowId());

        try {
            npjtTemplate.update(SQL_DELETE_SECURITY_ENTITY, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method removeSecurityEntity({}).", entity.getRowId(), ex);
            throw new ModelModificationException();
        }
    }

    public List<SecurityEntity> getAllSecurityEntity() {
        try {
            return npjtTemplate.query(SQL_SELECT_ALL_SECURITY_ENTITY, getSecurityEntityRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Invoke method getAllFirebaseCredentials().", ex);
        }
        return Collections.emptyList();
    }

    @Nullable
    public SecurityEntity getEntityById(String rowId) {
        return getSecurityEntityByQueryAndParameterSource(SQL_SELECT_SECURITY_ENTITY_BY_ID,
                new MapSqlParameterSource("row_id", rowId));
    }

    private SecurityEntity getSecurityEntityByQueryAndParameterSource(String sql, MapSqlParameterSource parameterSource) {
        try {
            return npjtTemplate.query(sql, parameterSource,
                    getSecurityEntityRowMapper())
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (DataAccessException ex) {
            logger.error("Invoke method getSecurityEntityByQueryAndParameterSource({}, {}).",
                    sql, parameterSource, ex);
        }
        return null;
    }

    private static RowMapper<SecurityEntity> getSecurityEntityRowMapper() {
        return (resultSet, i) -> new SecurityEntity(
                resultSet.getString("row_id"),
                resultSet.getString("application_name"),
                resultSet.getString("security_token"),
                SecurityEntity.ConnectionType
                        .getConnectionType(resultSet.getString("connection_type")),
                resultSet.getString("domain_name"),
                resultSet.getString("ip_address"),
                resultSet.getString("port"),
                SecurityEntity.SecurityType
                        .findTypeByAlias(resultSet.getString("security_type")));
    }

    @Nullable
    public SecurityEntity getEntityByApplicationName(String applicationName) {
        return getSecurityEntityByQueryAndParameterSource(SQL_SELECT_SECURITY_ENTITY_BY_APPLICATION_NAME,
                new MapSqlParameterSource("application_name", applicationName));
    }
}
