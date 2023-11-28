package org.meveo.service.securityDeposit.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.PaymentException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.securityDeposit.SecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositPaymentInput;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.*;
import org.meveo.model.securityDeposit.*;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;

@Stateless
public class SecurityDepositService extends BusinessService<SecurityDeposit> {

    private static final String OCC_TEMPLATE_ADJ_SD_CODE = "ADJ_SD";

    private static final String OCC_PAY_SD_TEMPLATE = "PAY_SD";

    @Inject
    private AuditLogService auditLogService;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    CustomerAccountService customerAccountService;

    @Inject
    AccountOperationService accountOperationService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private SecurityDepositTransactionService securityDepositTransactionService;

    @Inject
    private RefundService refundService;

    @Inject
    private ProviderService providerService;
    
    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private PaymentHistoryService paymentHistoryService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private OCCTemplateService occTemplateService;

    @Inject
    private InvoiceService invoiceService;

    protected List<String> missingParameters = new ArrayList<>();



    public BigDecimal sumAmountPerCustomer(CustomerAccount customerAccount) {
        return getEntityManager()
                .createNamedQuery("SecurityDeposit.sumAmountPerClient", BigDecimal.class)
                .setParameter("customerAccount", customerAccount)
                .getSingleResult();
    }

    public Long countPerTemplate(SecurityDepositTemplate template) {
        return getEntityManager()
                .createNamedQuery("SecurityDeposit.countPerTemplate", Long.class)
                .setParameter("template", template)
                .getSingleResult();
    }

    public void checkParameters(SecurityDeposit securityDeposit, SecurityDepositInput securityDepositInput) {
        FinanceSettings financeSettings = financeSettingsService.getFinanceSetting();
        if (!financeSettings.isAutoRefund() && (securityDepositInput.getValidityDate() != null || securityDepositInput.getValidityPeriod() != null || securityDepositInput.getValidityPeriodUnit() != null)) {
            throw new InvalidParameterException("the option 'Allow auto refund' need to be checked");
        }
        if (securityDeposit.getServiceInstance() != null && securityDeposit.getSubscription() != null) {
            ServiceInstance serviceInstance = serviceInstanceService.retrieveIfNotManaged(securityDeposit.getServiceInstance());
            if (serviceInstance.getSubscription() != null && !serviceInstance.getSubscription().getId().equals(securityDeposit.getSubscription().getId())) {
                throw new InvalidParameterException("ServiceInstance must have the same chosen in subscription");
            }
        }
    }

    public void refund(SecurityDeposit securityDepositToUpdate, String reason, SecurityDepositOperationEnum securityDepositOperationEnum, SecurityDepositStatusEnum securityDepositStatusEnum, String operationType, Invoice adjInvoice) {
        if(adjInvoice != null) {
            adjInvoice = invoiceService.findById(adjInvoice.getId(), List.of("orders"));
            if (securityDepositToUpdate.getCurrentBalance() != null && BigDecimal.ZERO.compareTo(securityDepositToUpdate.getCurrentBalance()) != 0) {
                Refund refund = createRefund(securityDepositToUpdate, adjInvoice);
                if (refund == null) {
                    throw new BusinessException("Cannot create Refund.");
                } else {
                    createSecurityDepositTransaction(securityDepositToUpdate, securityDepositToUpdate.getCurrentBalance(),
                            securityDepositOperationEnum, OperationCategoryEnum.DEBIT, refund);
                }
            }
        }        

        if (SecurityDepositStatusEnum.CANCELED.equals(securityDepositStatusEnum)) {
            securityDepositToUpdate.setCancelReason(reason);
        } else if (SecurityDepositStatusEnum.REFUNDED.equals(securityDepositStatusEnum)) {
            securityDepositToUpdate.setRefundReason(reason);
        }
        securityDepositToUpdate.setStatus(securityDepositStatusEnum);
        securityDepositToUpdate.setCurrentBalance(new BigDecimal(0));
        update(securityDepositToUpdate);
        String nameSDandExplanation = securityDepositToUpdate.getCode() + ", explanation: " + reason;
        auditLogService.trackOperation(operationType, new Date(), securityDepositToUpdate, nameSDandExplanation);
    }

    private Refund createRefund(SecurityDeposit securityDepositToUpdate, Invoice adjInvoice) {
        securityDepositToUpdate = retrieveIfNotManaged(securityDepositToUpdate);
        long amountToPay = securityDepositToUpdate.getCurrentBalance().multiply(new BigDecimal(100)).longValue();
        List<Long> accountOperationsToPayIds = new ArrayList<Long>();
        CustomerAccount customerAccount = securityDepositToUpdate.getCustomerAccount();

        if (customerAccount == null) {
            throw new EntityDoesNotExistsException("Customer Account = null");
        }
        PaymentMethod preferredPaymentMethod = customerAccountService.getPreferredPaymentMethod(customerAccount.getId());
        if (preferredPaymentMethod == null) {
            throw new EntityDoesNotExistsException("Customer Account [" + customerAccount.getCode() + "] Preferred Payment Method = null");
        }
        List<SecurityDepositTransaction> listSecurityDepositTransaction = securityDepositTransactionService.getSecurityDepositTransactionBySecurityDepositId(securityDepositToUpdate.getId());
        for (int i = 0; i < listSecurityDepositTransaction.size(); i++) {
            SecurityDepositTransaction securityDepositTransaction = listSecurityDepositTransaction.get(i);
            AccountOperation aOSecurityDepositTransaction = securityDepositTransaction.getAccountOperation();
            if (OperationCategoryEnum.CREDIT.equals(aOSecurityDepositTransaction.getTransactionCategory())) {
                accountOperationsToPayIds.add(aOSecurityDepositTransaction.getId());
            }
        }

        try {
            OCCTemplate occTemplate = occTemplateService.findByCode(OCC_TEMPLATE_ADJ_SD_CODE);
            // specific case for AO invoice : this AO of ADJ related to SecurityDeposit must have a new ADJ_SD OOCTemplate code
            RecordedInvoice ao = recordedInvoiceService.generateRecordedInvoice(adjInvoice, occTemplate);

            Long refundId = null;
            PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, preferredPaymentMethod, null);
            if (paymentGateway == null) {
                throw new PaymentException(PaymentErrorEnum.NO_PAY_GATEWAY_FOR_CA, "No payment gateway for customerAccount:" + customerAccount.getCode());
            }

            refundId = doPayment(amountToPay, List.of(ao.getId()), customerAccount, preferredPaymentMethod, refundId, paymentGateway);
            if (refundId == null) {
                return null;
            } else {
                Refund refund = refundService.findById(refundId);
                if (StringUtils.isNotBlank(adjInvoice.getInvoiceNumber())) {
                    refund.setReference(adjInvoice.getInvoiceNumber());
                    refundService.update(refund); // This is the unique place where we can persist it without changing big hierary methods
                }
                return refund;
            }
        } catch (InvoiceExistException | ImportInvoiceException e) {
            throw new BusinessException(e);
        }
    }

    private Long doPayment(long amountToPay, List<Long> accountOperationsToPayIds, CustomerAccount customerAccount, PaymentMethod preferredPaymentMethod, Long refundId,
                           PaymentGateway paymentGateway) {
        if (paymentGateway != null && (preferredPaymentMethod.getPaymentType().equals(DIRECTDEBIT) || preferredPaymentMethod.getPaymentType().equals(CARD))) {
            try {
                if (!accountOperationsToPayIds.isEmpty()) {
                    if (preferredPaymentMethod.getPaymentType().equals(CARD)) {
                        if (preferredPaymentMethod instanceof HibernateProxy) {
                            preferredPaymentMethod = (PaymentMethod) ((HibernateProxy) preferredPaymentMethod).getHibernateLazyInitializer()
                                    .getImplementation();
                        }
                        CardPaymentMethod paymentMethod = (CardPaymentMethod) preferredPaymentMethod;
                        refundId = paymentService.refundByCardSD(customerAccount, amountToPay, paymentMethod.getCardNumber(),
                                paymentMethod.getCardNumber(), paymentMethod.getHiddenCardNumber(),
                                paymentMethod.getExpirationMonthAndYear(), paymentMethod.getCardType(),
                                accountOperationsToPayIds, paymentGateway);
                    } else {
                        refundId = paymentService.refundByMandatSD(customerAccount, amountToPay, accountOperationsToPayIds, paymentGateway);
                    }
                }
            } catch (Exception exception) {
                throw new BusinessException("Error occurred during payment process for customer " + customerAccount.getCode(), exception);
            }
        }
        return refundId;
    }

    public void credit(SecurityDeposit securityDepositToUpdate, SecurityDepositCreditInput securityDepositInput) {
        securityDepositToUpdate = retrieveIfNotManaged(securityDepositToUpdate);

        BigDecimal nCurrentBalance = securityDepositInput.getAmountToCredit().add(securityDepositToUpdate.getCurrentBalance());
        securityDepositToUpdate.setCurrentBalance(nCurrentBalance);

        if (securityDepositToUpdate.getTemplate().getMaxAmount() != null) {
            BigDecimal maxAmount = securityDepositToUpdate.getTemplate().getMaxAmount();
            if (nCurrentBalance.compareTo(maxAmount) > 0) {
                throw new EntityDoesNotExistsException("The current balance + amount to credit must be less than or equal to the maximum amount of the template");
            }
        }

        if (securityDepositToUpdate.getAmount() != null && (SecurityDepositStatusEnum.VALIDATED.equals(securityDepositToUpdate.getStatus())
                || SecurityDepositStatusEnum.HOLD.equals(securityDepositToUpdate.getStatus())
                || SecurityDepositStatusEnum.REFUNDED.equals(securityDepositToUpdate.getStatus()))) {
            BigDecimal nAmount = securityDepositToUpdate.getAmount().add(securityDepositInput.getAmountToCredit().negate());
            if (nAmount.compareTo(BigDecimal.ZERO) <= 0) {
                securityDepositToUpdate.setAmount(null);
                securityDepositToUpdate.setStatus(SecurityDepositStatusEnum.LOCKED);
            } else {
                securityDepositToUpdate.setAmount(nAmount);
                securityDepositToUpdate.setStatus(SecurityDepositStatusEnum.HOLD);
            }
        }
        update(securityDepositToUpdate);
    }

    public void createSecurityDepositTransaction(SecurityDeposit securityDepositToUpdate, BigDecimal amountToCredit,
                                                 SecurityDepositOperationEnum securityDepositOperationEnum, OperationCategoryEnum operationCategoryEnum, AccountOperation accountOperation) {
        SecurityDepositTransaction securityDepositTransaction = new SecurityDepositTransaction();
        securityDepositTransaction.setAmount(amountToCredit);
        securityDepositTransaction.setTransactionCategory(operationCategoryEnum);
        securityDepositTransaction.setTransactionDate(new Date());
        securityDepositTransaction.setOperation(securityDepositOperationEnum);
        securityDepositTransaction.setSecurityDeposit(securityDepositToUpdate);
        securityDepositTransaction.setAccountOperation(accountOperation);
        securityDepositTransactionService.create(securityDepositTransaction);
    }
    
    

    public List<Long> getSecurityDepositsToRefundIds() {
        return getEntityManager()
                .createNamedQuery("SecurityDeposit.securityDepositsToRefundIds", Long.class)
                .setParameter("sysDate", new Date())
                .getResultList();
    }

    public Optional<SecurityDeposit> getSecurityDepositByInvoiceId(Long invoiceId) {
        List<SecurityDeposit> resultList = getEntityManager()
                .createNamedQuery("SecurityDeposit.securityDepositsByInvoiceId", SecurityDeposit.class)
                .setParameter("invoiceId", invoiceId)
                .getResultList();
		return !resultList.isEmpty() ? Optional.ofNullable(resultList.get(0)) : Optional.empty();
    }

    public List<SecurityDeposit> checkPeriod(List<Long> securityDeposits) {
        List<SecurityDeposit> securityDepositsToRefund = new ArrayList<SecurityDeposit>();
        for (Long securityDepositId : securityDeposits) {
            SecurityDeposit securityDeposit = findById(securityDepositId);

            if (securityDeposit.getValidityDate() != null) {
                securityDepositsToRefund.add(securityDeposit);
            } else if (securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
                    && securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.DAYS)) {
                if (DateUtils.addDaysToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
                    securityDepositsToRefund.add(securityDeposit);
                }
            } else if (securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
                    && securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.WEEKS)) {
                if (DateUtils.addWeeksToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
                    securityDepositsToRefund.add(securityDeposit);
                }
            } else if (securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
                    && securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.MONTHS)) {
                if (DateUtils.addMonthsToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
                    securityDepositsToRefund.add(securityDeposit);
                }
            } else if (securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
                    && securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.YEARS)) {
                if (DateUtils.addYearsToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
                    securityDepositsToRefund.add(securityDeposit);
                }
            }
        }
        return securityDepositsToRefund;
    }

    public void payInvoices(Long id, SecurityDepositPaymentInput securityDepositPaymentInput) {
        SecurityDeposit securityDeposit = getSecurityDepositOrFail(id);
        RecordedInvoice recordedInvoice = getRecordedInvoiceOrFail(securityDepositPaymentInput.getAccountOperation().getId());
        checkSecurityDepositPaymentAmount(securityDeposit, securityDepositPaymentInput.getAmount(), recordedInvoice);
        checkSecurityDepositSubscription(securityDeposit, recordedInvoice);
        checkSecurityDepositServiceInstance(securityDeposit, recordedInvoice);
        Long securityDepositAOId = createSecurityDepositPaymentAccountOperation(securityDeposit, securityDepositPaymentInput.getAmount());
        matchSecurityDepositPayments(securityDeposit, recordedInvoice, securityDepositAOId, securityDepositPaymentInput.getAmount());
        logPaymentHistory(securityDepositPaymentInput, securityDeposit);

        DebitSecurityDeposit(securityDeposit, securityDepositPaymentInput.getAmount());
        createSecurityDepositTransaction(securityDeposit,
                securityDepositPaymentInput.getAmount(),
                SecurityDepositOperationEnum.PAY_BY_SECURITY_DEPOSIT,
                OperationCategoryEnum.DEBIT,
                recordedInvoice);

        auditLogService.trackOperation("DEBIT", new Date(), securityDeposit, securityDeposit.getCode());

    }

    public Long createSecurityDepositPaymentAccountOperation(SecurityDeposit securityDeposit, BigDecimal amount) {

        OCCTemplate occTemplate = occTemplateService.findByCode(OCC_PAY_SD_TEMPLATE);

        Payment securityDepositPaymentAccountOperation = new Payment();

        paymentService.calculateAmountsByTransactionCurrency(securityDepositPaymentAccountOperation, securityDeposit.getCustomerAccount(),
                amount,null, new Date());

        securityDepositPaymentAccountOperation.setDepositDate(new Date());
        securityDepositPaymentAccountOperation.setPaymentMethod(PaymentMethodEnum.CHECK);
        securityDepositPaymentAccountOperation.setCustomerAccount(securityDeposit.getCustomerAccount());
        securityDepositPaymentAccountOperation.setTransactionCategory(OperationCategoryEnum.CREDIT);
        securityDepositPaymentAccountOperation.setCode("PAY_SD");
        securityDepositPaymentAccountOperation.setCollectionDate(new Date());
        securityDepositPaymentAccountOperation.setMatchingStatus(MatchingStatusEnum.P);
        securityDepositPaymentAccountOperation.setDescription("Payment by security deposit");
        securityDepositPaymentAccountOperation.setType("P");
        securityDepositPaymentAccountOperation.setReference(securityDeposit.getCode());
        securityDepositPaymentAccountOperation.setJournal(occTemplate != null ? occTemplate.getJournal() : null);
        securityDepositPaymentAccountOperation.setTransactionDate(new Date());
        securityDepositPaymentAccountOperation.setAccountingDate(new Date());

        return accountOperationService.createAndReturnReference(securityDepositPaymentAccountOperation);

    }

    private void logPaymentHistory(SecurityDepositPaymentInput securityDepositPaymentInput, SecurityDeposit securityDeposit) {
        paymentHistoryService.addHistory(securityDeposit.getCustomerAccount(),
            		null,
    				null,
                securityDepositPaymentInput.getAmount().multiply(new BigDecimal(100)).longValue(),
    				PaymentStatusEnum.ACCEPTED, null, null,
                "paid by security deposit", null, null,
    				null,null,
                Collections.EMPTY_LIST);
    }

    private void DebitSecurityDeposit(SecurityDeposit securityDeposit, BigDecimal amount) {


        securityDeposit.setStatus(SecurityDepositStatusEnum.UNLOCKED);
        securityDeposit.setCurrentBalance(securityDeposit.getCurrentBalance().subtract(amount));
        update(securityDeposit);
    }

    private void matchSecurityDepositPayments(SecurityDeposit securityDeposit, AccountOperation accountOperation, Long securityDepositAOId, BigDecimal amount)  {

        CustomerAccount customerAccount = securityDeposit.getCustomerAccount();

        List<Long> aosIdsToMatch = new ArrayList<>();
        aosIdsToMatch.add(securityDepositAOId);
        aosIdsToMatch.add(accountOperation.getId());

        try {
            matchingCodeService.matchOperations(customerAccount.getId(), customerAccount.getCode(), aosIdsToMatch, null, MatchingTypeEnum.A, amount);
        } catch (UnbalanceAmountException | NoAllOperationUnmatchedException e) {
            throw new BusinessException(e);
        }


//        match the invoice (AO of the payload) with all Credit accountOperation (Payment) of the current SD.
//    (Only open and partially matched Payment).
//        See PaymentAPI.matchPayment ( )
    }

    private void checkSecurityDepositServiceInstance(SecurityDeposit securityDeposit, RecordedInvoice recordedInvoice) {

        if (securityDeposit.getServiceInstance() != null) {
            List<RatedTransaction> ratedTransactions =  ratedTransactionService.getRatedTransactionsByInvoice(recordedInvoice.getInvoice(), true);
            if(ratedTransactions == null || ratedTransactions.isEmpty())
            {
               throw new InvalidParameterException("All invoices should have the same serviceInstance");
            }

           ratedTransactions.stream()
                    .flatMap(ratedTransaction -> ratedTransaction.getSubscription().getServiceInstances().stream())
                    .filter(serviceInstance -> !securityDeposit.getServiceInstance().equals(serviceInstance))
                    .findAny()
                    .ifPresent(invoice -> {
                        throw new InvalidParameterException("All invoices should have the same serviceInstance");
                    });

        }
    }

    private void checkSecurityDepositSubscription(SecurityDeposit securityDeposit, RecordedInvoice recordedInvoice) {

        if (securityDeposit.getSubscription() != null) {
           List<RatedTransaction> ratedTransactions =  ratedTransactionService.getRatedTransactionsByInvoice(recordedInvoice.getInvoice(), true);
            if(ratedTransactions == null || ratedTransactions.isEmpty())
            {
               throw new InvalidParameterException("All invoices should have the same subscription");
            }

           ratedTransactions.stream()

                    .filter(ratedTransaction -> !securityDeposit.getSubscription().equals(ratedTransaction.getSubscription()))
                    .findAny()
                    .ifPresent(ratedTransaction -> {
                        throw new InvalidParameterException("All invoices should have the same subscription");
                    });

        }

    }


    void checkSecurityDepositPaymentAmount(SecurityDeposit securityDeposit, BigDecimal amount, AccountOperation accountOperation) {
        if (amount.compareTo(securityDeposit.getCurrentBalance()) > 0) {
            throw new InvalidParameterException("The amount to be paid must be less than or equal to the current security deposit balance");
        }

        if (amount.compareTo(accountOperation.getAmount()) > 0 || amount.compareTo(accountOperation.getUnMatchingAmount()) > 0) {
            throw new InvalidParameterException("The amount to be paid must be less than or equal to the unpaid amount of the invoice");
        }


    }

    SecurityDeposit getSecurityDepositOrFail(Long id) {
        SecurityDeposit securityDeposit = findById(id);
        if (securityDeposit == null) {
            throw new EntityDoesNotExistsException("security deposit does not exist.");
        }
        return securityDeposit;
    }

    RecordedInvoice getRecordedInvoiceOrFail(Long id) {
        RecordedInvoice recordedInvoice = recordedInvoiceService.findById(id);
        if (recordedInvoice == null) {
            throw new EntityDoesNotExistsException("account operation does not exist.");
        }
        return recordedInvoice;
    }

    public void createSecurityDeposit(Invoice invoice, SecurityDepositTemplate defaultSDTemplate, Long count) {
        SecurityDeposit securityDeposit = new SecurityDeposit();
        securityDeposit.setTemplate(defaultSDTemplate);
        String securityDepositName = defaultSDTemplate.getTemplateName();
        securityDeposit.setCode(securityDepositName + "-" + count);
        securityDeposit.setAmount(invoice.getAmountWithTax());
        securityDeposit.setStatus(SecurityDepositStatusEnum.VALIDATED);
        securityDeposit.setCustomerAccount(invoice.getBillingAccount().getCustomerAccount());
        securityDeposit.setBillingAccount(invoice.getBillingAccount());
        securityDeposit.setSeller(invoice.getSeller());
        if (providerService.getProvider().getCode() != null) {
            Provider provider = providerService.findByCode(providerService.getProvider().getCode());
            securityDeposit.setCurrency(provider.getCurrency());
        }
        securityDeposit.setSecurityDepositInvoice(invoice);
        create(securityDeposit);
    }

}
