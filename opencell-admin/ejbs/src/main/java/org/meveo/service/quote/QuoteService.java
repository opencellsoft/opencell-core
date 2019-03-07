package org.meveo.service.quote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.quote.Quote;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ProductChargeInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;

/**
 * The QuoteService class
 *
 * @author Andrius Karpavicius
 * @author Mounir BAHIJE
 * @lastModifiedVersion 7.0
 */
@Stateless
public class QuoteService extends BusinessService<Quote> {

    @Inject
    private CDRParsingService cdrParsingService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private XMLInvoiceCreator xmlInvoiceCreator;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private ProductChargeInstanceService productChargeInstanceService;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private InvoiceTypeService invoiceTypeService;
    
    /**
     * Create a simulated invoice for quote.
     * @param quoteInvoiceInfos map of quote invoice info
     * @return list of invoice
     * @throws BusinessException business exception
     */
    public List<Invoice> provideQuote(Map<String, List<QuoteInvoiceInfo>> quoteInvoiceInfos) throws BusinessException {
        return provideQuote(quoteInvoiceInfos, true);
    }

    /**
     * Create a simulated invoice for quote.
     * 
     * @param quoteInvoiceInfos map of quote invoice info
     * @param generatePdf generate a PDF file or not.
     * @return list of invoice
     * @throws BusinessException business exception
     */
    @SuppressWarnings("unused")
    public List<Invoice> provideQuote(Map<String, List<QuoteInvoiceInfo>> quoteInvoiceInfos, boolean generatePdf) throws BusinessException {    	
        log.info("Creating simulated invoice for {}", quoteInvoiceInfos);

        List<Invoice> invoices = new ArrayList<>();

        for (Entry<String, List<QuoteInvoiceInfo>> invoiceInfoEntry : quoteInvoiceInfos.entrySet()) {

            List<WalletOperation> walletOperations = new ArrayList<>();
            List<RatedTransaction> ratedTransactions = new ArrayList<>();
            BillingAccount billingAccount = null;

            for (QuoteInvoiceInfo quoteInvoiceInfo : invoiceInfoEntry.getValue()) {

                // Add Product charges
                List<ProductInstance> productInstances = quoteInvoiceInfo.getProductInstances();
                if (productInstances != null) {
                    for (ProductInstance productInstance : productInstances) {
                        UserAccount userAccount = productInstance.getUserAccount();
                        if (userAccount != null) {
                            billingAccount = userAccount.getBillingAccount();
                        }
                        List<ProductChargeInstance> productChargeInstances = productInstance.getProductChargeInstances();
                        for (ProductChargeInstance productChargeInstance : productChargeInstances) {
                            walletOperations.addAll(productChargeInstanceService.applyProductChargeInstance(productChargeInstance, true));
                        }
                    }
                }

                Subscription subscription = quoteInvoiceInfo.getSubscription();
				if (subscription != null) {

                    billingAccount = subscription.getUserAccount().getBillingAccount();

                    // Add Service charges
                    for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {

                        // Add subscription charges
                        for (OneShotChargeInstance subscriptionCharge : serviceInstance.getSubscriptionChargeInstances()) {
                            walletOperations.add(oneShotChargeInstanceService.oneShotChargeApplicationVirtual(subscription, subscriptionCharge,
                                serviceInstance.getSubscriptionDate(), serviceInstance.getQuantity()));
                        }

                        // Add termination charges
                        if (serviceInstance.getTerminationDate() != null && serviceInstance.getSubscriptionTerminationReason().isApplyTerminationCharges()) {
                            for (OneShotChargeInstance terminationCharge : serviceInstance.getTerminationChargeInstances()) {
                                walletOperations.add(oneShotChargeInstanceService.oneShotChargeApplicationVirtual(subscription, terminationCharge,
                                    serviceInstance.getTerminationDate(), serviceInstance.getQuantity()));
                            }
                        }

                        // Add recurring charges
                        for (RecurringChargeInstance recurringCharge : serviceInstance.getRecurringChargeInstances()) {
                            if (recurringCharge != null) {
                                recurringCharge = recurringChargeInstanceService.findByCode(recurringCharge.getCode());
                            }
                            List<WalletOperation> walletOps = recurringChargeInstanceService.applyRecurringChargeVirtual(recurringCharge, quoteInvoiceInfo.getFromDate(),
                                quoteInvoiceInfo.getToDate());
                            if (walletOperations != null && walletOps != null) {
                                walletOperations.addAll(walletOps);
                            }
                        }
                    }

                    // Process CDRS
                    if (quoteInvoiceInfo.getCdrs() != null && !quoteInvoiceInfo.getCdrs().isEmpty() && subscription != null) {

                        cdrParsingService.initByApi(currentUser.getUserName(), "quote");

                        List<EDR> edrs = new ArrayList<>();

                        // Parse CDRs to Edrs
                        try {
                            for (String cdr : quoteInvoiceInfo.getCdrs()) {
                                edrs.add(cdrParsingService.getEDRForVirtual(cdr, CDRParsingService.CDR_ORIGIN_API, subscription));
                            }

                        } catch (CDRParsingException e) {
                            log.error("Error parsing cdr={}", e.getRejectionCause());
                            throw new BusinessException(e.getRejectionCause().toString());
                        }

                        // Rate EDRs
                        for (EDR edr : edrs) {
                            log.debug("edr={}", edr);
                            try {
                                List<WalletOperation> walletOperationsFromEdr = usageRatingService.rateUsageDontChangeTransaction(edr, true);
                                if (edr.getStatus() == EDRStatusEnum.REJECTED) {
                                    log.error("edr rejected={}", edr.getRejectReason());
                                    throw new BusinessException(edr.getRejectReason());
                                }
                                walletOperations.addAll(walletOperationsFromEdr);

                            } catch (BusinessException e) {
                                if (e instanceof InsufficientBalanceException) {
                                    log.error("edr rejected={}", edr.getRejectReason());
                                } else {
                                    log.error("Exception rating edr={}", e);
                                }
                                throw e;
                            }
                        }
                    }
                }
            }            
            // Create rated transactions from wallet operations
            for (WalletOperation walletOperation : walletOperations) {
                ratedTransactions.add(ratedTransactionService.createRatedTransaction(walletOperation, true));
            }
            Invoice invoice = invoiceService.createAgregatesAndInvoiceVirtual(ratedTransactions, billingAccount, invoiceTypeService.getDefaultQuote());
            File xmlInvoiceFile = xmlInvoiceCreator.createXMLInvoice(invoice, true);
            
            if(generatePdf) {
                invoiceService.produceInvoicePdfNoUpdate(invoice);
            }
            
            // Clean up data (left only the methods that remove FK data that would fail to persist in case of virtual operations)
            // invoice.setBillingAccount(null);
            invoice.setRatedTransactions(ratedTransactions);
            for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
                log.debug("Invoice aggregate class {}", invoiceAgregate.getClass().getName());
                // invoiceAgregate.setBillingAccount(null);
                // invoiceAgregate.setTradingCurrency(null);
                // invoiceAgregate.setTradingLanguage(null);
                // invoiceAgregate.setBillingRun(null);
                // invoiceAgregate.setUserAccount(null);
                invoiceAgregate.setAuditable(null);
                invoiceAgregate.updateAudit(currentUser);
                if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
                    // ((CategoryInvoiceAgregate)invoiceAgregate).setInvoiceCategory(null);
                    // ((CategoryInvoiceAgregate)invoiceAgregate).setSubCategoryInvoiceAgregates(null);
                } else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                    // ((TaxInvoiceAgregate)invoiceAgregate).setTax(null);
                } else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                    // ((SubCategoryInvoiceAgregate)invoiceAgregate).setInvoiceSubCategory(null);
                    // ((SubCategoryInvoiceAgregate)invoiceAgregate).setSubCategoryTaxes(null);
                    // ((SubCategoryInvoiceAgregate)invoiceAgregate).setCategoryInvoiceAgregate(null);
                    ((SubCategoryInvoiceAgregate) invoiceAgregate).setWallet(null);
                    //((SubCategoryInvoiceAgregate) invoiceAgregate).setRatedtransactions(null);
                }
            }
            invoiceService.create(invoice);
            invoiceService.postCreate(invoice);
            invoices.add(invoice);
        }
        return invoices;
    }

    @Override
    public void create(Quote quote) throws BusinessException {

        if (quote.getQuoteItems() == null || quote.getQuoteItems().isEmpty()) {
            throw new ValidationException("At least one quote line item is required");
        }

        super.create(quote);
    }

    @Override
    public Quote update(Quote quote) throws BusinessException {

        if (quote.getQuoteItems() == null || quote.getQuoteItems().isEmpty()) {
            throw new ValidationException("At least one quote line item is required");
        }

        return super.update(quote);
    }
}