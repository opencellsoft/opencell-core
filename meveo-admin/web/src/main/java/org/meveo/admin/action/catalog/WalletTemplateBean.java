/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.sql.BatchUpdateException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.BillingWalletTypeEnum;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

@Named
@ViewScoped
public class WalletTemplateBean extends BaseBean<WalletTemplate> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link TradingCountry} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private WalletTemplateService walletTemplateService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public WalletTemplateBean() {
		super(WalletTemplate.class);

	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public WalletTemplate initEntity() {
		super.initEntity();

		entity.setWalletType(BillingWalletTypeEnum.PREPAID);

		return entity;
	}
	
	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<WalletTemplate> getPersistenceService() {
		return walletTemplateService;
	}

	public void test() throws BatchUpdateException {
		throw new BatchUpdateException();
	}

	@Inject
	private WalletService walletService;
	@Inject
	private ServiceChargeTemplateRecurringService recurringService;
	@Inject
	private ServiceChargeTemplateSubscriptionService subscriptionService;
	@Inject
	private ServiceChargeTemplateTerminationService terminationService;
	@Inject
	private ServiceChargeTemplateUsageService usageService;
	
	@Override
	protected void canDelete() {
		boolean result=true;
		List<WalletInstance> instances=walletService.findByWalletTemplate(entity);
		if(instances!=null&&instances.size()!=0){
			result=false;
		}else{
			List<ServiceChargeTemplateRecurring> recurrings=recurringService.findByWalletTemplate(entity);
			if(recurrings!=null&&recurrings.size()!=0){
				for(ServiceChargeTemplateRecurring recurring:recurrings){
					recurring.getWalletTemplates().remove(entity);
					recurringService.update(recurring);
				}
			}
			List<ServiceChargeTemplateSubscription> subscriptions=subscriptionService.findByWalletTemplate(entity);
			if(subscriptions!=null&&subscriptions.size()!=0){
				for(ServiceChargeTemplateSubscription subscription:subscriptions){
					subscription.getWalletTemplates().remove(subscription);
					subscriptionService.update(subscription);
				}
			}
			
			List<ServiceChargeTemplateTermination> terminations=terminationService.findByWalletTemplate(entity);
			if(terminations!=null&&terminations.size()!=0){
				for(ServiceChargeTemplateTermination termination:terminations){
					termination.getWalletTemplates().remove(entity);
					terminationService.update(termination);
				}
			}
			List<ServiceChargeTemplateUsage> usages=usageService.findByWalletTemplate(entity);
			if(usages!=null&&usages.size()!=0){
				for(ServiceChargeTemplateUsage usage:usages){
					usage.getWalletTemplates().remove(usage);
					usageService.update(usage);
				}
			}
			
		}
		if(result){
			this.delete();
		}
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("result", result);
	}
}