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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;

/**
 * Standard backing bean for {@link UserAccount} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ConversationScoped
public class UserAccountListBean extends BaseBean<UserAccount> {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    private Long parentEntityId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserAccountListBean() {
        super(UserAccount.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<UserAccount> getPersistenceService() {
        return userAccountService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public Long getParentEntityId() {
        return parentEntityId;
    }

    @Override
    public void preRenderView() {
        super.preRenderView();

        if (parentEntityId != null) {
            filters.put("billingAccount", billingAccountService.findById(parentEntityId));
        }
    }
}