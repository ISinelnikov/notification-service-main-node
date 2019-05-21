package notification.service.backend.service.notification;

import notification.service.backend.repository.user.SendingHistoryRepository;
import notification.service.domain.message.MessageId;
import notification.service.domain.message.SendingInfo;
import notification.service.domain.sending.UserSendingHistoryRow;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendingHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(SendingHistoryService.class);

    private final SendingHistoryRepository sendingHistoryRepository;

    public SendingHistoryService(SendingHistoryRepository sendingHistoryRepository) {
        this.sendingHistoryRepository = Objects.requireNonNull(sendingHistoryRepository,
                "Sending history repository can't be null.");
    }

    public void insertSendingInfo(SendingInfo sendingInfo) {
        Objects.requireNonNull(sendingInfo, "Sending info can't be null.");
        logger.debug("Insert sending info: {}.", sendingInfo);
        sendingHistoryRepository.insertSendingInfo(sendingInfo);
    }

    public void setDeliveredStatus(MessageId messageId) {
        Objects.requireNonNull(messageId, "Message id can't be null.");
        logger.debug("Set delivered status for message id: {}.", messageId);
        sendingHistoryRepository.setDeliveredStatus(messageId.getMessageId());
    }

    public List<UserSendingHistoryRow> getUserHistoryById(String userId) {
        List<UserSendingHistoryRow> historyListById = sendingHistoryRepository.getUserSendingHistoryListById(userId);
        logger.debug("Get user history by id: {}, result: {}.", userId, historyListById);
        return historyListById;
    }
}
