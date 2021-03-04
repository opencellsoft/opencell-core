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

import static org.meveo.commons.utils.NumberUtils.round;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ChargeTemplate.ChargeMainTypeEnum;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAggregateHandler;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;
import org.omnifaces.cdi.Param;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 *
 * @author Mohammed Amine Tazi
 * @lastModifiedVersion 7.0
 */
@Named
@ViewScoped
public class CreationDetailedInvoiceBean extends CustomFieldBean<Invoice> {

    private static final long serialVersionUID = 1L;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private TaxMappingService taxMappingService;
    
    @Inject 
    private AccountingCodeService accountingCodeService;
    
    @Inject
    SellerService sellerService;
    
    private Invoice invoiceToAdd;
    private Invoice selectedInvoice;
    private InvoiceSubCategory selectedInvoiceSubCategory;
    private ServiceTemplate selectedServiceTemplate;
    private AccountingCode selectedAccountingCode;
    private TaxClass selectedTaxClass;
    private BigDecimal quantity;
    private BigDecimal unitAmountWithoutTax;
    private BigDecimal unitAmountWithTax;
    private String description;
    private String parameter1;
    private String parameter2;
    private String parameter3;
    private String orderNumber;
    private Date usageDate = new Date();
    private RatedTransaction selectedRatedTransaction;
    private List<SelectItem> invoiceCategoriesGUI;
    private ChargeTemplate selectedCharge;

    private boolean includeBalance;

    private Order order;

    @Inject
    private InvoiceAggregateHandler aggregateHandler;
    private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregates = new ArrayList<>();

    @Inject
    @Param
    private String mode;

    @Inject
    @Param
    private Long linkedInvoiceIdParam;

    private List<CategoryInvoiceAgregate> categoryInvoiceAggregates = new ArrayList<>();
    private CategoryInvoiceAgregate selectedCategoryInvoiceAgregate;
    private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregateDetaild;
    private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;
    private boolean detailled = false;
    private Long rootInvoiceId = null;
    private Invoice rootInvoice;
    private boolean rtxHasImported = false;
    private Date startDate;
    private Date endDate;
    private boolean draftGenerated = false;
    private Invoice invoiceCopy = null;
    private Date rtStartDate;
    private Date rtEndDate;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CreationDetailedInvoiceBean() {
        super(Invoice.class);
        selectedServiceTemplate = new ServiceTemplate();
        selectedAccountingCode = new AccountingCode();
        selectedTaxClass = new TaxClass();
    }

    @Override
    public Invoice initEntity() {
        aggregateHandler.reset();
        entity = super.initEntity();
        entity.setDueDate(DateUtils.addMonthsToDate(new Date(), 1));
        entity.setInvoiceDate(new Date());

        if (entity.isTransient()) {
            if (mode != null) {
                setDetailled("detailed".equals(mode));
            }
            if (linkedInvoiceIdParam != null) {
                rootInvoiceId = linkedInvoiceIdParam;
                rootInvoice = invoiceService.findById(rootInvoiceId);
                entity.setBillingAccount(rootInvoice.getBillingAccount());
                entity.getLinkedInvoices().add(rootInvoice);
                try {
                    entity.setInvoiceType(invoiceTypeService.getDefaultAdjustement());
                } catch (BusinessException e) {
                    log.error("Cant get DefaultAdjustement Type:", e);
                }
            }
        }
        entity.setAmountWithoutTax(BigDecimal.ZERO);
        entity.setAmountWithTax(BigDecimal.ZERO);
        entity.setAmountTax(BigDecimal.ZERO);
        entity.setNetToPay(BigDecimal.ZERO);
        return entity;
    }

    /**
     * 
     * @return is detailed
     */
    public boolean isDetailed() {
        return detailled;
    }

    /**
     * @param detailled the detailled to set
     */
    public void setDetailled(boolean detailled) {
        this.detailled = detailled;
    }

    @Override
    protected IPersistenceService<Invoice> getPersistenceService() {
        return invoiceService;
    }

    public void onInvoiceSelect(SelectEvent event) {
        invoiceToAdd = (Invoice) event.getObject();
        if (invoiceToAdd != null && !entity.getLinkedInvoices().contains(invoiceToAdd)) {
            entity.getLinkedInvoices().add(invoiceToAdd);
        }
    }

    @ActionMethod
    public void deleteLinkedInvoice() throws BusinessException {
        entity.getLinkedInvoices().remove(selectedInvoice);
        selectedInvoice = null;

    }

    @ActionMethod
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteAllLinkedInvoice() throws BusinessException {
        entity.setLinkedInvoices(new HashSet());
        selectedInvoice = null;

    }

    public List<RatedTransaction> getRatedTransactions(SubCategoryInvoiceAgregate subCat) {
        if (subCat == null) {
            return null;
        }
        return subCat.getRatedtransactionsToAssociate();
    }

    @ActionMethod
    public void addDetailInvoiceLine() throws BusinessException {
        addDetailedInvoiceLines(selectedInvoiceSubCategory);
    }

    private void addDetailedInvoiceLines(InvoiceSubCategory selectInvoiceSubCat) {

        if (entity.getBillingAccount() == null) {
            messages.error("BillingAccount is required");
            return;
        }
        if (selectInvoiceSubCat == null) {
            messages.error("Invoice sub category is required.");
            return;
        }
        if (StringUtils.isBlank(description)) {
            messages.error("Description is required.");
            return;
        }
        if (StringUtils.isBlank(quantity)) {
            messages.error("Quantity is required.");
            return;
        }
        if (StringUtils.isBlank(selectedCharge)) {
            messages.error("Charge is required.");
            return;
        }
        if (StringUtils.isBlank(usageDate)) {
            messages.error("UsageDate is required.");
            return;
        }
        if (appProvider.isEntreprise()) {
            if (StringUtils.isBlank(unitAmountWithoutTax)) {
                messages.error("UnitAmountWithoutTax is required.");
                return;
            }

        } else {
            if (StringUtils.isBlank(unitAmountWithTax)) {
                messages.error("UnitAmountWithTax is required.");
                return;
            }
        }

        selectInvoiceSubCat = invoiceSubCategoryService.retrieveIfNotManaged(selectInvoiceSubCat);

        UserAccount ua = getFreshUA();

        Seller seller = entity.getSeller();
        if (seller == null) {
            seller = ua.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }
        seller=sellerService.refreshOrRetrieve(seller);
        TaxInfo taxInfo = taxMappingService.determineTax(selectedTaxClass, seller, entity.getBillingAccount(), ua, entity.getInvoiceDate(), true, false);

        // AKK check what happens with tax
        RatedTransaction ratedTransaction = new RatedTransaction(usageDate, unitAmountWithoutTax, unitAmountWithTax, null, quantity, null, null, null, RatedTransactionStatusEnum.BILLED, ua.getWallet(),
            ua.getBillingAccount(), ua, selectInvoiceSubCat, parameter1, parameter2, parameter3, null, orderNumber, null, null, null, null, null, null, selectedCharge.getCode(), description, rtStartDate, rtEndDate,
            seller, taxInfo.tax, taxInfo.tax.getPercent(), null, taxInfo.taxClass, null, null);

        ratedTransaction.setInvoice(entity);
        if(selectedAccountingCode != null) {
            ratedTransaction.setAccountingCode(selectedAccountingCode);
        }
        
        if(selectedAccountingCode != null && entity.getExternalRef() == null && orderNumber != null) {
            entity.setExternalRef(orderNumber);
        }

        aggregateHandler.addRT(entity.getInvoiceDate(), ratedTransaction);
        updateAmountsAndLines();

    }

    /**
     * BillingAccount selected. Update seller information if necessary.
     *
     * @param event billingAccount select event
     */
    public void onBillingAccountSet(SelectEvent event) {
        Object object = event.getObject();
        BillingAccount billingAccount = (BillingAccount) object;

        CustomerAccount ca = billingAccount.getCustomerAccount();
        if (ca != null) {
            Customer customer = ca.getCustomer();
            if (customer != null) {
                Seller seller = customer.getSeller();
                if (seller != null) {
                    entity.setSeller(seller);
                }
            }
        }
    }
    
    /**
     * Recompute agregates
     * 
     * @throws BusinessException General business exception
     */
    public void updateAmountsAndLines() throws BusinessException {

        BillingAccount billingAccount = billingAccountService.retrieveIfNotManaged(entity.getBillingAccount());

        subCategoryInvoiceAggregates = new ArrayList<SubCategoryInvoiceAgregate>(aggregateHandler.getSubCatInvAgregateMap().values());
        categoryInvoiceAggregates = new ArrayList<CategoryInvoiceAgregate>(aggregateHandler.getCatInvAgregateMap().values());

        entity.setAmountWithoutTax(round(aggregateHandler.getInvoiceAmountWithoutTax(), appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode()));
        entity.setAmountTax(round(aggregateHandler.getInvoiceAmountTax(), appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode()));
        entity.setAmountWithTax(round(aggregateHandler.getInvoiceAmountWithTax(), appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode()));

        BigDecimal netToPay = entity.getAmountWithTax();
        if (appProvider != null && !appProvider.isEntreprise() && isIncludeBalance()) {
            BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, billingAccount.getCustomerAccount().getCode(), entity.getDueDate());
            if (balance == null) {
                throw new BusinessException("account balance calculation failed");
            }
            netToPay = entity.getAmountWithTax().add(round(balance, appProvider.getInvoiceRounding(), appProvider.getInvoiceRoundingMode()));
        }
        entity.setNetToPay(netToPay);
    }

    /**
     * Called when a line is deleted from the dataList detailInvoice
     */
    @ActionMethod
    public void deleteRatedTransactionLine() {

        aggregateHandler.removeRT(selectedRatedTransaction);
        updateAmountsAndLines();
    }

    private void downloadFile(String fileName) {
        log.info("Requested to download file {}", fileName);

        File file = new File(fileName);

        OutputStream out = null;
        InputStream fin = null;
        try {
            HttpServletResponse res = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            res.setContentType("application/force-download");
            res.setContentLength((int) file.length());
            res.addHeader("Content-disposition", "attachment;filename=\"" + file.getName() + "\"");

            out = res.getOutputStream();
            fin = new FileInputStream(file);

            byte[] buf = new byte[1024];
            int sig = 0;
            while ((sig = fin.read(buf, 0, 1024)) != -1) {
                out.write(buf, 0, sig);
            }
            fin.close();
            out.flush();
            out.close();
            facesContext.responseComplete();
            log.info("File made available for download");
        } catch (Exception e) {
            log.error("Error: {}, when dowload file: {}", e.getMessage(), file.getAbsolutePath());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("Error", e);
                }
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    log.error("Error", e);
                }
            }
        }
    }

    @ActionMethod
    public void deleteLinkedInvoiceCategoryDetaild() {

        for (int i = 0; i < selectedSubCategoryInvoiceAgregateDetaild.getRatedtransactionsToAssociate().size(); i++) {
            aggregateHandler.removeRT(selectedSubCategoryInvoiceAgregateDetaild.getRatedtransactionsToAssociate().get(i));
        }
    }

    /**
     * Called when quantity or unitAmout are changed in the dataList detailInvoice
     * 
     * @param ratedTx rated transacion.
     */
    @ActionMethod
    public void reComputeAmounts(RatedTransaction ratedTx) {
        aggregateHandler.reset();
        for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregates) {
            for (RatedTransaction rt : subcat.getRatedtransactionsToAssociate()) {
                synchroniseAmounts(rt);
                aggregateHandler.addRT(entity.getInvoiceDate(), rt);
            }
        }
        updateAmountsAndLines();
    }

    private void synchroniseAmounts(RatedTransaction rt) {
        BigDecimal uawot = rt.getUnitAmountWithoutTax() != null ? rt.getUnitAmountWithoutTax() : BigDecimal.ZERO;
        BigDecimal rtAwot = rt.getAmountWithoutTax() != null ? rt.getAmountWithoutTax() : BigDecimal.ZERO;
        BigDecimal newAwot = uawot.multiply(rt.getQuantity());
        if (newAwot.compareTo(rtAwot) != 0) {
            BigDecimal amountTax = NumberUtils.computeTax(newAwot, rt.getTaxPercent(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
            BigDecimal newAwt = newAwot.add(amountTax);
            BigDecimal unitAmountTax = NumberUtils.computeTax(uawot, rt.getTaxPercent(), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
            BigDecimal uawt = uawot.add(unitAmountTax);

            rt.setUnitAmountTax(unitAmountTax);
            rt.setUnitAmountWithoutTax(uawot);
            rt.setUnitAmountWithTax(uawt);

            rt.setAmountTax(amountTax);
            rt.setAmountWithoutTax(newAwot);
            rt.setAmountWithTax(newAwt);
        }
    }

    /**
     * Include original opened ratedTransaction
     * 
     */
    @ActionMethod
    public void importOpenedRT() {

        if (isRtxHasImported()) {
            messages.error("Rtx already imported");
            return;
        }
        if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
            messages.error("BillingAccount is required.");
            return;
        }
        if (entity.getInvoiceType().getCode().equals(invoiceTypeService.getCommercialCode())) {
            List<RatedTransaction> openedRT = ratedTransactionService.openRTbySubCat(getFreshUA().getWallet(), null, getStartDate(), getEndDate());
            if (openedRT != null && openedRT.isEmpty()) {
                messages.error("No opened RatedTransactions");
                return;
            }
            for (RatedTransaction ratedTransaction : openedRT) {
                aggregateHandler.addRT(entity.getInvoiceDate(), ratedTransaction);
            }
            setRtxHasImported(true);
            updateAmountsAndLines();
        }
    }

    public boolean isDraftGenerated() {
        return draftGenerated;
    }

    /**
     * Allow generating draft invoice
     * 
     * @throws BusinessException General Business Exception
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    @ActionMethod
    public void generateDraftInvoice() throws BusinessException {

        try {

            invoiceCopy = (Invoice) BeanUtils.cloneBean(entity);
            invoiceCopy.setBillingAccount(billingAccountService.retrieveIfNotManaged(entity.getBillingAccount()));
            invoiceCopy.setLinkedInvoices(invoiceService.retrieveIfNotManaged(entity.getLinkedInvoices()));
            BillingAccount billingAccount = invoiceCopy.getBillingAccount();
            Customer customer = billingAccount.getCustomerAccount().getCustomer();
            if (invoiceCopy.getSeller() == null) {
                invoiceCopy.setSeller(customer.getSeller());
            }
            invoiceCopy.setInvoiceAgregates(new ArrayList<InvoiceAgregate>());

            Map<String, TaxInvoiceAgregate> taxInvAgregateMapCopy = new HashMap<String, TaxInvoiceAgregate>();
            for (Entry<String, TaxInvoiceAgregate> taxInvAggr : aggregateHandler.getTaxInvAgregateMap().entrySet()) {
                TaxInvoiceAgregate taxInvAggrCopy = new TaxInvoiceAgregate();
                BeanUtils.copyProperties(taxInvAggrCopy, taxInvAggr.getValue());
                taxInvAggrCopy.setId(null);
                taxInvAggrCopy.updateAudit(currentUser);
                taxInvAggrCopy.setInvoice(invoiceCopy);
                taxInvAgregateMapCopy.put(taxInvAggr.getKey(), taxInvAggrCopy);
            }

            List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregatesCopy = new ArrayList<SubCategoryInvoiceAgregate>();
            List<RatedTransaction> ratedTransactionCopy = new ArrayList<RatedTransaction>();
            for (SubCategoryInvoiceAgregate subCatInvAggr : subCategoryInvoiceAggregates) {

                CategoryInvoiceAgregate catInvAggrCopy = new CategoryInvoiceAgregate();
                BeanUtils.copyProperties(catInvAggrCopy, subCatInvAggr.getCategoryInvoiceAgregate());
                catInvAggrCopy.setId(null);
                catInvAggrCopy.updateAudit(currentUser);
                catInvAggrCopy.setInvoice(invoiceCopy);
                catInvAggrCopy.setSubCategoryInvoiceAgregates(new HashSet<SubCategoryInvoiceAgregate>());

                SubCategoryInvoiceAgregate subCatInvAggrCopy = new SubCategoryInvoiceAgregate();
                BeanUtils.copyProperties(subCatInvAggrCopy, subCatInvAggr);
                subCatInvAggrCopy.setId(null);
                subCatInvAggrCopy.updateAudit(currentUser);
                subCatInvAggrCopy.setInvoice(invoiceCopy);
                subCatInvAggrCopy.setCategoryInvoiceAgregate(catInvAggrCopy);
                subCatInvAggrCopy.setRatedtransactionsToAssociate(new ArrayList<RatedTransaction>());
                subCategoryInvoiceAggregatesCopy.add(subCatInvAggrCopy);

                for (RatedTransaction rt : subCatInvAggr.getRatedtransactionsToAssociate()) {
                    RatedTransaction rtCopy = new RatedTransaction();
                    BeanUtils.copyProperties(rtCopy, rt);
                    rtCopy.setId(null);
                    rtCopy.setBillingAccount(billingAccount);
                    rtCopy.changeStatus(RatedTransactionStatusEnum.BILLED);
                    rtCopy.setInvoice(invoiceCopy);
                    rtCopy.setInvoiceAgregateF(subCatInvAggrCopy);

                    ratedTransactionCopy.add(rtCopy);
                }
            }

            getPersistenceService().create(invoiceCopy);

            for (RatedTransaction rtCopy : ratedTransactionCopy) {
                ratedTransactionService.create(rtCopy);
            }

            invoiceCopy = invoiceService.generateXmlAndPdfInvoice(invoiceCopy, true);
            draftGenerated = true;

            for (RatedTransaction rtCopy : ratedTransactionCopy) {
                ratedTransactionService.remove(rtCopy);
            }

            invoiceService.cancelInvoice(invoiceCopy);

        } catch (Exception e) {
            log.error("Error generating xml / pdf invoice", e);
            messages.error("Error generating xml / pdf invoice=" + e.getMessage());
        }
    }

    @ActionMethod
    public void downloadXmlInvoice() {
        String fileName = invoiceService.getFullXmlFilePath(invoiceCopy, false);
        downloadFile(fileName);
    }

    @ActionMethod
    public void downloadPdfInvoice() {
        if (invoiceCopy.getPdfFilename() == null) {
            return;
        }
        String fileName = invoiceService.getFullPdfFilePath(invoiceCopy, false);
        downloadFile(fileName);
    }

    @Override

    @ActionMethod
    public String saveOrUpdate(boolean killConversation) {
        BillingAccount billingAccount = getFreshBA();
        Customer customer = billingAccount.getCustomerAccount().getCustomer();
        entity.setBillingAccount(billingAccount);
        entity.setDetailedInvoice(isDetailed());
        if (entity.getSeller() == null) {
            entity.setSeller(customer.getSeller());
        }

        for (Entry<String, TaxInvoiceAgregate> entry : aggregateHandler.getTaxInvAgregateMap().entrySet()) {
            TaxInvoiceAgregate taxInvAgr = entry.getValue();
            taxInvAgr.setInvoice(entity);
            taxInvAgr.updateAudit(currentUser);
        }

        for (Entry<String, CategoryInvoiceAgregate> entry : aggregateHandler.getCatInvAgregateMap().entrySet()) {
            CategoryInvoiceAgregate catInvAgr = entry.getValue();
            catInvAgr.setInvoice(entity);
            catInvAgr.updateAudit(currentUser);
            catInvAgr.setSubCategoryInvoiceAgregates(new HashSet<SubCategoryInvoiceAgregate>());
        }

        List<RatedTransaction> rts = new ArrayList<RatedTransaction>();

        for (SubCategoryInvoiceAgregate subCatInvAggr : subCategoryInvoiceAggregates) {
            subCatInvAggr.setInvoice(entity);
            subCatInvAggr.updateAudit(currentUser);

            for (RatedTransaction rt : subCatInvAggr.getRatedtransactionsToAssociate()) {
                rt.setInvoice(entity);
                rt.setInvoiceAgregateF(subCatInvAggr);
                rt.changeStatus(RatedTransactionStatusEnum.BILLED);
                rts.add(rt);
            }
        }

        entity.setLinkedInvoices(invoiceService.retrieveIfNotManaged(entity.getLinkedInvoices()));

        super.saveOrUpdate(false);

        for (RatedTransaction rt : rts) {
        	if(rt.getId() == null) {
        		ratedTransactionService.create(rt);
        	}else {
        		ratedTransactionService.update(rt);
        	}
        }

        invoiceService.postCreate(entity);

        entity = serviceSingleton.assignInvoiceNumberVirtual(entity);

        try {
            entity = invoiceService.generateXmlAndPdfInvoice(entity, true);
        } catch (Exception e) {
            log.error("Failed to create an XML and PDF invoice", e);
            messages.error("Error generating xml / pdf invoice=" + e.getMessage());
        }

        return getListViewName();
    }

    /**
     * Include a copy from linkedIncoice's RatedTransaction
     * 
     */
    @ActionMethod
    public void importFromLinkedInvoices() {

        if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
            messages.error("BillingAccount is required.");
            return;
        }

        if (entity.getLinkedInvoices() != null && entity.getLinkedInvoices().size() <= 0) {
            messages.info(new BundleKey("messages", "message.invoice.addAggregate.linked.null"));
            return;
        }

        UserAccount ua = getFreshUA();

        for (Invoice invoice : entity.getLinkedInvoices()) {
            invoice = invoiceService.findById(invoice.getId());
            if (entity.getLinkedInvoices().size() == 1) {
                entity.setCfValues(invoice.getCfValues());
                customFieldDataEntryBean.refreshFieldsAndActions(entity);
            }

            List<RatedTransaction> ratedTransactions = ratedTransactionService.getRatedTransactionsByInvoice(invoice, true);

            if (isDetailed()) {

                for (RatedTransaction rt : ratedTransactions) {
                    if (!DateUtils.isWithinDate(rt.getUsageDate(), getStartDate(), getEndDate())) {
                        continue;
                    }

                    TaxInfo taxInfo = null;
                    if (rt.getTaxClass() != null) {
                        taxInfo = taxMappingService.determineTax(rt.getTaxClass(), sellerService.refreshOrRetrieve(rt.getSeller()), entity.getBillingAccount(), ua, entity.getInvoiceDate(), true, false);
                    }

                    RatedTransaction newRT = new RatedTransaction(rt.getUsageDate(), rt.getUnitAmountWithoutTax(), rt.getUnitAmountWithTax(), rt.getUnitAmountTax(), rt.getQuantity(), rt.getAmountWithoutTax(),
                        rt.getAmountWithTax(), rt.getAmountTax(), RatedTransactionStatusEnum.BILLED, ua.getWallet(), entity.getBillingAccount(), ua, rt.getInvoiceSubCategory(), rt.getParameter1(), rt.getParameter2(),
                        rt.getParameter3(), null, rt.getOrderNumber(), null, rt.getUnityDescription(), rt.getRatingUnitDescription(), null, null, null, rt.getCode(), rt.getDescription(), rt.getStartDate(),
                        rt.getEndDate(), rt.getSeller(), taxInfo != null ? taxInfo.tax : rt.getTax(), taxInfo != null ? taxInfo.tax.getPercent() : rt.getTax().getPercent(), null,
                        taxInfo != null ? taxInfo.taxClass : null, null, rt.getType());

                    newRT.setInvoice(entity);

                    aggregateHandler.addRT(entity.getInvoiceDate(), newRT);
                }
            } else {
                for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
                    if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                        aggregateHandler.addInvoiceSubCategory(((SubCategoryInvoiceAgregate) invoiceAgregate).getInvoiceSubCategory(), getFreshUA(), invoiceAgregate.getDescription(),
                            invoiceAgregate.getAmountWithoutTax(), invoiceAgregate.getAmountWithTax());
                    }
                }

                for (RatedTransaction rt : ratedTransactions) {
                    if (rt.getWallet() == null) {
                        aggregateHandler.addRT(entity.getInvoiceDate(), rt);
                    }
                }

            }

        }

        updateAmountsAndLines();
    }

    /**
     * 
     * @return InvoiceCatSubCats list
     */
    public List<SelectItem> getInvoiceCatSubCats() {
        if (invoiceCategoriesGUI != null) {
            return invoiceCategoriesGUI;
        }

        invoiceCategoriesGUI = new ArrayList<>();

        List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list();
        for (InvoiceCategory ic : invoiceCategories) {
            SelectItemGroup selectItemGroup = new SelectItemGroup(ic.getCode());
            List<SelectItem> subCats = new ArrayList<>();
            for (InvoiceSubCategory invoiceSubCategory : ic.getInvoiceSubCategories()) {
                subCats.add(new SelectItem(invoiceSubCategory, invoiceSubCategory.getCode()));
            }
            selectItemGroup.setSelectItems(subCats.toArray(new SelectItem[subCats.size()]));
            invoiceCategoriesGUI.add(selectItemGroup);
        }

        return invoiceCategoriesGUI;
    }

    /**
     * 
     * @return Charges list
     */
    public List<ChargeTemplate> getCharges() {
        if(selectedServiceTemplate == null) {
            return Collections.emptyList();
        }
        List<ChargeTemplate> chargeTemplates = new ArrayList<>();
        selectedServiceTemplate.getServiceSubscriptionCharges().stream().forEach((charge)-> chargeTemplates.add(charge.getChargeTemplate()));
        selectedServiceTemplate.getServiceTerminationCharges().stream().forEach((charge)-> chargeTemplates.add(charge.getChargeTemplate()));
        selectedServiceTemplate.getServiceRecurringCharges().stream().forEach((charge)-> chargeTemplates.add(charge.getChargeTemplate()));
        selectedServiceTemplate.getServiceUsageCharges().stream().forEach((charge)-> chargeTemplates.add(charge.getChargeTemplate()));
        for(ChargeTemplate chargeTemplate : chargeTemplates) {
            selectedCharge = chargeTemplate;
            if(ChargeMainTypeEnum.ONESHOT.equals(chargeTemplate.getChargeMainType())) {
                break;
            }
        }
        return chargeTemplates;         
    }

    @ActionMethod
    public void setIncludeBalance(boolean includeBalance) {
        this.includeBalance = includeBalance;

        updateAmountsAndLines();
    }

    /**
     * @return the subCategoryInvoiceAggregates
     */
    public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAggregates() {
        return subCategoryInvoiceAggregates;
    }

    /**
     * @param subCategoryInvoiceAggregates the subCategoryInvoiceAggregates to set
     */
    public void setSubCategoryInvoiceAggregates(List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregates) {
        this.subCategoryInvoiceAggregates = subCategoryInvoiceAggregates;
    }

    private BillingAccount getFreshBA() throws BusinessException {

        if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
            throw new BusinessException("BillingAccount is required.");
        }
        entity.setBillingAccount(billingAccountService.retrieveIfNotManaged(entity.getBillingAccount()));
        return entity.getBillingAccount();
    }

    private UserAccount getFreshUA() throws BusinessException {
        BillingAccount ba = getFreshBA();
        if (ba.getUsersAccounts() == null || ba.getUsersAccounts().isEmpty()) {
            throw new BusinessException("BillingAccount with code=" + getFreshBA().getCode() + " has no userAccount.");
        }
        return userAccountService.retrieveIfNotManaged(ba.getUsersAccounts().get(0));
    }

    public List<CategoryInvoiceAgregate> getCategoryInvoiceAggregates() {
        return categoryInvoiceAggregates;
    }

    /**
     * delete a cat invoice agregate
     * 
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public void deleteLinkedInvoiceCategory() {
        for (SubCategoryInvoiceAgregate subCat : selectedCategoryInvoiceAgregate.getSubCategoryInvoiceAgregates()) {
            aggregateHandler.removeInvoiceSubCategory(subCat.getInvoiceSubCategory(), getFreshUA(), subCat.getAmountWithoutTax(), subCat.getAmountWithTax());
        }

        updateAmountsAndLines();
    }

    /**
     * delete a sub cat invoice aggregate
     * 
     * @throws BusinessException General business exception
     */
    @ActionMethod
    public void deleteLinkedInvoiceSubCategory() {
        aggregateHandler.removeInvoiceSubCategory(selectedSubCategoryInvoiceAgregate.getInvoiceSubCategory(), getFreshUA(), selectedSubCategoryInvoiceAgregate.getAmountWithoutTax(),
            selectedSubCategoryInvoiceAgregate.getAmountWithTax());
        updateAmountsAndLines();

    }

    public CategoryInvoiceAgregate getSelectedCategoryInvoiceAgregate() {
        return selectedCategoryInvoiceAgregate;
    }

    public void setSelectedCategoryInvoiceAgregate(CategoryInvoiceAgregate selectedCategoryInvoiceAgregate) {
        this.selectedCategoryInvoiceAgregate = selectedCategoryInvoiceAgregate;
    }

    public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAggregates(CategoryInvoiceAgregate cat) {
        if (cat == null)
            return null;
        List<SubCategoryInvoiceAgregate> result = new ArrayList<>();
        if (cat.getSubCategoryInvoiceAgregates() == null)
            return result;

        for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
            result.add(subCat);
        }

        return result;
    }

    public SubCategoryInvoiceAgregate getSelectedSubCategoryInvoiceAgregate() {
        return selectedSubCategoryInvoiceAgregate;
    }

    public void setSelectedSubCategoryInvoiceAgregate(SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate) {
        this.selectedSubCategoryInvoiceAgregate = selectedSubCategoryInvoiceAgregate;
    }

    @ActionMethod
    public void addAggregatedLine() throws BusinessException {

        if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
            messages.error("BillingAccount is required.");
            return;
        }

        if (StringUtils.isBlank(description)) {
            messages.error("Description is required.");
            return;
        }

        if (appProvider.isEntreprise()) {
            if (amountWithoutTax == null || selectedInvoiceSubCategory == null) {
                messages.error("AmountWithoutTax and InvoiceSubCategory is required.");
                return;
            }

        } else {
            if (amountWithTax == null || selectedInvoiceSubCategory == null) {
                messages.error("AmountWithTax and InvoiceSubCategory is required.");
                return;
            }
        }

        selectedInvoiceSubCategory = invoiceSubCategoryService.retrieveIfNotManaged(selectedInvoiceSubCategory);

        aggregateHandler.addInvoiceSubCategory(selectedInvoiceSubCategory, getFreshUA(), description, amountWithoutTax, amountWithTax);
        updateAmountsAndLines();
    }

    /**
     * Called whene quantity or unitAmout are changed in the dataList detailInvoice
     * 
     * @param invSubCat sub category invoice
     * @throws BusinessException business exception.
     */
    @ActionMethod
    public void reComputeAmounts(SubCategoryInvoiceAgregate invSubCat) {
        aggregateHandler.reset();
        for (CategoryInvoiceAgregate cat : categoryInvoiceAggregates) {
            for (SubCategoryInvoiceAgregate subCate : cat.getSubCategoryInvoiceAgregates()) {
                InvoiceSubCategory tmp = subCate.getInvoiceSubCategory();
                aggregateHandler.addInvoiceSubCategory(tmp, getFreshUA(), subCate.getDescription(), subCate.getAmountWithoutTax(), subCate.getAmountWithTax());
            }
        }
        updateAmountsAndLines();
    }

    /**
     * 
     * @return InvoicesByTypeAndBA
     * @throws BusinessException General business exception
     */
    public LazyDataModel<Invoice> getInvoicesByTypeAndBA() throws BusinessException {
        if (getEntity().getBillingAccount() != null && !entity.getBillingAccount().isTransient()) {
            BillingAccount ba = billingAccountService.retrieveIfNotManaged(entity.getBillingAccount());
            filters.put("billingAccount", ba);
        }
        if (entity.getInvoiceType() != null) {
            InvoiceType selInvoiceType = invoiceTypeService.refreshOrRetrieve(entity.getInvoiceType());
            List<InvoiceType> invoiceTypes = selInvoiceType.getAppliesTo();
            if (invoiceTypes != null && invoiceTypes.size() > 0) {
                StringBuilder invoiceTypeIds = new StringBuilder();
                for (InvoiceType invoiceType : invoiceTypes) {
                    invoiceTypeIds.append(invoiceType.getId() + ",");
                }
                invoiceTypeIds.deleteCharAt(invoiceTypeIds.length() - 1);
                filters.put("inList invoiceType.id", invoiceTypeIds);
            }
        }

        return getLazyDataModel();
    }

    @Override
    public String getBackView() {
        if (rootInvoiceId == null) {
            return super.getBackView();
        }
        return "/pages/billing/invoices/invoiceDetail.xhtml?objectId=" + rootInvoiceId + "&cid=" + conversation.getId() + "&edit=true&faces-redirect=true";
    }

    @Override
    public String getBackViewSave() {
        return getBackView();
    }

    public boolean isCanAddLinkedInvoice() {
        return true;// entity.getBillingAccount() != null && entity.getInvoiceType() != null;
    }

    /*
     * ################################################################################################### # Setters and Getters #
     * ###################################################################################################
     */


    public void handleSelectedCharge() {
        if (selectedCharge != null) {
            description = selectedCharge.getDescriptionOrCode();
        }
    }

    public void handleSelectedServiceTemplate() {
        if (selectedServiceTemplate != null) {
            description = selectedServiceTemplate.getDescriptionOrCode();
        }
    }

    public String getPageMode() {
        if (mode != null && !StringUtils.isBlank(mode)) {
            return mode;
        }

        return "agregated";
    }

    /**
     * @return the rootInvoiceId
     */
    public Long getRootInvoiceId() {
        return rootInvoiceId;
    }

    /**
     * @param rootInvoiceId the rootInvoiceId to set
     */
    public void setRootInvoiceId(Long rootInvoiceId) {
        this.rootInvoiceId = rootInvoiceId;
    }

    /**
     * @return the selectedSubCategoryInvoiceAgregateDetaild
     */
    public SubCategoryInvoiceAgregate getSelectedSubCategoryInvoiceAgregateDetaild() {
        return selectedSubCategoryInvoiceAgregateDetaild;
    }

    /**
     * @param selectedSubCategoryInvoiceAgregateDetaild the selectedSubCategoryInvoiceAgregateDetaild to set
     */
    public void setSelectedSubCategoryInvoiceAgregateDetaild(SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregateDetaild) {
        this.selectedSubCategoryInvoiceAgregateDetaild = selectedSubCategoryInvoiceAgregateDetaild;
    }

    /**
     * @return the usageDate
     */
    public Date getUsageDate() {
        return usageDate;
    }

    /**
     * @param usageDate the usageDate to set
     */
    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    /**
     * @return the selectedCharge
     */
    public ChargeTemplate getSelectedCharge() {
        return selectedCharge;
    }

    /**
     * @param selectedCharge the selectedCharge to set
     */
    public void setSelectedCharge(ChargeTemplate selectedCharge) {
        this.selectedCharge = selectedCharge;
    }

    /**
     * @return the amountWithoutTax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * @param amountWithoutTax the amountWithoutTax to set
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public Invoice getInvoiceToAdd() {
        return invoiceToAdd;
    }

    public void setInvoiceToAdd(Invoice invoiceToAdd) {
        if (invoiceToAdd != null && !entity.getLinkedInvoices().contains(invoiceToAdd)) {
            entity.getLinkedInvoices().add(invoiceToAdd);
        }
        this.invoiceToAdd = invoiceToAdd;
    }

    public Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(Invoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    public List<SelectItem> getInvoiceCategoriesGUI() {
        return invoiceCategoriesGUI;
    }

    public void setInvoiceCategoriesGUI(List<SelectItem> invoiceCategoriesGUI) {
        this.invoiceCategoriesGUI = invoiceCategoriesGUI;
    }

    public InvoiceSubCategory getSelectedInvoiceSubCategory() {
        return selectedInvoiceSubCategory;
    }

    public void setSelectedInvoiceSubCategory(InvoiceSubCategory selectedInvoiceSubCategory) {
        this.selectedInvoiceSubCategory = selectedInvoiceSubCategory;
    }

    public RatedTransaction getSelectedRatedTransaction() {
        return selectedRatedTransaction;
    }

    public void setSelectedRatedTransaction(RatedTransaction selectedRatedTransaction) {
        this.selectedRatedTransaction = selectedRatedTransaction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isIncludeBalance() {
        return includeBalance;
    }

    /**
     * @return the rtxHasImported
     */
    public boolean isRtxHasImported() {
        return rtxHasImported;
    }

    /**
     * @param rtxHasImported the rtxHasImported to set
     */
    public void setRtxHasImported(boolean rtxHasImported) {
        this.rtxHasImported = rtxHasImported;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getRtStartDate() {
        return rtStartDate;
    }

    public void setRtStartDate(Date rtStartDate) {
        this.rtStartDate = rtStartDate;
    }

    public Date getRtEndDate() {
        return rtEndDate;
    }

    public void setRtEndDate(Date rtEndDate) {
        this.rtEndDate = rtEndDate;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }
    
    public ServiceTemplate getSelectedServiceTemplate() {
        return selectedServiceTemplate;
    }
    
    public void setSelectedServiceTemplate(ServiceTemplate selectedServiceTemplate) {
        this.selectedServiceTemplate = selectedServiceTemplate;
        this.description = selectedServiceTemplate.getDescriptionOrCode();
    }
    
    public AccountingCodeService getAccountingCodeService() {
        return accountingCodeService;
    }
    
    public void setAccountingCodeService(AccountingCodeService accountingCodeService) {
        this.accountingCodeService = accountingCodeService;
    }
    
    public AccountingCode getSelectedAccountingCode() {
        return selectedAccountingCode;
    }
    
    public void setSelectedAccountingCode(AccountingCode selectedAccountingCode) {
        this.selectedAccountingCode = selectedAccountingCode;
    }
    
    public TaxClass getSelectedTaxClass() {
        return selectedTaxClass;
    }
    
    public void setSelectedTaxClass(TaxClass selectedTaxClass) {
        this.selectedTaxClass = selectedTaxClass;
    }
}