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

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.admin.util.ListItemsSelector;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.util.Faces;
import org.primefaces.model.DualListModel;

import javax.faces.context.ExternalContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Standard backing bean for {@link BillingAccount} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class BillingAccountBean extends AccountBean<BillingAccount> {

    private static final long serialVersionUID = 1L;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private BillingRunService billingRunService;

    private Long customerAccountId;

    @Inject
    private CounterInstanceService counterInstanceService;

    private boolean returnToAgency;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private DiscountPlanService discountPlanService;
    
    @Inject
    private ExternalContext externalContext;

    /** Selected billing account in exceptionelInvoicing page. */
    private ListItemsSelector<BillingAccount> itemSelector;

    private Date exceptionalInvoicingDate = new Date();

    private Date exceptionalLastTransactionDate = new Date();

    private CounterInstance selectedCounterInstance;

    private DualListModel<DiscountPlan> discountPlanDM;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public BillingAccountBean() {
        super(BillingAccount.class);
    }

    @Override
    public BillingAccount initEntity() {
        super.initEntity();
        returnToAgency = !(entity.getInvoicePrefix() == null);

        if (entity.getId() == null && customerAccountId != null) {
            CustomerAccount customerAccount = customerAccountService.findById(customerAccountId);
            entity.setCustomerAccount(customerAccount);
            entity.setTradingLanguage(customerAccount.getTradingLanguage());
            populateAccounts(customerAccount);
        }

        selectedCounterInstance = entity.getCounters() != null && entity.getCounters().size() > 0 ? entity.getCounters().values().iterator().next() : null;

        this.initNestedFields(entity);

        if (discountPlanDM == null) {
            List<DiscountPlan> sourceDS = null;
            sourceDS = discountPlanService.list();
            discountPlanDM = new DualListModel<>(sourceDS, new ArrayList<>());
        }

        return entity;
    }

    @ActionMethod
    public String instantiateDiscountPlan() throws BusinessException {
        if (entity.getDiscountPlan() != null) {
            DiscountPlan dp = entity.getDiscountPlan();
            entity = billingAccountService.instantiateDiscountPlan(entity, dp);
            entity.setDiscountPlan(null);
        }

        return getEditViewName();
    }

    @ActionMethod
    public String deleteDiscountPlanInstance(DiscountPlanInstance dpi) throws BusinessException {
        billingAccountService.terminateDiscountPlan(entity, dpi);
        return getEditViewName();
//		messages.warn(new BundleKey("messages", "message.discount.terminate.warning"));
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        entity.setCustomerAccount(customerAccountService.findById(entity.getCustomerAccount().getId()));

        try {

            if (entity.isTransient()) {
                billingAccountService.initBillingAccount(entity);
            }

            String outcome = super.saveOrUpdate(killConversation);

            if (outcome != null) {
                return getEditViewName();
            }

        } catch (DuplicateDefaultAccountException e1) {
            messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));
        }
        return null;
    }

    @Override
    public BillingAccount getEntity() {
        BillingAccount ba = super.getEntity();
        this.initNestedFields(ba);
        return ba;
    }

    private void initNestedFields(BillingAccount ba) {
        if (ba.getAddress() == null) {
            ba.setAddress(new Address());
        }
        if (ba.getName() == null) {
            ba.setName(new Name());
        }
        if (ba.getContactInformation() == null) {
            ba.setContactInformation(new ContactInformation());
        }
        if (ba.getDiscountPlanInstances() == null) {
            ba.setDiscountPlanInstances(new ArrayList<>());
        }
    }

    @Override
    protected IPersistenceService<BillingAccount> getPersistenceService() {
        return billingAccountService;
    }

    public String terminateAccount() {
        log.debug("terminateAccount billingAccountId: {}", entity.getId());
        try {

            entity = billingAccountService.refreshOrRetrieve(entity);
            entity = billingAccountService.billingAccountTermination(entity, entity.getTerminationDate(), entity.getTerminationReason());
            messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));

        } catch (Exception e) {
            log.error("Failed to terminate account ", e);
            messages.error(new BundleKey("messages", "resiliation.resiliateUnsuccessful"), e.getMessage());
        }

        return getEditViewName();
    }

    public String cancelAccount() {
        log.info("cancelAccount billingAccountId:" + entity.getId());
        try {
            entity = billingAccountService.refreshOrRetrieve(entity);
            entity = billingAccountService.billingAccountCancellation(entity, new Date());
            messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));

        } catch (Exception e) {
            log.error("Failed to cancel account ", e);
            messages.error(new BundleKey("messages", "cancellation.cancelUnsuccessful"), e.getMessage());
        }
        return getEditViewName();
    }

    public String closeAccount() {
        log.info("closeAccount billingAccountId:" + entity.getId());
        try {
            entity = billingAccountService.closeBillingAccount(entity);
            messages.info(new BundleKey("messages", "close.closeSuccessful"));

        } catch (Exception e) {
            log.error("Failed to close account ", e);
            messages.error(new BundleKey("messages", "close.closeUnsuccessful"), e.getMessage());
        }
        return getEditViewName();
    }

    public String generateInvoice() {
        log.info("generateInvoice billingAccountId:" + entity.getId());
        try {
            entity = billingAccountService.refreshOrRetrieve(entity);

            GenerateInvoiceRequestDto generateInvoiceRequestDto = new GenerateInvoiceRequestDto();
            generateInvoiceRequestDto.setGenerateXML(true);
            generateInvoiceRequestDto.setGeneratePDF(true);
            generateInvoiceRequestDto.setGenerateAO(true);
            generateInvoiceRequestDto.setInvoicingDate(new Date());
            generateInvoiceRequestDto.setFirstTransactionDate(null);
            generateInvoiceRequestDto.setLastTransactionDate(new Date());
            generateInvoiceRequestDto.setOrderNumber(null);
            List<Invoice> invoices = invoiceService.generateInvoice(entity, generateInvoiceRequestDto, null, false, null);

            StringBuilder invoiceNumbers = new StringBuilder();
            for (Invoice invoice : invoices) {
                invoiceNumbers.append(invoice.getInvoiceNumber());
                invoiceNumbers.append(" ");
            }

            messages.info(new BundleKey("messages", "generateInvoice.successful"), invoiceNumbers.toString());

        } catch (Exception e) {
            log.error("Failed to generateInvoice ", e);
            messages.error(e.getMessage());
        }
        return getEditViewName();
    }

    public String launchExceptionalInvoicing() {
        try {
            List<Long> baIds = new ArrayList<Long>();
            for (BillingAccount ba : getSelectedEntities()) {
                baIds.add(ba.getId());
            }
            billingRunService.launchExceptionalInvoicing(baIds, exceptionalInvoicingDate, exceptionalLastTransactionDate, BillingProcessTypesEnum.MANUAL);
            return "/pages/billing/invoicing/billingRuns.xhtml?edit=true&faces-redirect=true";
        } catch (BusinessException e) {
            messages.error(e.getMessage());
        } catch (Exception e) {
            log.error("launchExceptionelInvoicing", e);
            messages.error(e.getMessage());
        }
        return null;
    }

    /**
     * Generates and returns a proforma invoice
     * 
     * @return
     */
    public String generateProformaInvoice() {
        log.info("generateProformaInvoice billingAccountId:" + entity.getId());
        try {
            entity = billingAccountService.refreshOrRetrieve(entity);

            GenerateInvoiceRequestDto generateInvoiceRequestDto = new GenerateInvoiceRequestDto();
            generateInvoiceRequestDto.setGeneratePDF(true);
            generateInvoiceRequestDto.setInvoicingDate(new Date());
            generateInvoiceRequestDto.setLastTransactionDate(new Date());
            List<Invoice> invoices = invoiceService.generateInvoice(entity, generateInvoiceRequestDto, null, true, null);
            for (Invoice invoice : invoices) {
                invoiceService.produceFilesAndAO(false, true, false, invoice.getId(), true, new ArrayList<>());
                String fileName = invoiceService.getFullPdfFilePath(invoice, false);
                Faces.sendFile(new File(fileName), true);
            }

            StringBuilder invoiceNumbers = new StringBuilder();
            for (Invoice invoice : invoices) {
                invoiceNumbers.append(invoice.getInvoiceNumber());
                invoiceNumbers.append(" ");
                invoiceService.cancelInvoice(invoice);
            }

            messages.info(new BundleKey("messages", "generateInvoice.successful"), invoiceNumbers.toString());
            if (isCommitted()) {
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to generateInvoice ", e);
            messages.error(e.getMessage());
        }
        return getEditViewName();
    }

    /**
     * indicates if response has already been committed
     * 
     * @return
     */
    private boolean isCommitted() {
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        return response.isCommitted();
    }
    
    /**
     * Item selector getter. Item selector keeps a state of multiselect checkboxes.
     * 
     * @return ListItemsSelector of BillingAccount
     */
    // TODO: @BypassInterceptors
    public ListItemsSelector<BillingAccount> getItemSelector() {
        if (itemSelector == null) {
            itemSelector = new ListItemsSelector<BillingAccount>(false);
        }
        return itemSelector;
    }

    /**
     * Check/uncheck all select boxes.
     * 
     * @param event notification that the local value of the source component has been changed
     */
    public void checkUncheckAll(ValueChangeEvent event) {
        itemSelector.switchMode();
    }

    /**
     * Listener of select changed event.
     * 
     * @param event Value change event
     */
    public void selectChanged(ValueChangeEvent event) {
        BillingAccount entity = getLazyDataModel().getRowData();
        if (entity != null) {
            itemSelector.check(entity);
        }
    }

    /**
     * Resets item selector.
     */
    public void resetSelection() {
        if (itemSelector == null) {
            itemSelector = new ListItemsSelector<BillingAccount>(false);
        } else {
            itemSelector.reset();
        }
    }

    public boolean isReturnToAgency() {
        return returnToAgency;
    }

    public void setReturnToAgency(boolean returnToAgency) {
        this.returnToAgency = returnToAgency;
    }

    public void setInvoicePrefix() {
        if (returnToAgency) {
            String invoicePrefix = null;
            if (appProvider.isEntreprise()) {
                invoicePrefix = "R_PRO_";
            } else {
                invoicePrefix = "R_PART_";
            }
            entity.setInvoicePrefix(invoicePrefix + entity.getExternalRef2());
        } else {
            entity.setInvoicePrefix(null);
    }
    }

    public void processValueChange(ValueChangeEvent value) {
        if (value != null) {
            if (value.getNewValue() instanceof String) {
                entity.setExternalRef2((String) value.getNewValue());
                setInvoicePrefix();
            }

        }
    }

    public void populateAccounts(CustomerAccount customerAccount) {
        entity.setCustomerAccount(customerAccount);

        if (customerAccount != null && appProvider.isLevelDuplication()) {

            entity.setCode(customerAccount.getCode());
            entity.setDescription(customerAccount.getDescription());
            if (customerAccount.getContactInformation() != null) {
                entity.getContactInformationNullSafe().setEmail(customerAccount.getContactInformation().getEmail());
            }
            entity.setAddress(customerAccount.getAddress());
            entity.setExternalRef1(customerAccount.getExternalRef1());
            entity.setExternalRef2(customerAccount.getExternalRef2());
            entity.setProviderContact(customerAccount.getProviderContact());
            entity.setName(customerAccount.getName());
            entity.setPrimaryContact(customerAccount.getPrimaryContact());
        }
    }

    public void setCustomerAccountId(Long customerAccountId) {
        this.customerAccountId = customerAccountId;
    }

    public Long getCustomerAccountId() {
        return customerAccountId;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("customerAccount", "customerAccount.customer");
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("customerAccount", "customerAccount.billingAccounts", "billingCycle");
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

    public Date getExceptionalInvoicingDate() {
        return exceptionalInvoicingDate;
    }

    public void setExceptionalInvoicingDate(Date exceptionalInvoicingDate) {
        this.exceptionalInvoicingDate = exceptionalInvoicingDate;
    }

    public Date getExceptionalLastTransactionDate() {
        return exceptionalLastTransactionDate;
    }

    public void setExceptionalLastTransactionDate(Date exceptionalLastTransactionDate) {
        this.exceptionalLastTransactionDate = exceptionalLastTransactionDate;
    }

    public DualListModel<DiscountPlan> getDiscountPlanDM() {
        return discountPlanDM;
    }

    public void setDiscountPlanDM(DualListModel<DiscountPlan> discountPlanDM) {
        this.discountPlanDM = discountPlanDM;
    }
}