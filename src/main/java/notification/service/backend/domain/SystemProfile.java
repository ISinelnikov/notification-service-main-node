package notification.service.backend.domain;

import java.util.Objects;

public class SystemProfile {
    private final long id;
    private final String username;
    private final String password;

    public SystemProfile(long id, String username, String password) {
        this.id = id;
        this.username = Objects.requireNonNull(username, "Username can't be null.");
        this.password = Objects.requireNonNull(password, "Password can't be null.");
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SystemProfile{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemProfile)) {
            return false;
        }
        SystemProfile that = (SystemProfile) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
