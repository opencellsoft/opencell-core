/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.billing;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.RejectedBillingAccountService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link RejectedBillingAccount} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class RejectedBillingAccountBean extends BaseBean<RejectedBillingAccount> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link RejectedBillingAccount} service. Extends {@link PersistenceService}
     */
    @Inject
    private RejectedBillingAccountService rejectedBillingAccountService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public RejectedBillingAccountBean() {
        super(RejectedBillingAccount.class);
    }

    @Override
    protected IPersistenceService<RejectedBillingAccount> getPersistenceService() {
        return rejectedBillingAccountService;
    }

    @Override
    protected String getDefaultSort() {
        return "billingAccount.code";
    }

    /**
     * Method, that is invoked in billing run screen. This method returns billingAccountRejected associated with current Billing Run.
     * 
     * @param br Billing run
     * @return Data model for Primefaces data list component
     */
    public LazyDataModel<RejectedBillingAccount> getBArejected(BillingRun br) {
        if (br == null) {
            log.warn("billingRun is null");
        } else {
            filters.put("billingRun", br);
            return getLazyDataModel();
        }

        return null;
    }
}