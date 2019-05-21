package notification.service.backend.repository.user;

import notification.service.backend.repository.base.AbstractDao;
import notification.service.domain.message.SendingInfo;
import notification.service.domain.sending.SendingStatus;
import notification.service.domain.sending.UserSendingHistoryRow;
import notification.service.utils.DateUtils;

import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SendingHistoryRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(SendingHistoryRepository.class);

    private static final String SQL_SELECT_HISTORY_INFO_BY_ID = "select user_id, template_id, message_id, status, last_update "
            + "from sending_info where user_id = :user_id";

    private static final String SQL_INSERT_SENDING_INFO =
            "insert into sending_info (user_id, template_id, message_id, status) "
                    + "values "
                    + "(:user_id, :template_id, :message_id, :status)";

    private static final String SQL_UPDATE_SET_DELIVERED_STATUS = "update sending_info "
            + "set status = :status where message_id = :message_id";

    public SendingHistoryRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void insertSendingInfo(SendingInfo sendingInfo) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("user_id", sendingInfo.getUserId());
        parameterSource.addValue("message_id", sendingInfo.getMessageId() == null ?
                "N/A" : sendingInfo.getMessageId());
        parameterSource.addValue("template_id", sendingInfo.getTemplateId());
        SendingStatus currentStatus = SendingStatus.SENDING;
        if (sendingInfo.getMessageId() == null) {
            currentStatus = SendingStatus.ERROR;
        }
        parameterSource.addValue("status", currentStatus.getAlias());

        try {
            npjtTemplate.update(SQL_INSERT_SENDING_INFO, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke insertSendingInfo({}).", sendingInfo, ex);
        }
    }

    public void setDeliveredStatus(String messageId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("message_id", messageId);
        parameterSource.addValue("status", SendingStatus.DELIVERED.getAlias());

        try {
            npjtTemplate.update(SQL_UPDATE_SET_DELIVERED_STATUS, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke setDeliveredStatus({}).", messageId, ex);
        }
    }

    public List<UserSendingHistoryRow> getUserSendingHistoryListById(String userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("user_id", userId);
        try {
            return npjtTemplate.query(SQL_SELECT_HISTORY_INFO_BY_ID, parameterSource, getUserSendingHistoryRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Invoke getUserSendingHistoryListById({}).", userId, ex);
        }
        return Collections.emptyList();
    }

    private static RowMapper<UserSendingHistoryRow> getUserSendingHistoryRowMapper() {
        return (resultSet, i) -> new UserSendingHistoryRow(
                resultSet.getString("user_id"),
                resultSet.getString("template_id"),
                resultSet.getString("message_id"),
                DateUtils.convertToLocalDateTime(resultSet.getTimestamp("last_update")),
                SendingStatus.findSendingStatusByAlias(resultSet.getString("status"))
        );
    }
}
