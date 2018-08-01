/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.commons.utils.StringUtils;
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
@ViewScoped
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
            if(BillingEntityTypeEnum.SUBSCRIPTION.equals(billingCycle.getType())) {
                billingCycleTarget.add(billingCycle);
            }
        }
        return billingCycleTarget;
    }
    
    public List<BillingCycle> listOrderBillingCycle() {
        List<BillingCycle> billingCycleSource = billingCycleService.list();
        List<BillingCycle> billingCycleTarget = new ArrayList<BillingCycle>();

        for (BillingCycle billingCycle : billingCycleSource) {
            if(BillingEntityTypeEnum.ORDER.equals(billingCycle.getType())) {
                billingCycleTarget.add(billingCycle);
            }
        }
        return billingCycleTarget;
    }
    
    public List<BillingCycle> listBillingAccountBillingCycle() {
        List<BillingCycle> billingCycleSource = billingCycleService.list();
        List<BillingCycle> billingCycleTarget = new ArrayList<BillingCycle>();

        for (BillingCycle billingCycle : billingCycleSource) {
            if(BillingEntityTypeEnum.BILLINGACCOUNT.equals(billingCycle.getType()) || StringUtils.isBlank(billingCycle.getType())) {
                billingCycleTarget.add(billingCycle);
            }
        }
        return billingCycleTarget;
    }
	
}