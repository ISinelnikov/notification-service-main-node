package notification.service.backend.cache;

import notification.service.backend.repository.NotificationTemplateRepository;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.notification.rich.RichTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.github.benmanes.caffeine.cache.LoadingCache;

import static notification.service.utils.CacheUtils.buildLoadingCache;
import static notification.service.utils.CacheUtils.getValue;
import static notification.service.utils.CacheUtils.updateListByMap;

@Service
public class NotificationTemplateCache {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateCache.class);

    private final List<RichTemplate> fireNotificationTemplateCache = new CopyOnWriteArrayList<>();

    private final LoadingCache<String, Optional<RichTemplate>> idToTemplateCache =
            buildLoadingCache(20, this::getNotificationTemplateById, null);

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

    public List<RichTemplate> getAllFirebaseCredentials() {
        return updateListByMap(fireNotificationTemplateCache, idToTemplateCache.asMap());
    }

    public void addNotificationTemplate(RichTemplate template) throws ModelModificationException {
        templateRepository.addNotificationTemplate(template);
        idToTemplateCache.put(template.getTemplateId(), Optional.of(template));
        fireNotificationTemplateCache.add(template);
    }

    public void updateNotificationTemplate(RichTemplate template) throws ModelModificationException {
        templateRepository.updateNotificationTemplate(template);
        idToTemplateCache.refresh(template.getTemplateId());
        fireNotificationTemplateCache.remove(template);
        fireNotificationTemplateCache.add(template);
    }

    public void removeNotificationTemplate(RichTemplate template) throws ModelModificationException {
        templateRepository.removeNotificationTemplate(template);
        idToTemplateCache.invalidate(template.getTemplateId());
        fireNotificationTemplateCache.remove(template);
    }

    @Nullable
    public RichTemplate findNotificationTemplateById(String id) {
        return getValue(idToTemplateCache.get(id));
    }

    private Optional<RichTemplate> getNotificationTemplateById(String id) {
        return Optional.ofNullable(templateRepository.getNotificationTemplateById(id));
    }

    @Scheduled()
    private void refreshCache() {
        logger.debug("Refresh notification cache.");
        initCacheFromRepository();
    }
}
