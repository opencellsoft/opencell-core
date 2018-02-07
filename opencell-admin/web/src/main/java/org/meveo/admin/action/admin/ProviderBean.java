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
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class ProviderBean extends CustomFieldBean<Provider> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProviderService providerService;
    
    private DualListModel<PaymentMethodEnum> paymentMethodsModel;

    public ProviderBean() {
        super(Provider.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Provider> getPersistenceService() {
        return providerService;
    }

    @Override
    protected String getListViewName() {
        return "providers";
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    
    
    @Override
    public Provider initEntity() {
        super.initEntity();
        if (entity.getId() != null && entity.getInvoiceConfiguration() == null) {
            InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration();
            invoiceConfiguration.setProvider(entity);
            entity.setInvoiceConfiguration(invoiceConfiguration);
        }

        if (entity.getBankCoordinates() == null) {
            entity.setBankCoordinates(new BankCoordinates());
        }

        return entity;
    }

    /**
     * Save or update provider.
     * 
     * @param entity Provider to save.
     * @throws BusinessException
     */
    @Override
    protected Provider saveOrUpdate(Provider entity) throws BusinessException {
        boolean isNew = entity.isTransient();
        if (isNew) {
            entity.getInvoiceConfiguration().setProvider(entity);
        }  
        entity = super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
	getEntity().getPaymentMethods().clear();
	getEntity().setPaymentMethods(paymentMethodsModel.getTarget());
        super.saveOrUpdate(killConversation);
        return "providerSelfDetail";
    }
    
  public DualListModel<PaymentMethodEnum> getPaymentMethodsModel() {
	if (paymentMethodsModel == null) {
	    List<PaymentMethodEnum> source = new ArrayList<PaymentMethodEnum>(Arrays.asList(PaymentMethodEnum.values()));
	    List<PaymentMethodEnum> target = new ArrayList<PaymentMethodEnum>();
	    if (getEntity().getPaymentMethods() != null) {
		target.addAll(getEntity().getPaymentMethods());
	    }
	    source.removeAll(target);
	    paymentMethodsModel = new DualListModel<PaymentMethodEnum>(source, target);
	}
	return paymentMethodsModel;
  }

/**
 * @param paymentMethodsModel the paymentMethodsModel to set
 */
public void setPaymentMethodsModel(DualListModel<PaymentMethodEnum> paymentMethodsModel) {
    this.paymentMethodsModel = paymentMethodsModel;
}

  
}