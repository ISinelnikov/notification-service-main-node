package notification.service.backend.domain;

import javax.annotation.Nullable;

public class MainNodeConfiguration {
    @Nullable
    private String databaseUrl;
    @Nullable
    private String databaseUsername;
    @Nullable
    private String databasePassword;

    @Nullable
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    @Nullable
    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    @Nullable
    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    @Override
    public String toString() {
        return "MainNodeConfiguration{" +
                "databaseUrl='" + databaseUrl + '\'' +
                ", databaseUsername='" + databaseUsername + '\'' +
                ", databasePassword='" + databasePassword + '\'' +
                '}';
    }
}
