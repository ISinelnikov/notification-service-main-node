package notification.service.backend.service;

import notification.service.cache.RequestTimeCache;

import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class NetworkSecurityService {
    private final RequestTimeCache timeCache;

    public NetworkSecurityService(RequestTimeCache timeCache) {
        this.timeCache = Objects.requireNonNull(timeCache, "Time cache can't be null.");
    }

    public boolean isEnabledRequest(String sourceId) {
        return timeCache.isEnabledRequest(sourceId);
    }
}
