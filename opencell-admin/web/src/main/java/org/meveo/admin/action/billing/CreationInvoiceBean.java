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
package org.meveo.admin.action.billing;

import static org.meveo.commons.utils.NumberUtils.round;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.meveo.commons.utils.StringUtils;
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
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.order.Order;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAgregateHandler;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.Param;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@Named
@ViewScoped
public class CreationInvoiceBean extends CustomFieldBean<Invoice> {

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
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ChargeTemplateServiceAll chargeTemplateServiceAll;

    private Invoice invoiceToAdd;
    private Invoice selectedInvoice;
    private InvoiceSubCategory selectedInvoiceSubCategory;
    private BigDecimal quantity;
    private BigDecimal unitAmountWithoutTax;
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
    private InvoiceAgregateHandler agregateHandler;
    private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregates = new ArrayList<SubCategoryInvoiceAgregate>();

    @Inject
    @Param
    private String mode;

    @Inject
    @Param
    private Long linkedInvoiceIdParam;

    private List<CategoryInvoiceAgregate> categoryInvoiceAggregates = new ArrayList<CategoryInvoiceAgregate>();
    private CategoryInvoiceAgregate selectedCategoryInvoiceAgregate;
    private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregateDetaild;
    private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate;
    private BigDecimal amountWithoutTax;
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
    
    private Integer invoiceRounding =  appProvider.getInvoiceRounding(); 
    private RoundingModeEnum invoiceRoundingMode =  appProvider.getInvoiceRoundingMode(); 

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CreationInvoiceBean() {
        super(Invoice.class);
    }

    @Override
    public Invoice initEntity() {
        agregateHandler.reset();
        entity = super.initEntity();
        entity.setDueDate(new Date());
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

    public void deleteLinkedInvoice() throws BusinessException {
        entity.getLinkedInvoices().remove(selectedInvoice);
        selectedInvoice = null;

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteAllLinkedInvoice() throws BusinessException {
        entity.setLinkedInvoices(new HashSet());
        selectedInvoice = null;

    }

    public List<RatedTransaction> getRatedTransactions(SubCategoryInvoiceAgregate subCat) {
        if (subCat == null) {
            return null;
        }
        return subCat.getRatedtransactions();
    }

    public void addDetailInvoiceLine() throws BusinessException {
        addDetailedInvoiceLines(selectedInvoiceSubCategory);
    }

    private void addDetailedInvoiceLines(InvoiceSubCategory selectInvoiceSubCat) {
        try {
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
            if (StringUtils.isBlank(unitAmountWithoutTax)) {
                messages.error("UnitAmountWithoutTax is required.");
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

            selectInvoiceSubCat = invoiceSubCategoryService.retrieveIfNotManaged(selectInvoiceSubCat);

            RatedTransaction ratedTransaction = new RatedTransaction();
            ratedTransaction.setUsageDate(usageDate);
            ratedTransaction.setUnitAmountWithoutTax(unitAmountWithoutTax);
            ratedTransaction.setQuantity(quantity);
            ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
            ratedTransaction.setWallet(getFreshUA().getWallet());
            ratedTransaction.setBillingAccount(getFreshBA());
            ratedTransaction.setInvoiceSubCategory(selectInvoiceSubCat);
            ratedTransaction.setCode(selectedCharge.getCode());
            ratedTransaction.setDescription(description);
            ratedTransaction.setParameter1(parameter1);
            ratedTransaction.setParameter2(parameter2);
            ratedTransaction.setParameter3(parameter3);
            ratedTransaction.setStartDate(rtStartDate);
            ratedTransaction.setEndDate(rtEndDate);
            ratedTransaction.setOrderNumber(orderNumber);
            ratedTransaction.setInvoice(entity);
            ratedTransaction.setInvoiceSubCategory(selectInvoiceSubCat);
            ratedTransaction.setSeller(ratedTransaction.getBillingAccount().getCustomerAccount().getCustomer().getSeller());

            agregateHandler.addRT(ratedTransaction, selectInvoiceSubCat.getDescription(), getFreshUA());
            updateAmountsAndLines(getFreshBA());
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
        } catch (Exception e) {
            messages.error(e.getMessage());
            return;
        }

    }

    /**
     * Recompute agregates
     * 
     * @param billingAccount billing account
     * @throws BusinessException General business exception
     */
    public void updateAmountsAndLines(BillingAccount billingAccount) throws BusinessException {
        billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
        subCategoryInvoiceAggregates = new ArrayList<SubCategoryInvoiceAgregate>(agregateHandler.getSubCatInvAgregateMap().values());
        categoryInvoiceAggregates = new ArrayList<CategoryInvoiceAgregate>(agregateHandler.getCatInvAgregateMap().values());
       
        entity.setAmountWithoutTax( round(agregateHandler.getInvoiceAmountWithoutTax(), invoiceRounding, invoiceRoundingMode) );
        entity.setAmountTax( round(agregateHandler.getInvoiceAmountTax(), invoiceRounding, invoiceRoundingMode) );
        entity.setAmountWithTax( round(agregateHandler.getInvoiceAmountWithTax(), invoiceRounding, invoiceRoundingMode) );

        BigDecimal netToPay = entity.getAmountWithTax();
        if (appProvider != null && !appProvider.isEntreprise() && isIncludeBalance()) {
            BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, billingAccount.getCustomerAccount().getCode(), entity.getDueDate());
            if (balance == null) {
                throw new BusinessException("account balance calculation failed");
            }
            netToPay = entity.getAmountWithTax().add( round(balance, invoiceRounding, invoiceRoundingMode) );
        }
        entity.setNetToPay(netToPay);
    }

    /**
     * Called when a line is deleted from the dataList detailInvoice
     */
    public void deleteRatedTransactionLine() {
        try {
            agregateHandler.removeRT(selectedRatedTransaction, selectedRatedTransaction.getInvoiceSubCategory().getDescription(), getFreshUA());
            updateAmountsAndLines(getFreshBA());
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
        }
    }

    private void downloadFile(String fileName) {
        log.info("Requested to download file {}", fileName);

        File file = new File(fileName);

        OutputStream out = null;
        InputStream fin = null;
        try {
            javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
            HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
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
            context.responseComplete();
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

    public void deleteLinkedInvoiceCategoryDetaild() {
        try {
            for (int i = 0; i < selectedSubCategoryInvoiceAgregateDetaild.getRatedtransactions().size(); i++) {
                agregateHandler.removeRT(selectedSubCategoryInvoiceAgregateDetaild.getRatedtransactions().get(i),
                    selectedSubCategoryInvoiceAgregateDetaild.getRatedtransactions().get(i).getInvoiceSubCategory().getDescription(), getFreshUA());
                updateAmountsAndLines(getFreshBA());
            }
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
        }
    }

    /**
     * Called when quantity or unitAmout are changed in the dataList detailInvoice
     * 
     * @param ratedTx rated transacion.
     */
    public void reComputeAmountWithoutTax(RatedTransaction ratedTx) {
        try {
            agregateHandler.reset();
            for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregates) {
                for (RatedTransaction rt : subcat.getRatedtransactions()) {
                    rt.setAmountWithoutTax(null);
                    agregateHandler.addRT(rt, rt.getInvoiceSubCategory().getDescription(), getFreshUA());
                    updateAmountsAndLines(ratedTx.getBillingAccount());
                }
            }
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
        }
    }

    /**
     * Include original opened ratedTransaction
     * 
     */
    public void importOpenedRT() {
        try {
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
                    agregateHandler.addRT(ratedTransaction, ratedTransaction.getInvoiceSubCategory().getDescription(), getFreshUA());
                    updateAmountsAndLines(entity.getBillingAccount());
                }
                setRtxHasImported(true);

            }
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
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
            invoiceCopy.setInvoiceAgregates(new ArrayList<InvoiceAgregate>());
            getPersistenceService().create(invoiceCopy);
            
            invoiceService.commit();
            invoiceCopy = invoiceService.refreshOrRetrieve(invoiceCopy);
            
            Map<String, TaxInvoiceAgregate> taxInvAgregateMapCopy = new HashMap<String, TaxInvoiceAgregate>();
            for (Entry<String, TaxInvoiceAgregate> entry : agregateHandler.getTaxInvAgregateMap().entrySet()) {
                TaxInvoiceAgregate taxInvAgr = new TaxInvoiceAgregate();
                BeanUtils.copyProperties(taxInvAgr, entry.getValue());
                taxInvAgr.setId(null);
                taxInvAgr.setInvoice(invoiceCopy);
                invoiceAgregateService.create(taxInvAgr);
                taxInvAgregateMapCopy.put(entry.getKey(), taxInvAgr);
            }
            
            List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregatesCopy = new ArrayList<SubCategoryInvoiceAgregate>();
            List<RatedTransaction> ratedTransactionCopy = new ArrayList<RatedTransaction>();
            for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregates) {
                
                CategoryInvoiceAgregate catInvAggr = new CategoryInvoiceAgregate();
                BeanUtils.copyProperties(catInvAggr,  subcat.getCategoryInvoiceAgregate());        
                catInvAggr.setId(null);
                catInvAggr.setInvoice(invoiceCopy);
                catInvAggr.setSubCategoryInvoiceAgregates(new HashSet<SubCategoryInvoiceAgregate>());
                invoiceAgregateService.create(catInvAggr);
                
                SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
                BeanUtils.copyProperties(subCategoryInvoiceAgregate, subcat);
                subCategoryInvoiceAgregate.setId(null);
                subCategoryInvoiceAgregate.setInvoice(invoiceCopy);
                subCategoryInvoiceAgregate.setCategoryInvoiceAgregate(catInvAggr);
                subCategoryInvoiceAgregate.setRatedtransactions(new ArrayList<RatedTransaction>());
                invoiceAgregateService.create(subCategoryInvoiceAgregate);
                subCategoryInvoiceAggregatesCopy.add(subCategoryInvoiceAgregate);
                
                for (RatedTransaction rt : subcat.getRatedtransactions()) {
                    RatedTransaction rtCopy = new RatedTransaction();
                    BeanUtils.copyProperties(rtCopy, rt);
                    rtCopy.setInvoice(invoiceCopy);
                    rtCopy.setId(null);
                    rtCopy.setStatus(RatedTransactionStatusEnum.BILLED);
                    ratedTransactionService.create(rtCopy);
                    ratedTransactionCopy.add(rtCopy);
                }
            }

            invoiceService.commit();
            invoiceCopy = invoiceService.generateXmlAndPdfInvoice(invoiceCopy, true);
            draftGenerated = true;
            
            for (Entry<String, TaxInvoiceAgregate> entry : taxInvAgregateMapCopy.entrySet()) {
                TaxInvoiceAgregate taxInvAgr = entry.getValue();
                invoiceAgregateService.remove(taxInvAgr);
            }
            
            for (RatedTransaction ratedTransaction : ratedTransactionCopy) {
                ratedTransactionService.remove(ratedTransaction);
            }
            
            for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregatesCopy) {
                invoiceAgregateService.remove(subcat);
                invoiceAgregateService.remove(subcat.getCategoryInvoiceAgregate());
            }
            
            invoiceService.cancelInvoice(invoiceCopy);
            invoiceService.commit();
            
        } catch (Exception e) {             
            messages.error("Error generating xml / pdf invoice=" + e.getMessage());
        }
    
    }

    public void downloadXmlInvoice() {
        String fileName = invoiceService.getFullXmlFilePath(invoiceCopy, false);
        downloadFile(fileName);
    }

    public void downloadPdfInvoice() {
        if (invoiceCopy.getPdfFilename() == null) {
            return;
        }
        String fileName = invoiceService.getFullPdfFilePath(invoiceCopy, false);
        downloadFile(fileName);
    }

    @Override
    public String saveOrUpdate(boolean killConversation) {
        try {
            entity.setBillingAccount(getFreshBA());
            entity.setDetailedInvoice(isDetailed());

            invoiceService.assignInvoiceNumber(entity);
            super.saveOrUpdate(false);
            invoiceService.commit();
            entity = invoiceService.refreshOrRetrieve(entity);
            for (Entry<String, TaxInvoiceAgregate> entry : agregateHandler.getTaxInvAgregateMap().entrySet()) {
                TaxInvoiceAgregate taxInvAgr = entry.getValue();
                taxInvAgr.setInvoice(entity);
                invoiceAgregateService.create(taxInvAgr);
            }

            for (Entry<String, CategoryInvoiceAgregate> entry : agregateHandler.getCatInvAgregateMap().entrySet()) {
                CategoryInvoiceAgregate catInvAgr = entry.getValue();
                catInvAgr.setInvoice(entity);
                catInvAgr.setSubCategoryInvoiceAgregates(new HashSet<SubCategoryInvoiceAgregate>());
                invoiceAgregateService.create(catInvAgr);
            }

            for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregates) {
                subcat.setInvoice(entity);
                invoiceAgregateService.create(subcat);
                for (RatedTransaction rt : subcat.getRatedtransactions()) {
                    rt.setInvoice(entity);
                    rt.setStatus(RatedTransactionStatusEnum.BILLED);
                    if (rt.isTransient()) {
                        ratedTransactionService.create(rt);
                    } else {
                        ratedTransactionService.update(rt);
                    }

                }
            }

            for (Invoice invoice : entity.getLinkedInvoices()) {
                invoice.getLinkedInvoices().add(entity);
                invoiceService.update(invoice);
            }

            try {
                invoiceService.commit();
                entity = invoiceService.generateXmlAndPdfInvoice(entity, true);
            } catch (Exception e) {
                messages.error("Error generating xml / pdf invoice=" + e.getMessage());
            }

            return getListViewName();
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return null;
        }
    }

    /**
     * Include a copy from linkedIncoice's RatedTransaction
     * 
     */
    public void importFromLinkedInvoices() {
        try {
            if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
                messages.error("BillingAccount is required.");
                return;
            }

            if (entity.getLinkedInvoices() != null && entity.getLinkedInvoices().size() <= 0) {
                messages.info(new BundleKey("messages", "message.invoice.addAggregate.linked.null"));
                return;
            }
            for (Invoice invoice : entity.getLinkedInvoices()) {
                invoice = invoiceService.findById(invoice.getId());
                if (entity.getLinkedInvoices().size() == 1) {
                    entity.setCfValues(invoice.getCfValues());
                    customFieldDataEntryBean.refreshFieldsAndActions(entity);
                }

                if (isDetailed()) {
                    for (RatedTransaction rt : invoice.getRatedTransactions()) {
                        if (!DateUtils.isWithinDate(rt.getUsageDate(), getStartDate(), getEndDate())) {
                            continue;
                        }
                        RatedTransaction newRT = new RatedTransaction();
                        newRT.setUsageDate(rt.getUsageDate());
                        newRT.setUnitAmountWithoutTax(rt.getUnitAmountWithoutTax());
                        newRT.setQuantity(rt.getQuantity());
                        newRT.setStatus(RatedTransactionStatusEnum.BILLED);
                        newRT.setWallet(rt.getWallet());
                        newRT.setBillingAccount(getFreshBA());
                        newRT.setInvoiceSubCategory(rt.getInvoiceSubCategory());
                        newRT.setCode(rt.getCode());
                        newRT.setDescription(rt.getDescription());
                        newRT.setParameter1(rt.getParameter1());
                        newRT.setParameter2(rt.getParameter2());
                        newRT.setParameter3(rt.getParameter3());
                        newRT.setStartDate(rt.getStartDate());
                        newRT.setEndDate(rt.getEndDate());
                        newRT.setOrderNumber(rt.getOrderNumber());
                        newRT.setInvoice(entity);
                        newRT.setSeller(rt.getSeller());
                        agregateHandler.addRT(newRT, rt.getInvoiceSubCategory().getDescription(), getFreshUA());
                        updateAmountsAndLines(getFreshBA());
                    }
                } else {
                    for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
                        if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                            agregateHandler.addInvoiceSubCategory(((SubCategoryInvoiceAgregate) invoiceAgregate).getInvoiceSubCategory(), getFreshBA(), getFreshUA(),
                                invoiceAgregate.getDescription(), invoiceAgregate.getAmountWithoutTax());
                            updateAmountsAndLines(getFreshBA());
                        }
                    }
                    
                    for (RatedTransaction rt : invoice.getRatedTransactions()) {
                        if(rt.getWallet() == null) {
                            agregateHandler.addRT(rt, rt.getInvoiceSubCategory().getDescription(), getFreshUA());
                        }
                    }
                    updateAmountsAndLines(getFreshBA());
                }

            }
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
        }

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
        return chargeTemplateServiceAll.findByInvoiceSubCategory(selectedInvoiceSubCategory);
    }

    public void setIncludeBalance(boolean includeBalance) {
        this.includeBalance = includeBalance;
        try {
            updateAmountsAndLines(getFreshBA());
        } catch (BusinessException be) {
            messages.error(be.getMessage());
            return;
        }
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
        // TODO singletone this
        if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
            throw new BusinessException("BillingAccount is required.");
        }
        return billingAccountService.retrieveIfNotManaged(entity.getBillingAccount());
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
    public void deleteLinkedInvoiceCategory() throws BusinessException {
        for (SubCategoryInvoiceAgregate subCat : selectedCategoryInvoiceAgregate.getSubCategoryInvoiceAgregates()) {
            agregateHandler.removeInvoiceSubCategory(subCat.getInvoiceSubCategory(), getFreshBA(), getFreshUA(), subCat.getDescription(), subCat.getAmountWithoutTax());
            updateAmountsAndLines(getFreshBA());
        }

    }

    /**
     * delete a sub cat invoice agregate
     * 
     * @throws BusinessException General business exception
     */
    public void deleteLinkedInvoiceSubCategory() throws BusinessException {
        agregateHandler.removeInvoiceSubCategory(selectedSubCategoryInvoiceAgregate.getInvoiceSubCategory(), getFreshBA(), getFreshUA(),
            selectedSubCategoryInvoiceAgregate.getDescription(), selectedSubCategoryInvoiceAgregate.getAmountWithoutTax());
        updateAmountsAndLines(getFreshBA());

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

    public void addAggregatedLine() throws BusinessException {

        if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
            messages.error("BillingAccount is required.");
            return;
        }

        if (StringUtils.isBlank(description)) {
            messages.error("Description is required.");
            return;
        }

        if (amountWithoutTax == null || selectedInvoiceSubCategory == null) {
            messages.error("AmountWithoutTax and InvoiceSubCategory is required.");
            return;
        }

        selectedInvoiceSubCategory = invoiceSubCategoryService.retrieveIfNotManaged(selectedInvoiceSubCategory);

        agregateHandler.addInvoiceSubCategory(selectedInvoiceSubCategory, getFreshBA(), getFreshUA(), description, amountWithoutTax);
        updateAmountsAndLines(getFreshBA());
    }

    /**
     * Called whene quantity or unitAmout are changed in the dataList detailInvoice
     * 
     * @param invSubCat sub category invoice
     * @throws BusinessException business exception.
     */
    public void reComputeAmountWithoutTax(SubCategoryInvoiceAgregate invSubCat) throws BusinessException {
        agregateHandler.reset();
        for (CategoryInvoiceAgregate cat : categoryInvoiceAggregates) {
            for (SubCategoryInvoiceAgregate subCate : cat.getSubCategoryInvoiceAgregates()) {
                InvoiceSubCategory tmp = subCate.getInvoiceSubCategory();
                agregateHandler.addInvoiceSubCategory(tmp, getFreshBA(), getFreshUA(), subCate.getDescription(), subCate.getAmountWithoutTax());
                updateAmountsAndLines(getFreshBA());
            }
        }

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
        // TODO use outcome and params
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

    public void handleSelectedInvoiceCatOrSubCat() {
        if (selectedInvoiceSubCategory != null) {
            description = selectedInvoiceSubCategory.getDescriptionOrCode();
        }
    }

    public void handleSelectedCharge() {
        if (selectedCharge != null) {
            description = selectedCharge.getDescriptionOrCode();
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
}