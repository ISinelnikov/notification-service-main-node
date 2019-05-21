package notification.service.backend.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class SecurityEntity {
    private String rowId;
    private String applicationName;
    private String securityToken;

    @Nullable
    private String domain;

    private ConnectionType connectionType;
    private String ipAddress;
    private String port;

    private SecurityType securityType;

    public SecurityEntity() {
        rowId = "NODE:" + System.currentTimeMillis();
    }

    public SecurityEntity(String rowId, String applicationName, String securityToken,
            ConnectionType connectionType, @Nullable String domain, String ipAddress, String port, SecurityType securityType) {
        this.rowId = Objects.requireNonNull(rowId, "Row id can't be null.");
        this.applicationName = Objects.requireNonNull(applicationName, "Application name can't be null.");
        this.securityToken = Objects.requireNonNull(securityToken, "Security token can't be null.");
        this.domain = domain;
        this.connectionType = Objects.requireNonNull(connectionType, "Connection type can't be null.");
        this.ipAddress = Objects.requireNonNull(ipAddress, "Ip address can't be null.");
        this.port = Objects.requireNonNull(port, "Port can't be null.");

        this.securityType = Objects.requireNonNull(securityType, "Security type can't be null.");
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Nullable
    public String getDomain() {
        return domain;
    }

    public void setDomain(@Nullable String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "SecurityEntity{" +
                "rowId='" + rowId + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", securityToken='" + securityToken + '\'' +
                ", domain='" + domain + '\'' +
                ", connectionType=" + connectionType +
                ", ipAddress='" + ipAddress + '\'' +
                ", port='" + port + '\'' +
                ", securityType=" + securityType +
                '}';
    }

    public enum SecurityType {
        SITE_NODE("SITE_NODE", "Application server"),
        CLUSTER_NODE("CLUSTER_NODE", "Web");

        private final String alias;
        private final String description;

        SecurityType(String alias, String description) {
            this.alias = Objects.requireNonNull(alias, "Alias can't be null.");
            this.description = Objects.requireNonNull(description, "Description can't be null.");
        }

        public String getAlias() {
            return alias;
        }

        public String getDescription() {
            return description;
        }

        public static SecurityType findTypeByAlias(String alias) {
            return Arrays.stream(values())
                    .filter(securityType -> securityType.getAlias().equals(alias))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Not found type by alias: " + alias + "."));
        }

        public static List<SecurityType> getEnabledTypes() {
            return Arrays.asList(values());
        }
    }

    public enum ConnectionType {
        HTTP("http", "http://"),
        HTTPS("https", "https://");

        private final String alias;
        private final String description;

        ConnectionType(String alias, String description) {
            this.alias = Objects.requireNonNull(alias, "Alias can't be null.");
            this.description = Objects.requireNonNull(description, "Description can't be null.");
        }

        public String getAlias() {
            return alias;
        }

        public String getDescription() {
            return description;
        }

        public static ConnectionType getConnectionType(String alias) {
            return alias.equalsIgnoreCase(HTTP.getAlias()) ? HTTP : HTTPS;
        }

        public static List<ConnectionType> getEnabledTypes() {
            return Arrays.asList(values());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityEntity)) {
            return false;
        }
        SecurityEntity that = (SecurityEntity) o;
        return Objects.equals(getRowId(), that.getRowId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRowId());
    }
}
