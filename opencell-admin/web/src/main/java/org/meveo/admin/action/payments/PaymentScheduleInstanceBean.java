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
package org.meveo.admin.action.payments;

import java.util.Date;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceService;
import org.primefaces.model.LazyDataModel;


/**
 * Standard backing bean for {@link PaymentScheduleInstance} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentScheduleInstanceBean extends BaseBean<PaymentScheduleInstance> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link PaymentScheduleInstance} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PaymentScheduleInstanceService paymentScheduleInstanceService;
    
    @Inject
    private ServiceInstanceService serviceInstanceService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentScheduleInstanceBean() {
        super(PaymentScheduleInstance.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return payment gateway.
     * 
     */
    @Override
    public PaymentScheduleInstance initEntity() {
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
    
    @ActionMethod
    public String terminate() throws BusinessException {
        log.info("\n\n\n\n\n entity.getEndDate(): "+entity.getEndDate());
        paymentScheduleInstanceService.terminate(entity, entity.getEndDate());
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentScheduleInstance> getPersistenceService() {
        return paymentScheduleInstanceService;
    }

    @Override
    protected String getDefaultSort() {
        return "startDate";
    }
   
    public LazyDataModel<PaymentScheduleInstance> getInstancesForService(ServiceInstance serviceInstance) throws BusinessException {                        
        filters.put("serviceInstance", serviceInstanceService.refreshOrRetrieve(serviceInstance));    
        return getLazyDataModel();
    }
}
