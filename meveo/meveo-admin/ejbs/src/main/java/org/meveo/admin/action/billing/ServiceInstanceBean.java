/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.billing;

import java.util.Date;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;

/**
 * Standard backing bean for {@link ServiceInstance} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Named
// TODO: @Scope(ScopeType.CONVERSATION)
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
	User currentUser;

	/**
	 * Offer Id passed as a parameter. Used when creating new Service from Offer
	 * window, so default offer will be set on newly created service.
	 */
	// TODO: @RequestParameter
	private Long offerInstanceId;

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
	/*
	 * TODO: @Begin(nested = true)
	 * 
	 * @Factory("serviceInstance")
	 */
	@Produces
	@Named("serviceInstance")
	public ServiceInstance init() {
		initEntity();
		if (offerInstanceId != null) {
			// serviceInstance.setOfferInstance(offerInstanceService.findById(offerInstanceId));
		}
		return entity;
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	// @Out(value = "serviceInstances", required = false)
	@Produces
	@Named("serviceInstances")
	protected PaginationDataModel<ServiceInstance> getDataModel() {
		return entities;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	/*
	 * TODO: @Begin(join = true)
	 * 
	 * @Factory("serviceInstances")
	 */
	@Produces
	@Named("serviceInstances")
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	//TODO: @End(beforeRedirect = true, root = false)
	public String saveOrUpdate() {
		return saveOrUpdate(entity);
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
			serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);
		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String saveOrUpdate(ServiceInstance entity) {
		if (entity.isTransient()) {
			serviceInstanciation(entity);
			statusMessages.addFromResourceBundle("save.successful");
		} else {
			getPersistenceService().update(entity);
			statusMessages.addFromResourceBundle("update.successful");
		}

		return back();
	}

	public String activateService() {
		log.info("activateService serviceInstanceId:" + entity.getId());
		try {
			serviceInstanceService.serviceActivation(entity, null, null, currentUser);
			statusMessages.addFromResourceBundle("activation.activateSuccessful");
			return "/pages/resource/serviceInstances/serviceInstanceDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String resiliateService() {
		log.info("resiliateService serviceInstanceId:" + entity.getId());
		try {
			// serviceInstanceService.serviceTermination(serviceInstance, new
			// Date(), currentUser);
			statusMessages.addFromResourceBundle("resiliation.resiliateSuccessful");
			return "/pages/resource/serviceInstances/serviceInstanceDetail.seam?objectId="
					+ entity.getId() + "&edit=false";

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String resiliateWithoutFeeService() {
		log.info("cancelService serviceInstanceId:" + entity.getId());
		try {
			// serviceInstanceService.serviceCancellation(serviceInstance, new
			// Date(), currentUser);
			statusMessages.addFromResourceBundle("cancellation.cancelSuccessful");
			return "/pages/resource/serviceInstances/serviceInstanceDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String cancelService() {
		log.info("cancelService serviceInstanceId:" + entity.getId());
		try {
			entity.setStatus(InstanceStatusEnum.CANCELED);
			serviceInstanceService.update(entity, currentUser);
			statusMessages.addFromResourceBundle("resiliation.resiliateSuccessful");
			return "/pages/resource/serviceInstances/serviceInstanceDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	public String suspendService() {
		log.info("closeAccount serviceInstanceId:" + entity.getId());
		try {
			serviceInstanceService.serviceSusupension(entity, new Date(), currentUser);
			statusMessages.addFromResourceBundle("suspension.suspendSuccessful");
			return "/pages/resource/serviceInstances/serviceInstanceDetail.seam?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}
}
