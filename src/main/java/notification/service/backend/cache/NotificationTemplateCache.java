package notification.service.backend.cache;

import notification.service.backend.repository.NotificationTemplateRepository;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.notification.firebase.NotificationTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.google.common.cache.LoadingCache;

import static notification.service.utils.cache.CacheUtils.FIVE_MINUTES;
import static notification.service.utils.cache.CacheUtils.buildLoadingCache;
import static notification.service.utils.cache.CacheUtils.updateListByMap;

@Service
public class NotificationTemplateCache {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateCache.class);

    private final List<NotificationTemplate> fireNotificationTemplateCache = new CopyOnWriteArrayList<>();

    private final LoadingCache<String, Optional<NotificationTemplate>> idToTemplateCache =
            buildLoadingCache(20, 10, this::getNotificationTemplateById);

    private final NotificationTemplateRepository templateRepository;

    public NotificationTemplateCache(NotificationTemplateRepository templateRepository) {
        this.templateRepository = Objects.requireNonNull(templateRepository,
                "Template repository can't be null.");

        initCacheFromRepository();
    }

    private void initCacheFromRepository() {
        logger.debug("Init notification cache from repository...");
        templateRepository.getAllNotificationTemplate()
                .stream()
                .filter(template -> template.getTemplateId() != null)
                .forEach(template -> {
                    logger.debug("Put notification template to cache: {}.", template);
                    idToTemplateCache.put(template.getTemplateId(), Optional.of(template));
                });
    }

    public List<NotificationTemplate> getAllFirebaseCredentials() {
        return updateListByMap(fireNotificationTemplateCache, idToTemplateCache.asMap());
    }

    public void addNotificationTemplate(NotificationTemplate template) throws ModelModificationException {
        templateRepository.addNotificationTemplate(template);
        idToTemplateCache.put(template.getTemplateId(), Optional.of(template));
        fireNotificationTemplateCache.add(template);
    }

    public void updateNotificationTemplate(NotificationTemplate template) throws ModelModificationException {
        templateRepository.updateNotificationTemplate(template);
        idToTemplateCache.refresh(template.getTemplateId());
        fireNotificationTemplateCache.remove(template);
        fireNotificationTemplateCache.add(template);
    }

    public void removeNotificationTemplate(NotificationTemplate template) throws ModelModificationException {
        templateRepository.removeNotificationTemplate(template);
        idToTemplateCache.invalidate(template.getTemplateId());
        fireNotificationTemplateCache.remove(template);
    }

    @Nullable
    public NotificationTemplate findNotificationTemplateById(String id) {
        return idToTemplateCache.getUnchecked(id).orElse(null);
    }

    private Optional<NotificationTemplate> getNotificationTemplateById(String id) {
        return Optional.ofNullable(templateRepository.getNotificationTemplateById(id));
    }

    @Scheduled(fixedDelay = FIVE_MINUTES, initialDelay = FIVE_MINUTES)
    private void refreshCache() {
        logger.debug("Refresh notification cache.");
        initCacheFromRepository();
    }
}
