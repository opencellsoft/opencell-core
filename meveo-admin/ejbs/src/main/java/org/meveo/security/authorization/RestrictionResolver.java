package org.meveo.security.authorization;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;

public class RestrictionResolver {
    @Secures
    @AccountManagement
    public boolean isAccountManagement(Identity identity) {
        return identity.hasPermission("account", "accountManagement");
    }

    @Secures
    @AccountVisualization
    public boolean isAccountVisualization(Identity identity) {
        return identity.hasPermission("account", "accountVisualization");
    }

    @Secures
    @AdministrationManagement
    public boolean isAdministrationManagement(Identity identity) {
        return identity.hasPermission("administration", "administrationManagement");
    }

    @Secures
    @AdministrationVisualization
    public boolean isAdministrationVisualization(Identity identity) {
        return identity.hasPermission("administration", "administrationVisualization");
    }

    @Secures
    @BillingManagement
    public boolean isBillingManagement(Identity identity) {
        return identity.hasPermission("billing", "billingManagement");
    }

    @Secures
    @BillingVisualization
    public boolean isBillingVisualization(Identity identity) {
        return identity.hasPermission("billing", "billingVisualization");
    }

    @Secures
    @CatalogManagement
    public boolean isCatalogManagement(Identity identity) {
        return identity.hasPermission("catalog", "catalogManagement");
    }

    @Secures
    @CatalogVisualization
    public boolean isCatalogVisualization(Identity identity) {
        return identity.hasPermission("catalog", "catalogVisualization");
    }

    @Secures
    @CustomerSummaryVisualization
    public boolean isCustomerSummaryVisualization(Identity identity) {
        return identity.hasPermission("customerSummary", "customerSummaryVisualization");
    }

    @Secures
    @ReportingManagement
    public boolean isReportingManagement(Identity identity) {
        return identity.hasPermission("reporting", "reportingManagement");
    }

    @Secures
    @ReportingVisualization
    public boolean isReportingVisualization(Identity identity) {
        return identity.hasPermission("reporting", "reportingVisualization");
    }

    @Secures
    @UserCreate
    public boolean isUserCreate(Identity identity) {
        return identity.hasPermission("administration", "administrationManagement");
    }

    @Secures
    @UserDelete
    public boolean isUserDelete(Identity identity) {
        return identity.hasPermission("administration", "administrationManagement");
    }

    @Secures
    @UserUpdate
    public boolean isUserUpdate(Identity identity) {
        return identity.hasPermission("administration", "administrationManagement");
    }

    @Secures
    @UserView
    public boolean isUserView(Identity identity) {
        return identity.hasPermission("administration", "administrationVisualization");
    }
}