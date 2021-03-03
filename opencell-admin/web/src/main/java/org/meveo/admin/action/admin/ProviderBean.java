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
package org.meveo.admin.action.admin;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.crm.Provider;
import org.meveo.model.dwh.GdprConfiguration;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.Param;
import org.primefaces.model.DualListModel;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 *
 * @lastModifiedVersion 6.X
 */
@Named
@ViewScoped
public class ProviderBean extends CustomFieldBean<Provider> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProviderService providerService;
    

	@Inject
    @Param
    private String mode;

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
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    public Provider initEntity() {

        if ("appConfiguration".equals(mode)) {
            setObjectId(appProvider.getId());
        }

        super.initEntity();

        if (entity.getId() != null && entity.getInvoiceConfiguration() == null) {
            InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration();
            entity.setInvoiceConfiguration(invoiceConfiguration);
        }

        if (entity.getBankCoordinates() == null) {
            entity.setBankCoordinates(new BankCoordinates());
        }

        if (entity.getGdprConfiguration() == null) {
            GdprConfiguration gdprConfiguration = new GdprConfiguration();
            entity.setGdprConfiguration(gdprConfiguration);
        }

        if (entity.getRumSequence() == null) {
            entity.setRumSequence(new GenericSequence());
        }

        if (entity.getCustomerNoSequence() == null) {
            entity.setCustomerNoSequence(new GenericSequence());
        }

        if (entity.getCurrency() != null) {
            entity.getCurrency().getCurrencyCode();
        }
        if (entity.getCountry() != null) {
            entity.getCountry().getCountryCode();
        }
        if (entity.getLanguage() != null) {
            entity.getLanguage().getLanguageCode();
        }
        
        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        Provider provider = getEntity();
        if (provider.getId() != null) {
            provider = providerService.findById(provider.getId());
        }
        provider.getPaymentMethods().clear();
        getEntity().setPaymentMethods(paymentMethodsModel.getTarget());
        String returnTo = super.saveOrUpdate(killConversation);

        if ("appConfiguration".equals(mode)) {
            return "providerSelfDetail";
        }

        return returnTo;
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