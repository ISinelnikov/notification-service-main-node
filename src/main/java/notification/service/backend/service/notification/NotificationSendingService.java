package notification.service.backend.service.notification;

import notification.service.backend.cache.SecurityEntityCache;
import notification.service.backend.domain.FirebaseCredentials;
import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.domain.UserProfile;
import notification.service.backend.service.FirebaseCredentialsService;
import notification.service.backend.service.UserService;
import notification.service.domain.notification.firebase.NotificationTemplate;
import notification.service.domain.notification.rich.RichNotification;
import notification.service.domain.notification.rich.RichTemplate;
import notification.service.domain.notification.rich.component.RichWrapper;
import notification.service.utils.HttpUtils;
import notification.service.utils.JsonUtils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationSendingService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationSendingService.class);

    private static final String ACCESS_TOKEN = "Access-Token";

    private static final String SEND_NOTIFICATION = "/api/notification/send";

    private final FirebaseCredentialsService credentialsService;
    private final UserService userService;
    private final HttpUtils httpUtils;
    private final SecurityEntityCache entityCache;

    public NotificationSendingService(FirebaseCredentialsService credentialsService,
            UserService userService, HttpUtils httpUtils,
            SecurityEntityCache entityCache) {
        this.credentialsService = Objects.requireNonNull(credentialsService,
                "Credentials service can't be null.");
        this.userService = Objects.requireNonNull(userService, "UserProfile service can't be null.");
        this.httpUtils = Objects.requireNonNull(httpUtils, "Http utils can't be null.");
        this.entityCache = Objects.requireNonNull(entityCache, "Entity cache can't be null.");
    }

    public Map<String, Optional<String>> sendMassNotification(NotificationTemplate template, List<String> userIds) {
        return userIds
                .stream()
                .map(userId -> {
                    String status;
                    try {
                        status = sendNotification(template, userId);
                    } catch (NotificationSendingException ex) {
                        status = ex.getMessage();
                        logger.warn("Can't send notification for user: {}.", userId);
                    }
                    return new AbstractMap.SimpleEntry<>(userId, Optional.ofNullable(status));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String sendNotification(RichTemplate template, String userId) throws NotificationSendingException {
        return sendRichNotification(template, userId);
    }

    public String sendRichNotification(NotificationTemplate template, String userId) throws NotificationSendingException {
        UserProfile recipient = userService.findUserById(userId);

        if (recipient == null) {
            throw new NotificationSendingException("Not found user with id: '" + userId + "'");
        }

        FirebaseCredentials credential = credentialsService
                .findFirebaseCredentialByPackageName(recipient.getPackageName());

        if (credential == null) {
            logger.error("Not found android application data for application id: {}.", recipient.getPackageName());
            throw new NotificationSendingException("Not found package: '" + recipient.getPackageName() + "'");
        }

        SecurityEntity entity = entityCache
                .findSecurityEntityById(recipient.getNodeId());

        if (entity == null) {
            logger.error("Not found node by application name: {}.", credential);
            throw new NotificationSendingException("Not found node by application name: '"
                    + credential.getApplicationName() + "'");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_TOKEN, entity.getSecurityToken());

        String destinationNodeUrl;

        // We can save empty domain name
        if (StringUtils.hasText(entity.getDomain())) {
            destinationNodeUrl = entity.getDomain();
        } else {
            destinationNodeUrl = entity.getConnectionType().getDescription()
                    + entity.getIpAddress()
                    + ":" + entity.getPort();
        }
        destinationNodeUrl += SEND_NOTIFICATION;


        RichWrapper notificationWrapper = new RichWrapper(
                credential.getServerKey(),
                userId,
                template.getTemplateId(),
                new RichNotification(recipient.getNotificationId(), template.convertToRichTemplateWithPersonality(userId)));

        logger.info("Notification wrapper: {}.", JsonUtils.convertObjectToJson(notificationWrapper));

        ResponseEntity<String> notificationQuery = httpUtils.jsonPostRequest(destinationNodeUrl, headers, notificationWrapper);

        logger.info("Get pojo info for user: {} completed with status: {} and message: {}. Node url: {}.",
                recipient, notificationQuery.getStatusCode(), notificationQuery.getBody(),
                destinationNodeUrl);

        return "Sending notification with status: " + notificationQuery.getStatusCode();
    }
}
