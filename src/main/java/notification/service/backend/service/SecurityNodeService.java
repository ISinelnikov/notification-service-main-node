package notification.service.backend.service;

import notification.service.backend.cache.SecurityEntityCache;
import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.repository.base.ModelModificationException;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecurityNodeService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityNodeService.class);

    private final SecurityEntityCache entityCache;

    public SecurityNodeService(SecurityEntityCache entityCache) {
        this.entityCache = Objects.requireNonNull(entityCache, "Entity cache can't be null.");
    }

    public List<SecurityEntity> getAllSecurityEntity() {
        return entityCache.getAllSecurityEntity();
    }

    @Nullable
    public SecurityEntity getSecurityEntityIdByTokenValue(String securityToken) {
        return entityCache.findSecurityEntityByTokenValue(securityToken);
    }

    @Nullable
    public SecurityEntity findSecurityEntity(String rowId) {
        return entityCache.findSecurityEntityById(rowId);
    }

    public boolean validateNodeCredentials(String securityToken, String ipAddress) {
        SecurityEntity entity = getSecurityEntityIdByTokenValue(securityToken);

        logger.info("Invoke validateNodeCredentials(token: {}, ip: {}). Security entity: {}.",
                securityToken, ipAddress, entity);

        if (entity == null) {
            logger.warn("Can't get security entity by token value: {}.", securityToken);
            return false;
        }

        return entity.getIpAddress().equals(ipAddress);
    }

    public void addSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        logger.debug("Create new security entity: {}.", entity);
        entityCache.addSecurityEntity(entity);
    }

    public void updateSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        logger.debug("Update security entity: {}.", entity);
        entityCache.updateSecurityEntity(entity);
    }

    public void removeSecurityEntity(SecurityEntity entity) throws ModelModificationException {
        logger.debug("Remove security entity: {}.", entity);
        entityCache.removeSecurityEntity(entity);
    }
}
