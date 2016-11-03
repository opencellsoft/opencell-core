package org.meveo.service.quote;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.quote.Quote;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ProductChargeInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;

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

    @SuppressWarnings("unused")
    public Invoice provideQuote(List<String> cdrs, Subscription subscription, List<ProductInstance> productInstances, Date fromDate, Date toDate, User currentUser)
            throws BusinessException {

        List<WalletOperation> walletOperations = new ArrayList<>();

        BillingAccount billingAccount = null;

        // Add Product charges
        if (productInstances != null) {
            for (ProductInstance productInstance : productInstances) {
                if (productInstance.getUserAccount() != null) {
                    billingAccount = productInstance.getUserAccount().getBillingAccount();
                }
                for (ProductChargeInstance productChargeInstance : productInstance.getProductChargeInstances()) {
                    walletOperations.addAll(productChargeInstanceService.applyProductChargeInstance(productChargeInstance, currentUser, true));
                }
            }
        }

        if (subscription != null) {
            billingAccount = subscription.getUserAccount().getBillingAccount();

            // Add subscription charges
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                for (OneShotChargeInstance subscriptionCharge : serviceInstance.getSubscriptionChargeInstances()) {
                    walletOperations.add(oneShotChargeInstanceService.oneShotChargeApplicationVirtual(subscription, subscriptionCharge, serviceInstance.getSubscriptionDate(),
                        serviceInstance.getQuantity(), currentUser));
                }

                // Add recurring charges
                for (RecurringChargeInstance recurringCharge : serviceInstance.getRecurringChargeInstances()) {
                    walletOperations.addAll(recurringChargeInstanceService.applyRecurringChargeVirtual(recurringCharge, fromDate, toDate, currentUser));
                }
            }

            // Process CDRS
            if (cdrs != null && !cdrs.isEmpty() && subscription!=null) {

                cdrParsingService.initByApi(currentUser.getUserName(), "quote");

                List<EDR> edrs = new ArrayList<>();

                // Parse CDRs to Edrs
                try {
                    for (String cdr : cdrs) {
                        edrs.add(cdrParsingService.getEDRForVirtual(cdr, CDRParsingService.CDR_ORIGIN_API, subscription, currentUser));
                    }

                } catch (CDRParsingException e) {
                    log.error("Error parsing cdr={}", e.getRejectionCause());
                    throw new BusinessException(e.getRejectionCause().toString());
                }

                // Rate EDRs
                for (EDR edr : edrs) {
                    log.debug("edr={}", edr);
                    List<WalletOperation> walletOperationsFromEdr = usageRatingService.rateUsageWithinTransaction(edr, true, currentUser);
                    if (edr.getStatus() == EDRStatusEnum.REJECTED) {
                        log.error("edr rejected={}", edr.getRejectReason());
                        throw new BusinessException(edr.getRejectReason());
                    }
                    walletOperations.addAll(walletOperationsFromEdr);
                }
            }
        }

        // Create rated transactions from wallet operations
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        for (WalletOperation walletOperation : walletOperations) {
            ratedTransactions.add(ratedTransactionService.createRatedTransaction(walletOperation, true, currentUser));
        }

        Invoice invoice = invoiceService.createAgregatesAndInvoiceVirtual(ratedTransactions, billingAccount, null, currentUser);

        File xmlInvoiceFile = xmlInvoiceCreator.createXMLInvoice(invoice);
        invoiceService.producePdf(invoice, currentUser);

        return invoice;
    }
}
