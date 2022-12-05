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
package org.meveo.admin.action.payments;

import java.util.Date;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.payment.AccountOperationApi;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
import org.omnifaces.cdi.Param;

/**
 * Standard backing bean for {@link OtherCreditAndCharge} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 * 
 */
@Named
@ViewScoped
public class OtherCreditAndChargeBean extends CustomFieldBean<OtherCreditAndCharge> {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OtherCreditAndCharge} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OtherCreditAndChargeService otherCreditAndChargeService;

    /**
     * Injected @{link OCustomerAccountService} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CustomerAccountService customerAccountService;
    
    /**
     * Injected @{link OCCTemplateService} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OCCTemplateService occTemplateService;
    
    @Inject
    private AccountOperationApi accountOperationApi;

    private CustomerAccount customerAccount;

    private OCCTemplate occTemplate;

    /**
     * CustomerAccoiunt Id passed as a parameter.
     */
    @Inject
    @Param
    private Long customerAccountId;

    /**
     * OCCTemplate Id passed as a parameter.
     */
    @Inject
    @Param
    private Long occTemplateId;

    @Inject
    @Param
    private String initType = null;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OtherCreditAndChargeBean() {
        super(OtherCreditAndCharge.class);
    }

    @PostConstruct
    public void init() {

        if (customerAccountId != null) {
            customerAccount = customerAccountService.findById(customerAccountId);
        }
        if (occTemplateId != null) {
            occTemplate = occTemplateService.findById(occTemplateId);
        }
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return other credit and charge
     */
    public OtherCreditAndCharge initEntity() {

        // Initialize a new one from ID or empty
        if (initType == null) {
            super.initEntity();

            // Either create a new entity from a user selected template
        } else if ("loadFromTemplate".equals(initType)) {
            if (occTemplateId != null) {
                copyFromTemplate(occTemplateService.findById(occTemplateId));

            }
            return entity;

            // Create a new entity from a rejectPayment template
        } else if ("loadFromTemplateRejectPayment".equals(initType)) {
            String occTemplateRejectPaymentCode = paramBeanFactory.getInstance().getProperty("occ.rejectedPayment.dd", "REJ_DDT");
            OCCTemplate occ = occTemplateService.findByCode(occTemplateRejectPaymentCode);
            copyFromTemplate(occ);
            entity.setType("R");

            // Create a new entity from a paymentCheck template
        } else if ("loadFromTemplatePaymentCheck".equals(initType)) {
            String occTemplatePaymentCode = paramBeanFactory.getInstance().getProperty("occ.templatePaymentCheckCode", "PAY_CHK");
            OCCTemplate occ = occTemplateService.findByCode(occTemplatePaymentCode);
            copyFromTemplate(occ);
            entity.setType("P");

        }
        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        entity.setUnMatchingAmount(entity.getAmount());
        //set journal
        OCCTemplate occ = occTemplateService.findByCode(entity.getCode());
        entity.setJournal(occ.getJournal());
        // lazyloading fix
        CustomerAccount customerAccount = customerAccountService.retrieveIfNotManaged(entity.getCustomerAccount());
        entity.setCustomerAccount(customerAccount);
        AccountOperationDto accountOperationDto = new AccountOperationDto(entity);       
        accountOperationApi.create(accountOperationDto); 
        messages.info(new BundleKey("messages", "save.successful"));
        return back();
    }

    @Override
    protected IPersistenceService<OtherCreditAndCharge> getPersistenceService() {
        return otherCreditAndChargeService;
    }

    @Override
    public String back() {
        return "customerAccountDetail";
    }

    /**
     * Load from template payment check
     * 
     * @param customerAccountId Customer acount id
     * @return Next view name
     */
    public String loadFromTemplatePaymentCheck(Long customerAccountId) {
        String occTemplatePaymentCode = paramBeanFactory.getInstance().getProperty("occ.templatePaymentCheckCode", "PAY_CHK");
        OCCTemplate occ = occTemplateService.findByCode(occTemplatePaymentCode);
        if (occ == null) {
            messages.error(new BundleKey("messages", "accountOperation.occTemplatePaymentCheckNotFound"));
            return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=true&mainTab=1&faces-redirect=true";
        }
        return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplatePaymentCheck" + "&edit=true&faces-redirect=true&includeViewParams=true";
    }

    /**
     * Load from template reject payment
     * 
     * @param customerAccountId Customer account id
     * @return Next view name
     */
    public String loadFromTemplateRejectPayment(Long customerAccountId) {
        return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplateRejectPayment" + "&edit=true&faces-redirect=true&includeViewParams=true";

    }

    /**
     * Copy from template
     * 
     * @param occ OCC template
     */
    private void copyFromTemplate(OCCTemplate occ) {
        entity = new OtherCreditAndCharge();
        entity.setCustomerAccount(customerAccount);
        if (occ != null) {
            entity.setCode(occ.getCode());
            entity.setDescription(occ.getDescription());
            entity.setAccountingCode(occ.getAccountingCode());
            entity.setTransactionCategory(occ.getOccCategory());
            entity.setAccountCodeClientSide(occ.getAccountCodeClientSide());
        }

        entity.setMatchingStatus(MatchingStatusEnum.O);
        entity.setDueDate(new Date());
        entity.setTransactionDate(new Date());
    }

    public String loadFromTemplate() {
        return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplate" + "&edit=true&faces-redirect=true&includeViewParams=true&occTemplateId="
                + occTemplate.getId();
    }

    public void setOccTemplate(OCCTemplate occTemplate) {
        this.occTemplate = occTemplate;
    }

    public OCCTemplate getOccTemplate() {
        return occTemplate;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }
}