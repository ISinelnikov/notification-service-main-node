package notification.service.backend.repository.user;

import notification.service.backend.domain.UserProfile;
import notification.service.backend.repository.CommonConnection;
import notification.service.backend.repository.base.ModelModificationException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProfileRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileRepositoryTest.class);

    @Test
    public void addUserProfileTest() throws ModelModificationException {
        UserProfileRepository userProfileRepository = new UserProfileRepository(CommonConnection.getJdbcTemplate());

        String userId = "test.user.id";


        UserProfile userProfile = new UserProfile(userId, "test.package", "test.notification.id",
                "test.os", "test.click.id", "test.node");
        userProfileRepository.removeUserProfile(userProfile);
        userProfileRepository.addUserProfile(userProfile);
        UserProfile userProfileById = userProfileRepository.findUserProfileById(userId);

        logger.debug("Result: {}.", userProfileById);
        userProfileRepository.removeUserProfile(userProfile);
    }
}
