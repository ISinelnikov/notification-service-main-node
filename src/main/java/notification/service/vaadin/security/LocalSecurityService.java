package notification.service.vaadin.security;

import notification.service.backend.domain.SystemProfile;
import notification.service.backend.repository.internal.SecurityRepository;

import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocalSecurityService implements SecurityService {
    private static final Logger logger = LoggerFactory.getLogger(LocalSecurityService.class);

    private final SecurityRepository securityRepository;

    public LocalSecurityService(SecurityRepository securityRepository) {
        this.securityRepository = Objects.requireNonNull(securityRepository,
                "Security repository can't be null.");
    }

    @Override
    public boolean signIn(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return false;
        }

        SystemProfile profile = securityRepository
                .getSystemProfileByCredentials(username, password);

        if (profile == null) {
            logger.warn("Can't get system profile with username: {} and password: {}.",
                    username, password);
            return false;
        }

        String sessionId = getSessionId(profile.getId());
        logger.debug("Create session id: {} for system profile: {}.", sessionId, profile);

        CurrentUser.set(sessionId);
        return true;
    }

    private static String getSessionId(long profileId) {
        return "SP(" + profileId + "): " + UUID.randomUUID().toString();
    }

    @Override
    public boolean isUserSignedIn() {
        return StringUtils.hasText(CurrentUser.get());
    }
}
