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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.omnifaces.cdi.Param;

/**
 * Standard backing bean for {@link ServiceInstance} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ServiceInstanceBean extends CustomFieldBean<ServiceInstance> {

    private static final long serialVersionUID = -4881285967381681922L;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private Messages messages;

    /**
     * Offer Id passed as a parameter. Used when creating new Service from Offer window, so default offer will be set on newly created service.
     */
    @Inject
    @Param
    private Long offerInstanceId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ServiceInstanceBean() {
        super(ServiceInstance.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return service instance.
     */
    @Override
    public ServiceInstance initEntity() {
        super.initEntity();

        if (offerInstanceId != null) {
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
            serviceInstanceService.serviceInstanciation(serviceInstance);

        } catch (BusinessException e) {
            log.error("error occurred in service instanciation ", e);
            messages.error(e.getMessage());
        } catch (Exception e) {
            log.error("error generated in service instanciation ", e);
            messages.error(e.getMessage());
        }
        return null;
    }

    public String activateService() {
        log.info("activateService serviceInstanceId:" + entity.getId());

        try {
            entity = serviceInstanceService.refreshOrRetrieve(entity);
            serviceInstanceService.serviceActivation(entity, null, null);
            messages.info(new BundleKey("messages", "activation.activateSuccessful"));

        } catch (BusinessException e) {
            log.error("error in service activation ", e);
            messages.error(e.getMessage());

        } catch (Exception e) {
            log.error("error generated in service activation ", e);
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return null;
    }

    public String resiliateService() {
        log.info("resiliateService serviceInstanceId:" + entity.getId());

        try {
            // serviceInstanceService.serviceTermination(serviceInstance, new
            // Date());
            messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));

        } catch (Exception e) {
            log.error("error in resiliate service", e);
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return null;
    }

    public String resiliateWithoutFeeService() {
        log.info("cancelService serviceInstanceId:" + entity.getId());

        try {
            // serviceInstanceService.serviceCancellation(serviceInstance, new
            // Date());
            messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));

        } catch (Exception e) {
            log.error("failed to resiliate without fee service", e);
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return null;
    }

    public String cancelService() {
        log.info("cancelService serviceInstanceId:" + entity.getId());

        try {
            entity.setStatus(InstanceStatusEnum.CANCELED);
            serviceInstanceService.update(entity);
            messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));

        } catch (Exception e) {
            log.error("failed to cancel service ", e);
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return null;
    }

    public String suspendService() {
        log.info("closeAccount serviceInstanceId:" + entity.getId());

        try {
            entity = serviceInstanceService.refreshOrRetrieve(entity);
            serviceInstanceService.serviceSuspension(entity, new Date());
            messages.info(new BundleKey("messages", "suspension.suspendSuccessful"));

        } catch (BusinessException e) {
            log.error("failed to suspend service", e);
            messages.error(e.getMessage());
        } catch (Exception e) {
            log.error("error generated in suspend service ", e);
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return null;
    }

    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("recurringChargeInstances");
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        boolean quantityChanged = entity.isQuantityChanged();

        String outcome = super.saveOrUpdate(killConversation);

        boolean allowServiceMultiInstantiation = ParamBeanFactory.getAppScopeInstance().isServiceMultiInstantiation();
        if (entity.getStatus() != InstanceStatusEnum.INACTIVE && quantityChanged) {
            messages.warn(new BundleKey("messages", allowServiceMultiInstantiation ? "serviceInstance.quantityChangedMulti" : "serviceInstance.quantityChanged"));
        }

        return outcome;
    }
}
