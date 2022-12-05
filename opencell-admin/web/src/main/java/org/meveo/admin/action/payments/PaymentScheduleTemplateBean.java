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
package org.meveo.admin.action.payments;

import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;
import org.primefaces.model.LazyDataModel;


/**
 * Standard backing bean for {@link PaymentScheduleTemplate} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentScheduleTemplateBean extends CustomFieldBean<PaymentScheduleTemplate> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link PaymentScheduleTemplate} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PaymentScheduleTemplateService paymentScheduleTemplateService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentScheduleTemplateBean() {
        super(PaymentScheduleTemplate.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return payment gateway.
     * 
     */
    @Override
    public PaymentScheduleTemplate initEntity() {
        super.initEntity();       
        return entity;
    }

    @Override
    public void search() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.search();
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getBackViewSave();
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentScheduleTemplate> getPersistenceService() {
        return paymentScheduleTemplateService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public List<PaymentMethodEnum> getAllowedPaymentMethods() {
        return appProvider.getPaymentMethods();
    }
}
