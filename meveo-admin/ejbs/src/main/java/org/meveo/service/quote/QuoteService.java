package org.meveo.service.quote;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.BaseService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ProductChargeInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;

@Stateless
public class QuoteService extends BaseService {

    @Inject
    private CDRParsingService cdrParsingService;

    @Inject
    private OfferTemplateService offerTemplateService;

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

    public void provideQuote(List<String> cdrs, String offerCode, UserAccount userAccount, Date fromDate, Date toDate, User currentUser) throws BusinessException {

        List<WalletOperation> walletOperations = new ArrayList<>();

        BigDecimal quantity = new BigDecimal(1);

        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode, currentUser.getProvider());

        // Add subscription charges
        for (OfferServiceTemplate service : offerTemplate.getOfferServiceTemplates()) {
            List<ServiceChargeTemplateSubscription> subscriptionCharges = service.getServiceTemplate().getServiceSubscriptionCharges();
            for (ServiceChargeTemplateSubscription charge : subscriptionCharges) {
                OneShotChargeTemplate chargeTemplate = charge.getChargeTemplate();
                walletOperations.add(oneShotChargeInstanceService.oneShotChargeApplicationVirtual(chargeTemplate, userAccount, offerCode, fromDate, quantity, null, null, null,
                    null, null, currentUser));
            }
        }

        // Add product charges
        for (OfferProductTemplate service : offerTemplate.getOfferProductTemplates()) {

            List<ProductChargeTemplate> productCharges = service.getProductTemplate().getProductChargeTemplates();
            for (ProductChargeTemplate chargeTemplate : productCharges) {
                walletOperations.add(productChargeInstanceService.applyProductChargeInstanceVirtual(chargeTemplate, null, userAccount, offerCode, fromDate, quantity, null, null,
                    null, null, null, currentUser));
            }
        }

        // Add recurring charges
        for (OfferServiceTemplate service : offerTemplate.getOfferServiceTemplates()) {
            List<ServiceChargeTemplateRecurring> recurringCharges = service.getServiceTemplate().getServiceRecurringCharges();
            for (ServiceChargeTemplateRecurring charge : recurringCharges) {
                RecurringChargeTemplate chargeTemplate = charge.getChargeTemplate();
                walletOperations.addAll(recurringChargeInstanceService.applyRecurringChargeVirtual(chargeTemplate, userAccount, offerCode, fromDate, fromDate, toDate, quantity,
                    null, null, null, null, null, currentUser));
            }
        }

        // Process CDRS
        if (cdrs != null && !cdrs.isEmpty()) {

            cdrParsingService.initByApi(currentUser.getUserName(), "quote");

            List<EDR> edrs = new ArrayList<>();

            // Parse CDRs to Edrs
            try {
                for (String cdr : cdrs) {
                    edrs.add(cdrParsingService.getEDRWoutAccess(cdr, CDRParsingService.CDR_ORIGIN_API, fromDate, currentUser));
                }

            } catch (CDRParsingException e) {
                log.error("Error parsing cdr={}", e.getRejectionCause());
                throw new BusinessException(e.getRejectionCause().toString());
            }

            // Rate EDRs
            for (EDR edr : edrs) {
                log.debug("edr={}", edr);
                List<WalletOperation> walletOperationsFromEdr = usageRatingService.rateUsageVirtual(edr, offerTemplate, userAccount, currentUser);
                if (edr.getStatus() == EDRStatusEnum.REJECTED) {
                    log.error("edr rejected={}", edr.getRejectReason());
                    throw new BusinessException(edr.getRejectReason());
                }
                walletOperations.addAll(walletOperationsFromEdr);
            }
        }

        // Create rated transactions from wallet operations
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        for (WalletOperation walletOperation : walletOperations) {
            ratedTransactions.add(ratedTransactionService.createRatedTransaction(walletOperation, true, currentUser));
        }

        Invoice invoice = invoiceService.createAgregatesAndInvoiceVirtual(ratedTransactions, userAccount.getBillingAccount(), null, currentUser);

        File xmlInvoiceFile = xmlInvoiceCreator.createXMLInvoice(invoice);
        invoiceService.producePdf(invoice, currentUser);

    }
}
