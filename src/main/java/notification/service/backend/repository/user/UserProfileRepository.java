package notification.service.backend.repository.user;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.repository.base.AbstractDao;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.utils.DateUtils;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public class UserProfileRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileRepository.class);

    //--- Select all user profile

    private static final String SQL_SELECT_ALL_USER_PROFILE =
            "select user_id, package_name, notification_id, node_id, last_update, "
                    + "os, click_id, is_active from user_profile";

    //--- Get user profile by id

    private static final String SQL_SELECT_USER_PROFILE_BY_ID =
            SQL_SELECT_ALL_USER_PROFILE + " where user_id = :user_id";

    //--- Insert user profile

    private static final String SQL_INSERT_USER_PROFILE =
            "insert into user_profile (user_id, package_name, notification_id, node_id, last_update, os, click_id, is_active) "
                    + "values (:user_id, :package_name, :notification_id, :node_id, :last_update, :os, :click_id, :is_active)";

    //--- Update user profile

    private static final String SQL_UPDATE_USER_PROFILE =
            "update user_profile set package_name = :package_name, notification_id = :notification_id, "
                    + "node_id = :node_id, last_update = :last_update, os = :os, click_id = :click_id, is_active = :is_active "
                    + "where user_id = :user_id";

    private static final String SQL_UPDATE_USER_PROFILE_ACTIVE =
            "update user_profile set is_active = :is_active where user_id = :user_id";

    //--- Remove user profile

    private static final String SQL_DELETE_USER_PROFILE =
            "delete from user_profile where user_id = :user_id";

    public UserProfileRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<UserProfile> getAllUserProfiles() {
        try {
            return npjtTemplate.query(SQL_SELECT_ALL_USER_PROFILE, getUserProfileRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Invoke method getAllUserProfiles().", ex);
        }
        return Collections.emptyList();
    }

    //--- Get user profile

    @Nullable
    public UserProfile findUserProfileById(String id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("user_id", id);
        try {
            return npjtTemplate.query(SQL_SELECT_USER_PROFILE_BY_ID, parameterSource,
                    getUserProfileRowMapper())
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (DataAccessException ex) {
            logger.error("Invoke method findUserProfileById({}).", id, ex);
        }
        return null;
    }

    //--- Insert user profile

    public void addUserProfile(UserProfile profile) throws ModelModificationException {
        MapSqlParameterSource parameterSourceByProfile = getParameterSourceByProfile(profile);

        try {
            npjtTemplate.update(SQL_INSERT_USER_PROFILE, parameterSourceByProfile);
        } catch (DataAccessException ex) {
            logger.error("Invoke method addUserProfile({}).", profile, ex);
            throw new ModelModificationException();
        }
    }

    //--- Update user profile

    public void updateUserProfile(UserProfile profile) throws ModelModificationException {
        MapSqlParameterSource parameterSourceByProfile = getParameterSourceByProfile(profile);

        try {
            npjtTemplate.update(SQL_UPDATE_USER_PROFILE, parameterSourceByProfile);
        } catch (DataAccessException ex) {
            logger.error("Invoke method updateUserProfile({}).", profile, ex);
            throw new ModelModificationException();
        }
    }

    //--- Update active status

    public void updateUserProfileActiveStatus(String userId, boolean isActive) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("user_id", userId);
        parameterSource.addValue("is_active", isActive);

        try {
            npjtTemplate.update(SQL_UPDATE_USER_PROFILE_ACTIVE, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method updateUserProfileActiveStatus({}, {}).", userId, isActive, ex);
        }
    }

    //--- Remove user profile

    public void removeUserProfile(UserProfile profile) throws ModelModificationException {
        MapSqlParameterSource parameterSource =
                new MapSqlParameterSource("user_id", profile.getUserId());

        try {
            npjtTemplate.update(SQL_DELETE_USER_PROFILE, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method removeUserProfile({}).", profile, ex);
            throw new ModelModificationException();
        }
    }

    private static MapSqlParameterSource getParameterSourceByProfile(UserProfile profile) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue("user_id", profile.getUserId());
        parameterSource.addValue("package_name", profile.getPackageName());
        parameterSource.addValue("notification_id", profile.getNotificationId());
        parameterSource.addValue("node_id", profile.getNodeId());
        parameterSource.addValue("os", profile.getOs());
        parameterSource.addValue("click_id", profile.getClickId());
        parameterSource.addValue("is_active", profile.isActive());
        parameterSource.addValue("last_update", DateUtils.convertToDate(profile.getLastUpdate()));

        return parameterSource;
    }

    private static RowMapper<UserProfile> getUserProfileRowMapper() {
        return (resultSet, i) -> new UserProfile(
                resultSet.getString("user_id"),
                resultSet.getString("package_name"),
                resultSet.getString("notification_id"),
                resultSet.getString("os"),
                resultSet.getString("click_id"),
                resultSet.getBoolean("is_active"),
                resultSet.getString("node_id"),
                DateUtils.convertToLocalDateTime(resultSet.getTimestamp("last_update",
                        DateUtils.DEFAULT_UTC))
        );
    }
}
