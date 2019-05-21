package notification.service.backend.service;

import notification.service.backend.cache.UserCache;
import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.domain.UserProfile;
import notification.service.backend.repository.user.EventHistoryRepository;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.domain.ServerRegistrationDto;
import notification.service.domain.user.EventHistoryRow;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final BinomIntegration binomIntegration;
    private final UserCache userCache;
    private final EventHistoryRepository historyRepository;

    public UserService(BinomIntegration binomIntegration, UserCache userCache, EventHistoryRepository historyRepository) {
        this.binomIntegration = Objects.requireNonNull(binomIntegration, "Binom integration can't be null.");
        this.userCache = Objects.requireNonNull(userCache, "User cache can't be null.");
        this.historyRepository = Objects.requireNonNull(historyRepository,
                "History repository can't be null.");
    }

    @Nullable
    public UserProfile findUserById(String userId) {
        return userCache.findUserProfileById(userId);
    }

    public List<UserProfile> getAllUserProfiles() {
        return userCache.getAllUserProfiles();
    }

    public List<String> getAllUserProfileIds() {
        return getAllUserProfiles()
                .stream()
                .map(UserProfile::getUserId)
                .collect(Collectors.toList());
    }

    public void addUserProfileFromRegistrationDto(ServerRegistrationDto signUpDto, SecurityEntity entity)
            throws ModelModificationException {
        logger.debug("Invoke addUserProfileFromRegistrationDto({}, {}).", signUpDto, entity);
        Objects.requireNonNull(entity, "Security entity can't be null.");
        UserProfile currentProfile = findUserById(signUpDto.getUserId());

        UserProfile profile = new UserProfile(signUpDto.getUserId(),
                signUpDto.getNotificationId(),
                entity.getRowId());

        if (currentProfile == null) {
            logger.debug("Create new user profile: {}.", profile);
            userCache.addUserProfile(profile);
        } else {
            logger.debug("Update user profile from: {} to: {}.", currentProfile, profile);
            userCache.updateUserProfile(profile);
        }
    }

    public void removeUser(UserProfile profile) throws ModelModificationException {
        userCache.removeUserProfile(profile);
    }
}
