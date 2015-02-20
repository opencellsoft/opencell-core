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
package org.meveo.admin.action.billing;

import java.util.Date;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link ServiceInstance} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ServiceInstanceBean extends BaseBean<ServiceInstance> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link ServiceInstance} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private ServiceInstanceService serviceInstanceService;

	@Inject
	private Messages messages;

	/**
	 * Offer Id passed as a parameter. Used when creating new Service from Offer
	 * window, so default offer will be set on newly created service.
	 */
	@Inject
	@RequestParam
	private Instance<Long> offerInstanceId;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ServiceInstanceBean() {
		super(ServiceInstance.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public ServiceInstance initEntity() {
		super.initEntity();

		if (offerInstanceId != null && offerInstanceId.get() != null) {
			// entity.setOfferInstance(offerInstanceService.findById(offerInstanceId.get());
		}

		return entity;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ServiceInstance> getPersistenceService() {
		return serviceInstanceService;
	}

	public String serviceInstanciation(ServiceInstance serviceInstance) {
		log.info("serviceInstanciation serviceInstanceId:" + serviceInstance.getId());
		try {
			serviceInstanceService.serviceInstanciation(serviceInstance, getCurrentUser());
			
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String activateService() {
		log.info("activateService serviceInstanceId:" + entity.getId());

		try {
			serviceInstanceService.serviceActivation(entity, null, null, getCurrentUser());
			messages.info(new BundleKey("messages", "activation.activateSuccessful"));
			
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
			
		} catch (Exception e) {
			log.error(e.getMessage());
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
		}
		return null;
	}

	public String resiliateService() {
		log.info("resiliateService serviceInstanceId:" + entity.getId());

		try {
			// serviceInstanceService.serviceTermination(serviceInstance, new
			// Date(), currentUser);
			messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
			
		} catch (Exception e) {
			log.error(e.getMessage());
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
		}
		return null;
	}

	public String resiliateWithoutFeeService() {
		log.info("cancelService serviceInstanceId:" + entity.getId());

		try {
			// serviceInstanceService.serviceCancellation(serviceInstance, new
			// Date(), currentUser);
			messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));
		
		} catch (Exception e) {
			log.error(e.getMessage());
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
		}
		return null;
	}

	public String cancelService() {
		log.info("cancelService serviceInstanceId:" + entity.getId());

		try {
			entity.setStatus(InstanceStatusEnum.CANCELED);
			serviceInstanceService.update(entity, getCurrentUser());
			messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));

		} catch (Exception e) {
			log.error(e.getMessage());
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
		}
		return null;
	}

	public String suspendService() {
		log.info("closeAccount serviceInstanceId:" + entity.getId());

		try {
			serviceInstanceService.serviceSuspension(entity, new Date(), getCurrentUser());
			messages.info(new BundleKey("messages", "suspension.suspendSuccessful"));

		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
		}
		return null;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// update recurring charges
		if (entity.getRecurringChargeInstances() != null) {
			for (RecurringChargeInstance recurringChargeInstance : entity.getRecurringChargeInstances()) {
				recurringChargeInstance.setSubscriptionDate(entity.getSubscriptionDate());
			}
		}

		return super.saveOrUpdate(killConversation);
	}

}
