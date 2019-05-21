package notification.service.backend.cache;

import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.repository.internal.SecurityEntityRepository;

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
import static notification.service.utils.CacheUtils.updateListByMap;

@Service
public class SecurityEntityCache {
    private static final Logger logger = LoggerFactory.getLogger(SecurityEntityCache.class);

    private static final long ONE_MINUTE = 1_000 * 60;

    private final LoadingCache<String, Optional<SecurityEntity>> tokenToEntity =
            buildLoadingCache(20, this::getEntityByTokenValue, null);

    private final LoadingCache<String, Optional<SecurityEntity>> idToEntity =
            buildLoadingCache(20, this::getEntityById, null);

    private final LoadingCache<String, Optional<SecurityEntity>> applicationNameToEntity =
            buildLoadingCache(20, this::getEntityByApplicationName, null);

    private final List<SecurityEntity> fireSecurityEntityCache = new CopyOnWriteArrayList<>();

    private final SecurityEntityRepository entityRepository;

    public SecurityEntityCache(SecurityEntityRepository entityRepository) {
        this.entityRepository = Objects.requireNonNull(entityRepository,
                "Entity repository can't be null.");

        initCacheFromRepository();
    }

    private void initCacheFromRepository() {
        logger.debug("Init credentials node cache from repository...");
        entityRepository.getAllSecurityEntity()
                .stream()
                .filter(credentials -> credentials.getRowId() != null)
                .forEach(firebaseCredentials -> {
                    String rowId = firebaseCredentials.getRowId();
                    logger.debug("Put security credentials to cache: {}.", firebaseCredentials);
                    idToEntity.put(rowId, Optional.of(firebaseCredentials));
                });
    }

    public List<SecurityEntity> getAllSecurityEntity() {
        return updateListByMap(fireSecurityEntityCache, idToEntity.asMap());
    }

    public void addSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        entityRepository.addSecurityEntity(entity);
        idToEntity.put(entity.getRowId(), Optional.of(entity));
        fireSecurityEntityCache.add(entity);
    }

    public void updateSecurityEntity(SecurityEntity entity) throws ModelModificationException  {
        entityRepository.updateSecurityEntity(entity);
        idToEntity.refresh(entity.getRowId());
        applicationNameToEntity.refresh(entity.getApplicationName());
        fireSecurityEntityCache.remove(entity);
        fireSecurityEntityCache.add(entity);
        logger.debug("Security entity after refresh: {}", getEntityById(entity.getRowId()));
    }

    public void removeSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        entityRepository.removeSecurityEntity(entity);
        idToEntity.invalidate(entity.getRowId());
        fireSecurityEntityCache.remove(entity);
    }

    //-- Entity by token value

    @Nullable
    public SecurityEntity findSecurityEntityByTokenValue(String securityToken) {
        return tokenToEntity.get(securityToken).orElse(null);
    }

    private Optional<SecurityEntity> getEntityByTokenValue(String tokenValue) {
        return Optional.ofNullable(entityRepository
                .getEntityByTokenValue(tokenValue));
    }

    //-- Entity by id
    @Nullable
    public SecurityEntity findSecurityEntityById(String rowId) {
        return idToEntity.get(rowId).orElse(null);
    }

    private Optional<SecurityEntity> getEntityById(String rowId) {
        return Optional.ofNullable(entityRepository
                .getEntityById(rowId));
    }

    @Scheduled(fixedDelay = ONE_MINUTE, initialDelay = ONE_MINUTE)
    private void refreshCache() {
        logger.debug("Refresh node security cache.");
        initCacheFromRepository();
    }

    //-- Entity by application name
    @Nullable
    public SecurityEntity findSecurityEntityByApplicationName(String applicationName) {
        Optional<SecurityEntity> securityEntity = applicationNameToEntity.get(applicationName);
        return !Objects.requireNonNull(securityEntity).isPresent() ? null : securityEntity.get();
    }

    private Optional<SecurityEntity> getEntityByApplicationName(String applicationName) {
        return Optional.ofNullable(entityRepository
                .getEntityByApplicationName(applicationName));
    }
}
