package notification.service.backend.repository;

import notification.service.backend.repository.base.AbstractDao;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.notification.rich.RichTemplate;
import notification.service.domain.notification.rich.component.RichTextElement;
import notification.service.utils.JsonUtils;

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
public class NotificationTemplateRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateRepository.class);

    //--- Select all notification template

    private static final String SQL_SELECT_ALL_NOTIFICATION_TEMPLATE =
            "select template_id, template_title, sending_mode, notification_url, top_image_url, ringtone, "
                    + "vibration_type, small_notification, big_notification, last_update "
                    + "from notification_template";

    //--- Select notification template by id

    private static final String SQL_SELECT_NOTIFICATION_TEMPLATE_BY_ID =
            SQL_SELECT_ALL_NOTIFICATION_TEMPLATE + " where template_id = :template_id";

    //--- Insert notification template

    private static final String SQL_INSERT_NOTIFICATION_TEMPLATE =
            "insert into notification_template (template_id, template_title, sending_mode, "
                    + "notification_url, top_image_url, vibration_type, ringtone,"
                    + "small_notification, big_notification) values "
                    + "(:template_id, :template_title, :sending_mode, :notification_url, "
                    + ":top_image_url, :vibration_type, :ringtone,"
                    + ":small_notification, :big_notification)";

    //--- Update notification template

    private static final String SQL_UPDATE_NOTIFICATION_TEMPLATE =
            "update notification_template set template_title = :template_title, sending_mode = :sending_mode, "
                    + "notification_url = :notification_url, top_image_url = :top_image_url, "
                    + "vibration_type = :vibration_type, ringtone = :ringtone,"
                    + "small_notification = :small_notification, big_notification = :big_notification "
                    + "where template_id = :template_id";

    //--- Delete notification template

    private static final String SQL_DELETE_NOTIFICATION_TEMPLATE =
            "delete from notification_template where template_id = :template_id";

    public NotificationTemplateRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<RichTemplate> getAllNotificationTemplate() {
        try {
            return npjtTemplate.query(SQL_SELECT_ALL_NOTIFICATION_TEMPLATE, getNotificationTemplateRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Invoke method getAllNotificationTemplate().", ex);
        }
        return Collections.emptyList();
    }

    public void addNotificationTemplate(RichTemplate template) throws ModelModificationException {
        MapSqlParameterSource parameterSource = getParameterSourceByNotificationTemplate(template);

        try {
            npjtTemplate.update(SQL_INSERT_NOTIFICATION_TEMPLATE, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method addNotificationTemplate({}).", template, ex);
            throw new ModelModificationException();
        }
    }

    public void updateNotificationTemplate(RichTemplate template) throws ModelModificationException {
        MapSqlParameterSource parameterSource = getParameterSourceByNotificationTemplate(template);

        try {
            npjtTemplate.update(SQL_UPDATE_NOTIFICATION_TEMPLATE, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method updateNotificationTemplate({}).", template, ex);
            throw new ModelModificationException();
        }
    }

    public void removeNotificationTemplate(RichTemplate template) throws ModelModificationException {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("template_id", template.getTemplateId());

        try {
            npjtTemplate.update(SQL_DELETE_NOTIFICATION_TEMPLATE, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method removeNotificationTemplate({}).", template, ex);
            throw new ModelModificationException();
        }
    }

    @Nullable
    public RichTemplate getNotificationTemplateById(String id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("template_id", id);

        try {
            return npjtTemplate.query(SQL_SELECT_NOTIFICATION_TEMPLATE_BY_ID, parameterSource,
                    getNotificationTemplateRowMapper())
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (DataAccessException ex) {
            logger.error("Invoke method getNotificationTemplateById({}).", id, ex);
        }
        return null;
    }

    private static MapSqlParameterSource getParameterSourceByNotificationTemplate(RichTemplate template) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        return parameterSource;
    }

    private static RowMapper<RichTemplate> getNotificationTemplateRowMapper() {
        return (resultSet, i) -> new RichTemplate(
                resultSet.getString("template_id"),
                JsonUtils.convertJsonToList(resultSet.getString("notification_body"),
                        RichTextElement.class)
        );
    }
}
