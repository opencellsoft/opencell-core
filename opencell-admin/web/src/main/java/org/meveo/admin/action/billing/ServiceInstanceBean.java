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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.EntityListDataModelPF;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.order.OrderHistory;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.omnifaces.cdi.Param;

/**
 * Standard backing bean for {@link ServiceInstance} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 *
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Named
@ViewScoped
public class ServiceInstanceBean extends CustomFieldBean<ServiceInstance> {

    private static final long serialVersionUID = -4881285967381681922L;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private CounterInstanceService counterInstanceService;

    private DataModel<OrderHistory> orderHistoryModel;

    /**
     * Offer Id passed as a parameter. Used when creating new Service from Offer window, so default offer will be set on newly created service.
     */
    @Inject
    @Param
    private Long offerInstanceId;

    private CounterInstance selectedCounterInstance;

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
        selectedCounterInstance = entity.getCounters() != null && entity.getCounters().size() > 0 ? entity.getCounters().values().iterator().next() : null;

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
            serviceInstanceService.serviceActivation(entity);
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

    public String reactiveService() {
        log.info("reactiveService serviceInstanceId:" + entity.getId());

        try {
            entity = serviceInstanceService.refreshOrRetrieve(entity);
            serviceInstanceService.serviceReactivation(entity, new Date(), true, false);
            messages.info(new BundleKey("messages", "activation.activateSuccessful"));

        } catch (BusinessException e) {
            log.error("failed to reactive service", e);
            messages.error(e.getMessage());
        } catch (Exception e) {
            log.error("error generated in reactive service ", e);
            messages.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
        return null;
    }

    public String suspendService() {
        log.info("suspendService serviceInstanceId:" + entity.getId());

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
        return Arrays.asList("chargeInstances");
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

    /**
     * Update subscribedTillDate field in service
     */
    public void updateSubscribedTillDate() {
        entity.updateSubscribedTillAndRenewalNotifyDates();
    }

    /**
     * Auto update end of engagement date.
     */
    public void autoUpdateEndOfEngagementDate() {
        entity.autoUpdateEndOfEngagementDate();
    }

    /**
     * Check is terminated service
     *
     * @return true is the service is terminated
     */
    public boolean isTerminatedService() {
        return serviceInstanceService.willBeTerminatedInFuture(entity);
    }

    /**
     * cancel subscription termination.
     */
    @ActionMethod
    public void cancelServiceTermination() throws BusinessException {
        log.debug("cancelTermination...");
        entity = serviceInstanceService.refreshOrRetrieve(entity);
        serviceInstanceService.cancelServiceTermination(entity);
        serviceInstanceService.refresh(entity);
        messages.info(new BundleKey("messages", "termination.cancelTerminationSuccessful"));

    }

    public CounterInstance getSelectedCounterInstance() {
        if (entity == null) {
            initEntity();
        }
        return selectedCounterInstance;
    }

    public void setSelectedCounterInstance(CounterInstance selectedCounterInstance) {
        if (selectedCounterInstance != null) {
            this.selectedCounterInstance = counterInstanceService.refreshOrRetrieve(selectedCounterInstance);
        } else {
            this.selectedCounterInstance = null;
        }
    }

    /**
     * Get a data model for order history information
     * 
     * @return Data model for order history information
     */
    public DataModel<OrderHistory> getOrderHistoryModel() {

        if (orderHistoryModel == null) {
            orderHistoryModel = new EntityListDataModelPF<OrderHistory>(entity.getOrderHistories());
        }

        return orderHistoryModel;
    }
}