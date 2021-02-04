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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.IEntity;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.finance.AccountingWriting;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link AccountOperation} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author anasseh
 * @lastModifiedVersion willBeSetHere
 */
@Named
@ViewScoped
public class AccountOperationBean extends CustomFieldBean<AccountOperation> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link AccountOperation} service. Extends {@link PersistenceService}.
     */
    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    protected MatchingCodeService matchingCodeService;

    @Inject
    private AccountOperationListBean accountOperationListBean;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public AccountOperationBean() {
        super(AccountOperation.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return account operation
     */
    @Produces
    @Named("accountOperation")
    public AccountOperation init() {
        return initEntity();
    }

    @Override
    protected IPersistenceService<AccountOperation> getPersistenceService() {
        return accountOperationService;
    }

    public String displayOperation(AccountOperation accountOperation) {
        String page = "/pages/payments/accountOperations/showOcc.xhtml";

        if (accountOperation instanceof RecordedInvoice) {
            page = "/pages/payments/accountOperations/showInvoice.xhtml";
        }
        if (accountOperation instanceof AutomatedPayment) {
            page = "/pages/payments/accountOperations/showAutomatedPayment.xhtml";
        }
        if (accountOperation instanceof Payment) {
            page = "/pages/payments/accountOperations/showPayment.xhtml";
        }
        return page;
    }

    /**
     * Match selected operations.
     * 
     * @param customerAccountId Customer account identifier
     * @return Redirect page
     */
    public String matching(Long customerAccountId) {
        List<Long> operationIds = new ArrayList<Long>();
        log.debug("getChecked():" + getSelectedEntities());
        for (IEntity operation : getSelectedEntities()) {
            operationIds.add((Long) operation.getId());
        }
        log.info("operationIds    " + operationIds);
        if (operationIds.isEmpty()) {
            messages.error(new BundleKey("messages", "customerAccount.matchingUnselectedOperation"));
            return null;
        }
        try {
            MatchingReturnObject result = matchingCodeService.matchOperations(customerAccountId, null, operationIds, null);
            if (result.isOk()) {
                messages.info(new BundleKey("messages", "customerAccount.matchingSuccessful"));
            } else {
                accountOperationListBean.setPartialMatchingOps(result.getPartialMatchingOcc());
                return "/pages/payments/customerAccounts/partialMatching.xhtml&faces-redirect=true";
            }

        } catch (NoAllOperationUnmatchedException ee) {
            messages.error(new BundleKey("messages", "customerAccount.noAllOperationUnmatched"));
        } catch (BusinessException ee) {
            messages.error(new BundleKey("messages", ee.getMessage()));
        } catch (Exception e) {
            log.error("failed to matching ", e);
            messages.error(e.getMessage());
        }
        return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
    }

    public String consultMatching(long customerAccountId) {
        List<Long> operationIds = new ArrayList<Long>();
        log.debug("getChecked():" + getSelectedEntities());
        for (IEntity operation : getSelectedEntities()) {
            operationIds.add((Long) operation.getId());
        }
        log.trace(" consultMatching operationIds " + operationIds);
        if (operationIds.isEmpty() || operationIds.size() > 1) {
            messages.warn(new BundleKey("messages", "consultMatching.noOperationSelected"));

            return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
        }
        AccountOperation accountOperation = accountOperationService.findById(operationIds.get(0));
        if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L && accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
            messages.info(new BundleKey("messages", "consultMatching.operationNotMatched"));

            return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
        }
        matchingAmounts = accountOperation.getMatchingAmounts();
        if (matchingAmounts.size() == 1) {
            return "/pages/payments/matchingCode/matchingCodeDetail.xhtml?objectId=" + matchingAmounts.get(0).getMatchingCode().getId() + "&edit=false&faces-redirect=true";
        }
        return "/pages/payments/matchingCode/selectMatchingCode.xhtml?objectId=" + accountOperation.getId() + "&edit=false&faces-redirect=true";
    }
    
    /**
     * Add Litigation 
     * 
     * @param customerAccountId customer account id
     * @return outcome
     */
    public String addLitigation(long customerAccountId) {
        try {            
            log.debug("getChecked():" + getSelectedEntities());
            if (getSelectedEntities() != null && getSelectedEntities().isEmpty()) {
                messages.warn(new BundleKey("messages", "accountOperation.selectTypeOCC"));
                return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
            }
            for (IEntity operation : getSelectedEntities()) {
                recordedInvoiceService.addLitigation((Long) operation.getId());
            }
            messages.info(new BundleKey("messages", "save.successful"));
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
        return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
    }
    
    /**
     * Cancel Litigation 
     * 
     * @param customerAccountId customer account id
     * @return outcome
     */
    public String cancelLitigation(long customerAccountId) {
        try {            
            log.debug("getChecked():" + getSelectedEntities());
            if (getSelectedEntities() != null && getSelectedEntities().isEmpty()) {
                messages.warn(new BundleKey("messages", "accountOperation.selectTypeOCC"));
                return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
            }
            for (IEntity operation : getSelectedEntities()) {
                recordedInvoiceService.cancelLitigation((Long) operation.getId());
            }
            messages.info(new BundleKey("messages", "save.successful"));
        } catch (Exception e) {
            messages.error(e.getMessage());
        }
        return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
    }

    public List<MatchingAmount> getMatchingAmounts() {
        return matchingAmounts;
    }

    public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
        this.matchingAmounts = matchingAmounts;
    }

    public String getDate() {
        return (new Date()).toString();
    }

    public LazyDataModel<AccountOperation> getAccountOperations(CustomerAccount ca) {
        if (!ca.isTransient()) {
            filters.put("customerAccount", ca);
            return getLazyDataModel();
        } else {
            return null;
        }
    }
    
    /**
    * get AOs attached to an accounting writing
    */	
    public LazyDataModel<AccountOperation> getAccountOperations(AccountingWriting aw) {
        if (aw != null) {
            filters.put("list accountingWritings", aw);
            return getLazyDataModel();
        } else {
            return null;
        }
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("customerAccount");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("customerAccount");
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return null;
    }
}