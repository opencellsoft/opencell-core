package org.meveo.service.securityDeposit.impl;

import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.PaymentException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.securityDeposit.SecurityDepositCancelInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;

import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentErrorEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.Refund;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.SecurityDepositTransaction;
import org.meveo.model.securityDeposit.ValidityPeriodUnit;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RefundService;

@Stateless
public class SecurityDepositService extends BusinessService<SecurityDeposit> {

    @Inject
    private AuditLogService auditLogService;
    
    @Inject
    private FinanceSettingsService financeSettingsService;
    
    @Inject
    private ServiceInstanceService serviceInstanceService;
    
    @Inject
    CustomerAccountService customerAccountService;    
   
    @Inject
    private PaymentService paymentService;
    
    @Inject
    private PaymentGatewayService paymentGatewayService;
     
    @Inject
    private SecurityDepositTransactionService securityDepositTransactionService;
    
    @Inject
    private RefundService refundService;
    
    @Inject
    private SecurityDepositService securityDepositService;
    
    
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
    
    public void checkParameters(SecurityDeposit securityDeposit,SecurityDepositInput securityDepositInput, BigDecimal oldAmountSD)
    {
        FinanceSettings financeSettings = financeSettingsService.findLastOne();
        if(securityDeposit.getCurrency() == null)
            throw new EntityDoesNotExistsException("currency does not exist.");
        if(!financeSettings.isAutoRefund() && (securityDepositInput.getValidityDate() != null || securityDepositInput.getValidityPeriod() != null || securityDepositInput.getValidityPeriodUnit() != null))
            throw new InvalidParameterException("the option 'Allow auto refund' need to be checked");
        if(!SecurityDepositStatusEnum.NEW.equals(securityDeposit.getStatus()) && !SecurityDepositStatusEnum.HOLD.equals(securityDeposit.getStatus()))
            securityDeposit.setAmount(oldAmountSD);
        if(securityDeposit.getServiceInstance() != null && securityDeposit.getSubscription() != null){           
            ServiceInstance serviceInstance = serviceInstanceService.retrieveIfNotManaged(securityDeposit.getServiceInstance());
            if(serviceInstance.getSubscription() != null && !serviceInstance.getSubscription().getId().equals(securityDeposit.getSubscription().getId())){
                throw new InvalidParameterException("ServiceInstance must have the same chosen in subscription");
            }
        }
    }

    public void refund(SecurityDeposit securityDepositToUpdate, String refundReason, SecurityDepositOperationEnum securityDepositOperationEnum, SecurityDepositStatusEnum securityDepositStatusEnum)
    {
        Refund refund = createRefund(securityDepositToUpdate);
        if(refund == null){
            throw new BusinessException("Cannot create Refund.");
        }
        else{        
            createSecurityDepositTransaction(securityDepositToUpdate, securityDepositToUpdate.getCurrentBalance(), 
            		securityDepositOperationEnum, OperationCategoryEnum.DEBIT, refund); 
    
            securityDepositToUpdate.setRefundReason(refundReason);
            securityDepositToUpdate.setStatus(securityDepositStatusEnum);
            securityDepositToUpdate.setCurrentBalance(new BigDecimal(0));
            update(securityDepositToUpdate);
            auditLogService.trackOperation("REFUND", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
        }
    }
    
    public void cancel(SecurityDeposit securityDepositToUpdate, SecurityDepositCancelInput securityDepositInput)
    {
        Refund refund = createRefund(securityDepositToUpdate);        
        if(refund == null){
            throw new BusinessException("Cannot create Refund.");
        }
        else{
            createSecurityDepositTransaction(securityDepositToUpdate, securityDepositToUpdate.getCurrentBalance(), 
                SecurityDepositOperationEnum.CANCEL_SECURITY_DEPOSIT, OperationCategoryEnum.DEBIT, refund); 

            securityDepositToUpdate.setRefundReason(securityDepositInput.getCancelReason());
            securityDepositToUpdate.setStatus(SecurityDepositStatusEnum.CANCELED);
            securityDepositToUpdate.setCurrentBalance(new BigDecimal(0));
            update(securityDepositToUpdate);
            auditLogService.trackOperation("CANCEL", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
        }
    }


    private Refund createRefund(SecurityDeposit securityDepositToUpdate) {
        securityDepositToUpdate = retrieveIfNotManaged(securityDepositToUpdate);
        long amountToPay = securityDepositToUpdate.getCurrentBalance().longValue();
        List<Long> accountOperationsToPayIds = new ArrayList<Long>();
        CustomerAccount customerAccount = securityDepositToUpdate.getCustomerAccount();
        
        if(customerAccount == null){
            throw new EntityDoesNotExistsException("Customer Account = null");        
        }
        PaymentMethod preferredPaymentMethod = customerAccountService.getPreferredPaymentMethod(customerAccount.getId());
        if(preferredPaymentMethod == null){
            throw new EntityDoesNotExistsException("Customer Account [" + customerAccount.getCode() + "] Preferred Payment Method = null");
        }
        List<SecurityDepositTransaction> listSecurityDepositTransaction = securityDepositTransactionService.getSecurityDepositTransactionBySecurityDepositId(securityDepositToUpdate.getId());
        for (int i = 0; i < listSecurityDepositTransaction.size(); i++) {
            SecurityDepositTransaction securityDepositTransaction = listSecurityDepositTransaction.get(i);
            AccountOperation aOSecurityDepositTransaction = securityDepositTransaction.getAccountOperation();
            if(OperationCategoryEnum.CREDIT.equals(aOSecurityDepositTransaction.getTransactionCategory())) {
                accountOperationsToPayIds.add(aOSecurityDepositTransaction.getId());
            }            
        }
        
        Long refundId = null;        
        PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, preferredPaymentMethod, null);
        if(paymentGateway == null) {
            throw new PaymentException(PaymentErrorEnum.NO_PAY_GATEWAY_FOR_CA, "No payment gateway for customerAccount:" + customerAccount.getCode());
        } 
        
        refundId = doPayment(amountToPay, accountOperationsToPayIds, customerAccount, preferredPaymentMethod, refundId, paymentGateway);
        if(refundId == null){
            return null;
        }
        else{
            return refundService.findById(refundId);
        }
    }
    
    
    private Long doPayment(long amountToPay, List<Long> accountOperationsToPayIds, CustomerAccount customerAccount, PaymentMethod preferredPaymentMethod, Long refundId,
            PaymentGateway paymentGateway) {
        if(paymentGateway!=null && (preferredPaymentMethod.getPaymentType().equals(DIRECTDEBIT) || preferredPaymentMethod.getPaymentType().equals(CARD))) {
            try {
                if(!accountOperationsToPayIds.isEmpty()) {
                    if(preferredPaymentMethod.getPaymentType().equals(CARD)) {
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
    
    public void credit(SecurityDeposit securityDepositToUpdate, SecurityDepositCreditInput securityDepositInput)
    {
        securityDepositToUpdate = retrieveIfNotManaged(securityDepositToUpdate);
        CustomerAccount customerAccount = securityDepositToUpdate.getCustomerAccount();
        
        if(customerAccount == null){
            throw new EntityDoesNotExistsException("Cannot find customer account in the this Security Deposit");
        }
        
        if(securityDepositToUpdate.getCurrentBalance() == null){
            securityDepositToUpdate.setCurrentBalance(BigDecimal.ZERO);
        }
        BigDecimal nCurrentBalance = securityDepositInput.getAmountToCredit().add(securityDepositToUpdate.getCurrentBalance());
        securityDepositToUpdate.setCurrentBalance(nCurrentBalance);
        
        if (securityDepositToUpdate.getTemplate().getMaxAmount() != null) {
            BigDecimal maxAmount = securityDepositToUpdate.getTemplate().getMaxAmount();
            if (nCurrentBalance.compareTo(maxAmount) > 0) {
                throw new EntityDoesNotExistsException("The Current Balance + Amount to Credit must be less than or equal to the Maximum Amount of the Template.");
            }
        }
        
        if(securityDepositToUpdate.getAmount() != null && (SecurityDepositStatusEnum.NEW.equals(securityDepositToUpdate.getStatus()) 
                || SecurityDepositStatusEnum.HOLD.equals(securityDepositToUpdate.getStatus()))){
            BigDecimal nAmount = securityDepositToUpdate.getAmount().add(securityDepositInput.getAmountToCredit().negate());            
            if(nAmount.compareTo(BigDecimal.ZERO) <= 0) {
                securityDepositToUpdate.setAmount(null);
                securityDepositToUpdate.setStatus(SecurityDepositStatusEnum.LOCKED);
            }
            else {
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

	public List<SecurityDeposit> checkPeriod(List<Long> securityDeposits) {
		List<SecurityDeposit> securityDepositsToRefund = new ArrayList<SecurityDeposit>();
		for (Long securityDepositId : securityDeposits) {
			SecurityDeposit securityDeposit = securityDepositService.findById(securityDepositId);
			
			if (securityDeposit.getValidityDate() != null) {
				securityDepositsToRefund.add(securityDeposit);
			}else if(securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
					&& securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.DAYS) ) {
				if(DateUtils.addDaysToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
					securityDepositsToRefund.add(securityDeposit);
				}
			}else if(securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
					&& securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.WEEKS) ) {
				if(DateUtils.addWeeksToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
					securityDepositsToRefund.add(securityDeposit);
				}
			}else if(securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
					&& securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.MONTHS) ) {
				if(DateUtils.addMonthsToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
					securityDepositsToRefund.add(securityDeposit);
				}
			}else if(securityDeposit.getValidityPeriod() == null && securityDeposit.getValidityPeriodUnit() == null
					&& securityDeposit.getValidityPeriodUnit().equals(ValidityPeriodUnit.YEARS) ) {
				if(DateUtils.addYearsToDate(securityDeposit.getAuditable().getCreated(), securityDeposit.getValidityPeriod()).after(new Date())) {
					securityDepositsToRefund.add(securityDeposit);
				}
			}
		}
		return securityDepositsToRefund;
	}
}
