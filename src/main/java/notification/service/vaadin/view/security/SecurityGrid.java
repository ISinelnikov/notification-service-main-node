package notification.service.vaadin.view.security;

import notification.service.backend.domain.SecurityEntity;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.TemplateRenderer;

public class SecurityGrid extends Grid<SecurityEntity> {
    private static final String SECURITY_APPLICATION_PROPERTY = "applicationName";
    private static final String SECURITY_APPLICATION_TEMPLATE = "<div>[[item." + SECURITY_APPLICATION_PROPERTY + "]]</div>";

    private static final String SECURITY_DOMAIN_PROPERTY = "domainName";
    private static final String SECURITY_DOMAIN_TEMPLATE = "<div>[[item." + SECURITY_DOMAIN_PROPERTY + "]]</div>";

    public SecurityGrid() {
        setSizeFull();

        addColumn(SecurityEntity::getRowId)
                .setHeader("Node ID")
                .setFlexGrow(1);

        addColumn(SecurityEntity::getApplicationName)
                .setHeader("Node name")
                .setSortable(true)
                .setFlexGrow(2);

        addColumn(SecurityEntity::getIpAddress)
                .setHeader("Node address")
                .setFlexGrow(2);

        addColumn(TemplateRenderer.<SecurityEntity>of(SECURITY_DOMAIN_TEMPLATE)
                .withProperty(SECURITY_DOMAIN_PROPERTY, entity ->
                        entity.getDomain() != null ? entity.getDomain() : ""))
                .setHeader("Domain")
                .setSortable(true)
                .setFlexGrow(2);

        addColumn(SecurityEntity::getSecurityToken)
                .setHeader("Security token")
                .setFlexGrow(2);

        addColumn(TemplateRenderer.<SecurityEntity>of(SECURITY_APPLICATION_TEMPLATE)
                .withProperty(SECURITY_APPLICATION_PROPERTY, entity -> entity.getSecurityType().getDescription()))
                .setHeader("Application type")
                .setSortable(true)
                .setFlexGrow(1);
    }
}
