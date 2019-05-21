package notification.service.backend.repository.user;

import notification.service.backend.repository.base.AbstractDao;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.SimpleEvent;

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
public class EventRepository extends AbstractDao {
    private static final Logger logger = LoggerFactory.getLogger(EventRepository.class);

    private static final String SQL_SELECT_ALL_EVENTS = "select event_id, event_name, event_option "
            + "from event_info";

    private static final String SQL_SELECT_EVENT_BY_ID = SQL_SELECT_ALL_EVENTS
            + " where event_id = :event_id";

    private static final String SQL_INSERT_EVENT = "insert into event_info(event_id, event_name, event_option) "
            + "values (:event_id, :event_name, :event_option)";

    private static final String SQL_UPDATE_EVENT = "update event_info set event_name = :event_name, "
            + "event_option = :event_option "
            + "where event_id = :event_id";

    private static final String SQL_REMOVE_EVENT = "delete from event_info where event_id = :event_id";

    public EventRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<SimpleEvent> getAllEvents() {
        try {
            return npjtTemplate.query(SQL_SELECT_ALL_EVENTS, getEventRowMapper());
        } catch (DataAccessException ex) {
            logger.error("Invoke method getAllEvents().", ex);
        }
        return Collections.emptyList();
    }

    @Nullable
    public SimpleEvent getEventById(String eventId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("event_id", eventId);
        try {
            return npjtTemplate.query(SQL_SELECT_EVENT_BY_ID, parameterSource, getEventRowMapper())
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (DataAccessException ex) {
            logger.error("Invoke method getEventById({}).", eventId, ex);
        }
        return null;
    }

    public void updateEvent(SimpleEvent event) throws ModelModificationException {
        try {
            npjtTemplate.update(SQL_UPDATE_EVENT, getParameterSourceByEvent(event));
        } catch (DataAccessException ex) {
            logger.error("Invoke method updateEvent({}).", event, ex);
            throw new ModelModificationException();
        }
    }

    public void addEvent(SimpleEvent event) throws ModelModificationException {
        try {
            npjtTemplate.update(SQL_INSERT_EVENT, getParameterSourceByEvent(event));
        } catch (DataAccessException ex) {
            logger.error("Invoke method addEvent({}).", event, ex);
            throw new ModelModificationException();
        }
    }

    public void removeEvent(SimpleEvent event) throws ModelModificationException {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("event_id", event.getEventId());
        try {
            npjtTemplate.update(SQL_REMOVE_EVENT, parameterSource);
        } catch (DataAccessException ex) {
            logger.error("Invoke method removeEvent({}).", event, ex);
            throw new ModelModificationException();
        }
    }

    private static MapSqlParameterSource getParameterSourceByEvent(SimpleEvent event) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("event_id", event.getEventId());
        parameterSource.addValue("event_name", event.getEventName());
        parameterSource.addValue("event_option", event.getEventOption());
        return parameterSource;
    }

    private static RowMapper<SimpleEvent> getEventRowMapper() {
        return (resultSet, i) -> new SimpleEvent(
                resultSet.getString("event_id"),
                resultSet.getString("event_name"),
                resultSet.getString("event_option")
        );
    }
}
