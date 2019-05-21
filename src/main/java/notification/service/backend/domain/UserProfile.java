package notification.service.backend.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserProfile {
    private final String userId;
    private final String emailAddress;
    private final String nodeId;
    private final LocalDateTime lastUpdate;

    public UserProfile(String userId, String emailAddress, String nodeId) {
        this(userId, emailAddress, nodeId, LocalDateTime.now());
    }

    public UserProfile(String userId, String emailAddress, String nodeId, LocalDateTime lastUpdate) {
        this.userId = Objects.requireNonNull(userId, "UserProfile id can't be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "");
        this.nodeId = nodeId;
        this.lastUpdate = Objects.requireNonNull(lastUpdate, "Last update date can't be null.");
    }

    public String getUserId() {
        return userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId='" + userId + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        UserProfile profile = (UserProfile) o;
        return Objects.equals(getUserId(), profile.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
