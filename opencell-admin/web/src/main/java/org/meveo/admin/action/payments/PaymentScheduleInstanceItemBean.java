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

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.primefaces.model.LazyDataModel;


/**
 * Standard backing bean for {@link PaymentScheduleInstanceItem} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentScheduleInstanceItemBean extends CustomFieldBean<PaymentScheduleInstanceItem> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link PaymentScheduleInstanceItem} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentScheduleInstanceItemBean() {
        super(PaymentScheduleInstanceItem.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return payment gateway.
     * 
     */
    @Override
    public PaymentScheduleInstanceItem initEntity() {
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
    protected IPersistenceService<PaymentScheduleInstanceItem> getPersistenceService() {
        return paymentScheduleInstanceItemService;
    }

    @Override
    protected String getDefaultSort() {
        return "dueDate";
    }
    public LazyDataModel<PaymentScheduleInstanceItem> getItemsForInstance(PaymentScheduleInstance paymentScheduleInstance) throws BusinessException {              
        // filters.put("serviceInstance", serviceInstanceService.refreshOrRetrieve(serviceInstance));    
     filters.put("paymentScheduleInstance", paymentScheduleInstance);    
     return getLazyDataModel();
 }   
}
