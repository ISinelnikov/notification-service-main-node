package notification.service.backend.cache;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.repository.user.UserProfileRepository;

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
public class UserCache {
    private static final Logger logger = LoggerFactory.getLogger(UserCache.class);

    private static final long FIVE_MINUTES = 1_000 * 60 * 5;

    private final List<UserProfile> fireUserProfileCache = new CopyOnWriteArrayList<>();

    private final LoadingCache<String, Optional<UserProfile>> idToProfileCache =
            buildLoadingCache(20, this::getUserProfileById, null);

    private final UserProfileRepository userProfileRepository;

    public UserCache(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = Objects.requireNonNull(userProfileRepository,
                "UserProfile repository can't be null.");
        initCacheFromRepository();
    }

    private void initCacheFromRepository() {
        logger.debug("Init user profile cache from repository...");
        userProfileRepository.getAllUserProfiles().forEach(profile -> {
            logger.debug("Put user profile to cache: {}.", profile);
            idToProfileCache.put(profile.getUserId(), Optional.of(profile));
        });
    }

    public List<UserProfile> getAllUserProfiles() {
        return updateListByMap(fireUserProfileCache, idToProfileCache.asMap());
    }

    @Nullable
    public UserProfile findUserProfileById(String userId) {
        return idToProfileCache.get(userId).orElse(null);
    }

    public void addUserProfile(UserProfile profile) throws ModelModificationException {
        userProfileRepository.addUserProfile(profile);
        idToProfileCache.put(profile.getUserId(), Optional.of(profile));
        fireUserProfileCache.add(profile);
    }

    public void updateUserProfile(UserProfile profile) throws ModelModificationException {
        userProfileRepository.updateUserProfile(profile);
        idToProfileCache.refresh(profile.getUserId());
        fireUserProfileCache.remove(profile);
        fireUserProfileCache.add(profile);
    }

    public void removeUserProfile(UserProfile profile) throws ModelModificationException {
        userProfileRepository.removeUserProfile(profile);
        idToProfileCache.invalidate(profile.getUserId());
        fireUserProfileCache.remove(profile);
    }

    private Optional<UserProfile> getUserProfileById(String id) {
        return Optional.ofNullable(userProfileRepository.findUserProfileById(id));
    }

    @Scheduled(fixedDelay = FIVE_MINUTES, initialDelay = FIVE_MINUTES)
    private void refreshCache() {
        logger.debug("Refresh user cache.");
        initCacheFromRepository();
    }
}
