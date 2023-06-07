package org.meveo.service.script;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.collections4.MapUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Customer;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RejectedBillingAccountService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.order.OrderItemService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingAmountService;

/**
 *
 * @author Said Ramli
 */
@SuppressWarnings({ "unchecked", "unused" })
public class DeleteCustomersScript extends Script {

    private final AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());
    private final CustomerService customerService = (CustomerService) getServiceInterface(CustomerService.class.getSimpleName());
    private final InvoiceService invoiceService = (InvoiceService) getServiceInterface(InvoiceService.class.getSimpleName());
    private final SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface(SubscriptionService.class.getSimpleName());
    private final RatedTransactionService ratedTransactionService = (RatedTransactionService) getServiceInterface(RatedTransactionService.class.getSimpleName());
    private final WalletService walletService = (WalletService) getServiceInterface(WalletService.class.getSimpleName());
    private final RejectedBillingAccountService rejectedBillingAccountService = (RejectedBillingAccountService) getServiceInterface(RejectedBillingAccountService.class.getSimpleName());
    private final CounterInstanceService counterInstanceService = (CounterInstanceService) getServiceInterface(CounterInstanceService.class.getSimpleName());
    private final CounterPeriodService counterPeriodService = (CounterPeriodService) getServiceInterface(CounterPeriodService.class.getSimpleName());
    private final OrderItemService orderItemService = (OrderItemService) getServiceInterface(OrderItemService.class.getSimpleName());
    private final ProductInstanceService productInstanceService = (ProductInstanceService) getServiceInterface(ProductInstanceService.class.getSimpleName());
    private final BusinessEntityService businessEntityService = (BusinessEntityService) getServiceInterface(BusinessEntityService.class.getSimpleName());
    private final WalletOperationService walletOperationService = (WalletOperationService) getServiceInterface(WalletOperationService.class.getSimpleName());
    private final ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface(ServiceInstanceService.class.getSimpleName());

    private final EdrService edrService = (EdrService) getServiceInterface(EdrService.class.getSimpleName());

    @SuppressWarnings("rawtypes")
    private final ChargeInstanceService chargeInstanceService = (ChargeInstanceService) getServiceInterface(ChargeInstanceService.class.getSimpleName());

    private static final List<String> noCheckAllowedValues = Arrays.asList("0", "1");

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {

        log.debug(" >>> execute {} ", methodContext.entrySet());

        try {

            Map<String, String> record = (Map<String, String>) methodContext.get("RECORD");
            if (MapUtils.isEmpty(record)) {
                throw new BusinessException(String.format("Parameter RECORD is missing"));
            }

            final String customerCode = record.get("customerCode");
            Customer customer = customerService.findByCode(customerCode);
            if (customer == null) {
                throw new BusinessException(String.format("No customer found having code = %s ", customerCode));
            }

            String noCheckVal = record.get("noCheck");
            if (isNotBlank(noCheckVal) && !noCheckAllowedValues.contains(noCheckVal)) {
                throw new BusinessException(String.format("Invalid ''noCheck'' value = %s , allowed values are [0,1]", noCheckVal));
            }
            boolean noCheck = "1".contentEquals(noCheckVal);

            if (!noCheck) {
                // If noCheck is 0 then the following conditions must be met in order to allow
                // deletion:
                // No unmatched AO
                // No open EDR/WO/RT

                this.checkUnmatchedAOs(customer);
                this.checkOpenEDRs(customer);
                this.checkOpenWOs(customer);
                this.checkOpenRTs(customer);
            }

            this.removeCustomerHirarchy(customer);

        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.error(" Error executing DeleteCustomersScript ", e);
            throw new BusinessException(e.getMessage());
        }
    }

    private void checkOpenEDRs(Customer customer) throws BusinessException {
        try {
            log.debug(" Start checkOpenEDRs for customer = {} ", customer);
            String query = "select count(edr.id) from EDR edr where edr.status = ''OPEN'' and edr.subscription.userAccount.billingAccount.customerAccount.customer.id =:customerId ";

            Object count = edrService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).getSingleResult();
            log.debug(" count of open EDRs = {} ", count);
            log.debug(" End checkOpenEDRs for customer = {} ", customer);

            if (((Long) count).intValue() > 0) {
                throw new BusinessException(" Customer has [" + count + "] open EDRs ");
            }
        } catch (Exception e) {
            log.error(" Error on checkOpenEDRs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void checkOpenWOs(Customer customer) throws BusinessException {
        try {
            log.debug(" Start checkOpenWOs for customer = {} ", customer);
            String query = "select count(wo.id) from WalletOperation wo where wo.status = ''OPEN'' and wo.chargeInstance.userAccount.billingAccount.customerAccount.customer.id =:customerId ";
            final WalletOperationService walletOperationService = (WalletOperationService) getServiceInterface(WalletOperationService.class.getSimpleName());

            Object count = walletOperationService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).getSingleResult();
            log.debug(" count of open WOs = {} ", count);
            log.debug(" End checkOpenWOs for customer = {} ", customer);

            if (((Long) count).intValue() > 0) {
                throw new BusinessException(" Customer has [" + count + "] open WOs ");
            }
        } catch (Exception e) {
            log.error(" Error on checkOpenWOs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void checkOpenRTs(Customer customer) throws BusinessException {
        try {

            log.debug(" Start checkOpenRTs for customer = {} ", customer);
            String query = "select count(rt.id) from RatedTransaction rt where rt.status = ''OPEN'' and rt.billingAccount.customerAccount.customer.id =:customerId ";
            Object count = this.ratedTransactionService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).getSingleResult();
            log.debug(" count of open RTs = {} ", count);
            log.debug(" End checkOpenRTs for customer = {} ", customer);

            if (((Long) count).intValue() > 0) {
                throw new BusinessException(" Customer has [" + count + "] open RTs ");
            }
        } catch (Exception e) {
            log.error(" Error on checkOpenRTs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void checkUnmatchedAOs(Customer customer) throws BusinessException {
        try {

            log.debug(" Start checkUnmatchedAOs for customer = {} ", customer);
            String query = "select count(ao.id) from AccountOperation ao where ao.matchingStatus = ''O'' and ao.customerAccount.customer.id =:customerId ";
            final AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());
            Object count = accountOperationService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).getSingleResult();
            log.debug(" count of unmatched AOs = {} ", count);
            log.debug(" End checkUnmatchedAOs for customer = {} ", customer);

            if (((Long) count).intValue() > 0) {
                throw new BusinessException(" Customer has [" + count + "] unmatched AOs ");
            }
        } catch (Exception e) {
            log.error(" Error on checkUnmatchedAOs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeCustomerHirarchy(Customer customer) throws BusinessException {
        log.debug(" Start removeCustomerHirarchy for customer = {} ", customer);

        //
        // A workaround for #4134 , no need for this method if #4134 is fixed (only
        // 6.1.X is concerned)!
        this.nullifyDDRequestItem(customer);
        this.nullifyAOsSeller(customer);

        this.removeWalletOperations(customer);
        this.removeChargeInstances(customer);
        // the relationship [SubCategoryInvoiceAgregate <=> WalletInstance] should be broke fisrt
        this.nullifySubCategoryInvoiceAgregatesWallet(customer);
        this.removeRatedTransactions(customer);
        this.nullifyWalletInstanceInUserAccount(customer);
        this.removeWalletInstances(customer);

        this.removePaymentHistory(customer);
//      this.removeRatedTransactions(customer);

        this.removeMatchingAmounts(customer);
        this.nullifyRecordedInvoice(customer);
        this.removeInvoices(customer);

        this.removeServiceInstances(customer);
        this.removeAccess(customer);
        this.removeEDRs(customer);
        this.removeDiscountPlanInstance(customer);
        this.nullifyAOSubscriptions(customer);
        this.removeSubscriptions(customer);
        this.removeRejectedBAs(customer);

        this.removeCounterPeriods(customer);
        this.removeCounterInstances(customer);
        this.removeOrderItems(customer);
        this.removeProductInstances(customer);

        this.customerService.remove(customer);

        log.debug(" End removeCustomerHirarchy for customer = {} ", customer);
    }

    private void nullifyWalletInstanceInUserAccount(Customer customer) {
        try {
            log.debug(" Start removeEDRs for customer = {} ", customer);
            String query = "Update UserAccount ua set ua.wallet = null where ua.wallet.id in (select wi.id from WalletInstance wi where wi.userAccount.billingAccount.customerAccount.customer.id =:customerId)";
            edrService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeEDRs for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeEDRs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeEDRs(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeEDRs for customer = {} ", customer);

            String queryText = "delete from EDR edr1 where edr1.id in (select edr.id from EDR edr where edr.subscription.userAccount.billingAccount.customerAccount.customer.id =:customerId )";
            edrService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeEDRs for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeEDRs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeMatchingAmounts(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeMatchingAmounts for customer = {} ", customer);

            final MatchingAmountService matchingAmountService = (MatchingAmountService) getServiceInterface(MatchingAmountService.class.getSimpleName());
            final String query = "delete from MatchingAmount ma1 where ma1.id in (select ma.id from MatchingAmount ma where ma.accountOperation.customerAccount.customer.id =:customerId )";
            matchingAmountService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeMatchingAmounts for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeMatchingAmounts : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeAccess(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeAccess for customer = {} ", customer);

            final AccessService accessService = (AccessService) getServiceInterface(AccessService.class.getSimpleName());

            final String queryText = "delete from Access a1 where a1.id in (select a.id from Access a where a.subscription.userAccount.billingAccount.customerAccount.customer.id =:customerId )";
            accessService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeAccess for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeAccess : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeServiceInstances(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeServiceInstances for customer = {} ", customer);

            String queryText = "delete from ServiceInstance si1 where si1.id in (select si.id from ServiceInstance si where si.subscription.userAccount.billingAccount.customerAccount.customer.id =:customerId) ";
            this.serviceInstanceService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeServiceInstances for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeServiceInstances : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeWalletOperations(Customer customer) throws BusinessException {
        try {
            log.debug(" removeWalletOperations for customer={} ", customer);
            String queryText = "delete from WalletOperation wo where wo.id in ( select wo1.id from WalletOperation wo1  where wo1.chargeInstance.userAccount.billingAccount.customerAccount.customer.id =:customerId) ";

            Query query = this.walletOperationService.getEntityManager().createQuery(queryText);
            query.setParameter("customerId", customer.getId());
            query.executeUpdate();
        } catch (Exception e) {
            log.error(" Error on removeWalletOperations : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    /**
     * using Sub-queries to Resolve Error: ERROR: syntax error at or near "cross"
     *
     * @param customer
     * @throws BusinessException
     */
    private void removePaymentHistory(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removePaymentHistory for customer = {} ", customer);

            String query = "delete from PaymentHistory ph1 where ph1.id in (select ph2.id from PaymentHistory ph2 where ph2.customerCode=:customerCode) ";
            int count = this.businessEntityService.getEntityManager().createQuery(query).setParameter("customerCode", customer.getCode()).executeUpdate();

            log.debug(" {} PaymentHistory to delete ", count);
            log.debug(" End removePaymentHistory for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removePaymentHistory : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    /**
     * using Sub-queries to Resolve Error: ERROR: syntax error at or near "cross"
     *
     * @param customer
     * @throws BusinessException
     */

    private void nullifyDDRequestItem(Customer customer) {

        try {
            log.debug(" Start deleteDDRequestItem for customer = {} ", customer);

            String query = "update DDRequestItem ddri set ddri.automatedPayment= null where ddri.automatedPayment.id in (select ao2.id from AccountOperation ao2 where ao2.customerAccount.customer.id =:customerId) ";
            int count = this.accountOperationService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" seller to nullify for {} AOs ", count);
            log.debug(" End nullifyAOsSeller for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on nullifyAOsSeller : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void nullifyAOsSeller(Customer customer) throws BusinessException {
        try {
            log.debug(" Start nullifyAOsSeller for customer = {} ", customer);

            String query = "UPDATE AccountOperation ao1 SET ao1.seller = null where ao1.id in (select ao2.id from AccountOperation ao2 where ao2.customerAccount.customer.id =:customerId) ";
            int count = this.accountOperationService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" seller to nullify for {} AOs ", count);
            log.debug(" End nullifyAOsSeller for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on nullifyAOsSeller : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void nullifySubCategoryInvoiceAgregatesWallet(Customer customer) throws BusinessException {
        try {
            log.debug(" Start nullifySubCategoryInvoiceAgregatesWallet for customer = {} ", customer);

            StringBuilder query = new StringBuilder("UPDATE SubCategoryInvoiceAgregate subCat SET subCat.wallet = null ")
                .append(" where subCat.id in (select subCatIn.id from SubCategoryInvoiceAgregate subCatIn where subCatIn.wallet.userAccount.billingAccount.customerAccount.customer.id =:customerId) ");

            int count = this.accountOperationService.getEntityManager().createQuery(query.toString()).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" wallet to nullify for {} SubCategoryInvoiceAgregate ", count);
            log.debug(" End nullifySubCategoryInvoiceAgregatesWallet for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on nullifySubCategoryInvoiceAgregatesWallet : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }

    }

    private void removeProductInstances(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeProductInstances for customer = {} ", customer);
            String query = "delete from ProductInstance pi where pi.id in (select p.id from ProductInstance p where p.userAccount.billingAccount.customerAccount.customer.id =:customerId )";
            productInstanceService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();
            log.debug(" End removeProductInstances for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeProductInstances : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeOrderItems(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeOrderItem for customer = {} ", customer);

            String query = "delete from OrderItem oi where oi.id in (select p.id from OrderItem p where p.userAccount.billingAccount.customerAccount.customer.id =:customerId) ";

            orderItemService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeOrderItem for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeOrderItem : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeCounterPeriods(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeCounterPeriods for customer = {} ", customer);
            String queryText = "delete from CounterPeriod cp1 where cp1.id in ( select c.id from CounterPeriod c where c.counterInstance.userAccount.billingAccount.customerAccount.customer.id =:customerId )";
            counterInstanceService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();
            log.debug(" End removeCounterPeriods for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeCounterPeriods : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeCounterInstances(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeCounterInstances for customer = {} ", customer);

            String query = "delete from CounterInstance c1 where c1.id in (select c.id from CounterInstance c where c.userAccount.billingAccount.customerAccount.customer.id =:customerId )";
            counterInstanceService.getEntityManager().createQuery(query).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeCounterInstances for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeCounterInstances : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeRejectedBAs(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeRejectedBAs for customer = {} ", customer);

            String queryText = "delete from RejectedBillingAccount rba1 where rba1.id in (select rba.id from RejectedBillingAccount rba where rba.billingAccount.customerAccount.customer.id =:customerId )";
            rejectedBillingAccountService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeRejectedBAs for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeRejectedBAs : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void nullifyRecordedInvoice(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeRecordedInvoice for customer = {} ", customer);

            String queryText = "update  RecordedInvoice ri   set ri.invoice = null where ri.invoice.id in ( select inv.id from Invoice inv where inv.billingAccount.customerAccount.customer.id =:customerId )";
            invoiceService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeRecordedInvoice for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeRecordedInvoice : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeInvoices(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeInvoices for customer = {} ", customer);

            String queryText = "delete from Invoice inv1 where inv1.id in (select inv.id from Invoice inv where inv.billingAccount.customerAccount.customer.id =:customerId )";
            invoiceService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeInvoices for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeInvoices : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeWalletInstances(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeWalletInstances for customer = {} ", customer);

            String queryText = "delete from WalletInstance wi1 where wi1.id in (select wi.id from WalletInstance wi where wi.userAccount.billingAccount.customerAccount.customer.id =:customerId) ";
            this.walletService.getEntityManager().createQuery(queryText).setParameter("customerId", customer.getId()).executeUpdate();

            log.debug(" End removeWalletInstances for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeWalletInstances : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeRatedTransactions(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeRatedTransactions for customer = {} ", customer);

            String queryText = "delete from RatedTransaction rt1 where rt1.id in (select rt.id from RatedTransaction rt where rt.billingAccount.customerAccount.customer.id =:customerId )";
            Query query = chargeInstanceService.getEntityManager().createQuery(queryText);
            query.setParameter("customerId", customer.getId());
            query.executeUpdate();

            log.debug(" End removeRatedTransactions for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeRatedTransactions : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }

    }

    private void removeChargeInstances(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeChargeInstances for customer = {} ", customer);

            String queryText = "delete from ChargeInstance ci1 where ci1.id in (select ci.id from ChargeInstance ci where ci.userAccount.billingAccount.customerAccount.customer.id =:customerId )";
            Query query = chargeInstanceService.getEntityManager().createQuery(queryText);
            query.setParameter("customerId", customer.getId());
            query.executeUpdate();

            log.debug(" End removeChargeInstances for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeChargeInstances : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void nullifyAOSubscriptions(Customer customer) throws BusinessException {
        try {
            log.debug(" Start nullifyAOSubscriptions for customer = {} ", customer);
            String queryText = "update AccountOperation  ao set ao.subscription= null where ao.subscription.id in (select s.id from Subscription s where s.userAccount.billingAccount.customerAccount.customer =:customer) ";
            subscriptionService.getEntityManager().createQuery(queryText).setParameter("customer", customer).executeUpdate();
            log.debug(" End nullifyAOSubscriptions for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on nullifyAOSubscriptions : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeSubscriptions(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeSubscriptions for customer = {} ", customer);
            String queryText = "delete from Subscription s1 where s1.id in (select s.id from Subscription s where s.userAccount.billingAccount.customerAccount.customer =:customer) ";
            subscriptionService.getEntityManager().createQuery(queryText).setParameter("customer", customer).executeUpdate();
            log.debug(" End removeSubscriptions for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeSubscriptions : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private void removeDiscountPlanInstance(Customer customer) throws BusinessException {
        try {
            log.debug(" Start removeSubscriptions for customer = {} ", customer);
            String queryText = "delete from DiscountPlanInstance dpi where subscription_id in (select s.id from Subscription s where s.userAccount.billingAccount.customerAccount.customer =:customer) ";
            subscriptionService.getEntityManager().createQuery(queryText).setParameter("customer", customer).executeUpdate();
            log.debug(" End removeSubscriptions for customer = {} ", customer);
        } catch (Exception e) {
            log.error(" Error on removeSubscriptions : [{}] ", e.getMessage(), e);
            throw new BusinessException(e);
        }
    }

    private <T> Collection<T> safe(Collection<T> collection) {
        return collection == null ? Collections.EMPTY_LIST : collection;
    }
}