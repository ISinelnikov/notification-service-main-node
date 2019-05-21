package notification.service.backend.repository.user;

import notification.service.backend.repository.base.AbstractDao;
import notification.service.domain.user.EventHistoryRow;
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
public class EventHistoryRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(EventHistoryRepository.class);

    private static final String SQL_SELECT_USER_HISTORY_BY_ID = "select row_id, user_id, event_id, event_url, "
            + "event_date, event_count from event_history where user_id = :user_id";

    private static final String SQL_INSERT_USER_HISTORY = "insert into event_history (user_id, "
            + "event_id, event_date, event_url, event_count) "
            + "values "
            + "(:user_id, :event_id, :event_date, :event_url, "
            + "     (select count(history.event_date) "
            + "     from event_history history"
            + "     where history.user_id = :user_id and "
            + "     history.event_id = :event_id)"
            + ")";

    public EventHistoryRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public void saveEventInfo(EventHistoryRow eventHistoryRow) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("user_id", eventHistoryRow.getUserId());
        parameterSource.addValue("event_id", eventHistoryRow.getEventId());
        parameterSource.addValue("event_url", eventHistoryRow.getEventUrl());
        parameterSource.addValue("event_date", DateUtils.convertToDate(eventHistoryRow.getEventDate()));

        try {
            npjtTemplate.update(SQL_INSERT_USER_HISTORY, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke saveEventInfo({}).", eventHistoryRow, ex);
        }
    }

    public List<EventHistoryRow> getUserHistoryById(String userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("user_id", userId);
        try {
            return npjtTemplate.query(SQL_SELECT_USER_HISTORY_BY_ID, parameterSource, getHistoryRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Invoke getUserHistoryById({}).", userId, ex);
        }
        return Collections.emptyList();
    }

    private static RowMapper<EventHistoryRow> getHistoryRowMapper() {
        return (resultSet, i) -> new EventHistoryRow(
                resultSet.getString("user_id"),
                resultSet.getString("event_id"),
                resultSet.getString("event_url"),
                DateUtils.convertToLocalDateTime(resultSet.getTimestamp("event_date")),
                resultSet.getInt("event_count")
        );
    }
}
