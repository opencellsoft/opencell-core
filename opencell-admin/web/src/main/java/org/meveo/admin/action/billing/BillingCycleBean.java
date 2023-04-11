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

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingEntityTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingCycleService;

/**
 * Standard backing bean for {@link BillingCycle} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named

public class BillingCycleBean extends CustomFieldBean<BillingCycle> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link BillingCycle} service. Extends {@link PersistenceService}
     * .
     */
    @Inject
    private BillingCycleService billingCycleService;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public BillingCycleBean() {
        super(BillingCycle.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<BillingCycle> getPersistenceService() {
        return billingCycleService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public List<BillingCycle> listSubscriptionBillingCycle() {
        List<BillingCycle> billingCycleSource = billingCycleService.list();
        List<BillingCycle> billingCycleTarget = new ArrayList<BillingCycle>();

        for (BillingCycle billingCycle : billingCycleSource) {
            if (BillingEntityTypeEnum.SUBSCRIPTION.equals(billingCycle.getType())) {
                billingCycleTarget.add(billingCycle);
            }
        }
        return billingCycleTarget;
    }

    public List<BillingCycle> listOrderBillingCycle() {
        List<BillingCycle> billingCycleSource = billingCycleService.list();
        List<BillingCycle> billingCycleTarget = new ArrayList<BillingCycle>();

        for (BillingCycle billingCycle : billingCycleSource) {
            if (BillingEntityTypeEnum.ORDER.equals(billingCycle.getType())) {
                billingCycleTarget.add(billingCycle);
            }
        }
        return billingCycleTarget;
    }

    public List<BillingCycle> listBillingAccountBillingCycle() {
        List<BillingCycle> billingCycleSource = billingCycleService.list();
        List<BillingCycle> billingCycleTarget = new ArrayList<BillingCycle>();

        for (BillingCycle billingCycle : billingCycleSource) {
            if (BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) || StringUtils.isBlank(billingCycle.getType())) {
                billingCycleTarget.add(billingCycle);
            }
        }
        return billingCycleTarget;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (entity.getType() == null) {
            entity.setType(BillingEntityTypeEnum.BILLINGACCOUNT);
        }
        return super.saveOrUpdate(killConversation);
        
    }

}