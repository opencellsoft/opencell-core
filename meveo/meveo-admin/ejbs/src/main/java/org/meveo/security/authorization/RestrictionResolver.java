package org.meveo.security.authorization;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;

public class RestrictionResolver {
    @Secures
    @AccountManagement
    public boolean isAccountManagement(Identity identity) {
        return identity.hasPermission("account", "manage");
    }

    @Secures
    @AccountVisualization
    public boolean isAccountVisualization(Identity identity) {
        return identity.hasPermission("account", "visualization");
    }

    @Secures
    @AdministrationManagement
    public boolean isAdministrationManagement(Identity identity) {
        return identity.hasPermission("administration", "manage");
    }

    @Secures
    @AdministrationVisualization
    public boolean isAdministrationVisualization(Identity identity) {
        return identity.hasPermission("administration", "visualization");
    }

    @Secures
    @BillingManagement
    public boolean isBillingManagement(Identity identity) {
        return identity.hasPermission("billing", "manage");
    }

    @Secures
    @BillingVisualization
    public boolean isBillingVisualization(Identity identity) {
        return identity.hasPermission("billing", "visualization");
    }

    @Secures
    @CatalogManagement
    public boolean isCatalogManagement(Identity identity) {
        return identity.hasPermission("catalog", "manage");
    }

    @Secures
    @CatalogVisualization
    public boolean isCatalogVisualization(Identity identity) {
        return identity.hasPermission("catalog", "visualization");
    }

    @Secures
    @CustomerSummaryVisualization
    public boolean isCustomerSummaryVisualization(Identity identity) {
        return identity.hasPermission("customerSummary", "visualization");
    }

    @Secures
    @ReportingManagement
    public boolean isReportingManagement(Identity identity) {
        return identity.hasPermission("reporting", "manage");
    }

    @Secures
    @ReportingVisualization
    public boolean isReportingVisualization(Identity identity) {
        return identity.hasPermission("reporting", "visualization");
    }

    @Secures
    @UserCreate
    public boolean isUserCreate(Identity identity) {
        return identity.hasPermission("user", "create");
    }

    @Secures
    @UserDelete
    public boolean isUserDelete(Identity identity) {
        return identity.hasPermission("user", "delete");
    }

    @Secures
    @UserUpdate
    public boolean isUserUpdate(Identity identity) {
        return identity.hasPermission("user", "update");
    }

    @Secures
    @UserView
    public boolean isUserView(Identity identity) {
        return identity.hasPermission("user", "view");
    }
}