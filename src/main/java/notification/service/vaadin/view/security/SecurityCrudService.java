package notification.service.vaadin.view.security;

import notification.service.backend.domain.SecurityEntity;
import notification.service.backend.repository.base.ModelModificationException;
import notification.service.backend.service.SecurityNodeService;
import notification.service.vaadin.common.CrudService;

import java.util.Objects;
import org.springframework.stereotype.Service;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

@Service
public class SecurityCrudService implements CrudService<SecurityEntity> {
    private final SecurityNodeService securityNodeService;
    private final ListDataProvider<SecurityEntity> securityEntityDataProvider;

    public SecurityCrudService(SecurityNodeService securityNodeService) {
        this.securityNodeService = Objects.requireNonNull(securityNodeService,
                "Security node service can't be null.");
        this.securityEntityDataProvider = DataProvider.ofCollection(securityNodeService
                .getAllSecurityEntity());
    }

    public ListDataProvider<SecurityEntity> getSecurityEntityDataProvider() {
        return securityEntityDataProvider;
    }

    @Override
    public void saveModel(SecurityEntity model) throws ModelModificationException {
        securityNodeService.addSecurityEntity(model);
    }

    @Override
    public void updateModel(SecurityEntity model) throws ModelModificationException {
        securityNodeService.updateSecurityEntity(model);
    }

    @Override
    public void removeModel(SecurityEntity model) throws ModelModificationException {
        securityNodeService.removeSecurityEntity(model);
    }

    @Override
    public void refreshAll() {
        securityEntityDataProvider.refreshAll();
    }
}
