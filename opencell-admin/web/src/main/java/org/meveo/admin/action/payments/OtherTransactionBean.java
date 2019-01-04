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
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OtherTransaction;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OtherTransactionService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link OtherTransaction} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author hznibar
 * @lastModifiedVersion 5.4.0
 */
@Named
@ViewScoped
public class OtherTransactionBean extends CustomFieldBean<OtherTransaction> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OtherTransaction} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OtherTransactionService otherTransactionService;

    @Inject
    protected MatchingCodeService matchingCodeService;

    @Inject
    private OtherTransactionListBean otherTransactionListBean;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OtherTransactionBean() {
        super(OtherTransaction.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return account operation
     */
    @Produces
    @Named("otherTransaction")
    public OtherTransaction init() {
        return initEntity();
    }

    @Override
    protected IPersistenceService<OtherTransaction> getPersistenceService() {
        return otherTransactionService;
    }

    public String displayOperation(OtherTransaction otherTransaction) {
        String page = "/pages/payments/otherTransactions/showOcc.xhtml";

//        if (otherTransaction instanceof RecordedInvoice) {
//            page = "/pages/payments/otherTransactions/showInvoice.xhtml";
//        }
//        if (otherTransaction instanceof AutomatedPayment) {
//            page = "/pages/payments/otherTransactions/showAutomatedPayment.xhtml";
//        }
//        if (otherTransaction instanceof Payment) {
//            page = "/pages/payments/otherTransactions/showPayment.xhtml";
//        }
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
                otherTransactionListBean.setPartialMatchingOps(result.getPartialMatchingOcc());
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
        OtherTransaction otherTransaction = otherTransactionService.findById(operationIds.get(0));
        if (otherTransaction.getMatchingStatus() != MatchingStatusEnum.L && otherTransaction.getMatchingStatus() != MatchingStatusEnum.P) {
            messages.info(new BundleKey("messages", "consultMatching.operationNotMatched"));

            return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?customerAccountId=" + customerAccountId + "&edit=false&mainTab=1&faces-redirect=true";
        }
//        matchingAmounts = otherTransaction.getMatchingAmounts();
//        if (matchingAmounts.size() == 1) {
//            return "/pages/payments/matchingCode/matchingCodeDetail.xhtml?objectId=" + matchingAmounts.get(0).getMatchingCode().getId() + "&edit=false&faces-redirect=true";
//        }
        return "/pages/payments/matchingCode/selectMatchingCode.xhtml?objectId=" + otherTransaction.getId() + "&edit=false&faces-redirect=true";
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
                messages.warn(new BundleKey("messages", "otherTransaction.selectTypeOCC"));
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
                messages.warn(new BundleKey("messages", "otherTransaction.selectTypeOCC"));
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

    public LazyDataModel<OtherTransaction> getOtherTransactions(Seller seller) {
        if (!seller.isTransient() && seller.getGeneralLedger() != null && !seller.getGeneralLedger().isTransient()) {
            filters.put("generalLedger", seller.getGeneralLedger());
            return getLazyDataModel();
        } else {
            return null;
        }
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("generalLedger");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("generalLedger");
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return null;
    }
}