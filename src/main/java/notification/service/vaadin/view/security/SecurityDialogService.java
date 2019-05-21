package notification.service.vaadin.view.security;

import notification.service.backend.domain.SecurityEntity;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecurityDialogService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityCrudService.class);

    private final SecurityCrudService securityCrudService;

    public SecurityDialogService(SecurityCrudService securityCrudService) {
        this.securityCrudService = Objects.requireNonNull(securityCrudService,
                "Security crud service can't be null.");
    }

    public void openExistSecurityEntity(SecurityEntity securityEntity) {
        logger.debug("Open security entity with editable: {}.", securityEntity);
        new SecurityWindow(securityCrudService, securityEntity, true);
    }

    public void openEmptySecurityEntity() {
        logger.debug("Open security entity create form.");
        new SecurityWindow(securityCrudService);
    }
}
