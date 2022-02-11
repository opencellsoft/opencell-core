package org.meveo.service.securityDeposit.impl;

import java.math.BigDecimal;

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
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositRefundInput;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.SecurityDepositTransaction;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;

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
    private AccountOperationService accountOperationService;
    
    @Inject
    private SecurityDepositTransactionService securityDepositTransactionService;    
    
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
            if(serviceInstance.getSubscription() != null && serviceInstance.getSubscription().getId() != securityDeposit.getSubscription().getId()){
                throw new InvalidParameterException("ServiceInstance must have the same chosen in subscription");
            }
        }
    }
    
    public void refund(SecurityDeposit securityDepositToUpdate, SecurityDepositRefundInput securityDepositInput)
    {
        securityDepositToUpdate = retrieveIfNotManaged(securityDepositToUpdate);
        long amountToPay = securityDepositToUpdate.getCurrentBalance().longValue();
        List<Long> accountOperationsToPayIds = new ArrayList<Long>();
        CustomerAccount customerAccount = securityDepositToUpdate.getCustomerAccount();
        
        if(customerAccount == null)
            throw new EntityDoesNotExistsException("Customer Account = null");        
        PaymentMethod preferredPaymentMethod = customerAccountService.getPreferredPaymentMethod(customerAccount.getId());
        if(preferredPaymentMethod == null)
            throw new EntityDoesNotExistsException("Customer Account [" + customerAccount.getCode() + "] Preferred Payment Method = null");
        
        List<SecurityDepositTransaction> listSecurityDepositTransaction = securityDepositTransactionService.getSecurityDepositTransactionBySecurityDepositId(securityDepositToUpdate.getId());//securityDepositId
        for (int i = 0; i < listSecurityDepositTransaction.size(); i++) {
            SecurityDepositTransaction securityDepositTransaction = listSecurityDepositTransaction.get(i);
            AccountOperation aOSecurityDepositTransaction = securityDepositTransaction.getAccountOperation();
            if(OperationCategoryEnum.CREDIT.equals(aOSecurityDepositTransaction.getTransactionCategory())) {
                accountOperationsToPayIds.add(listSecurityDepositTransaction.get(i).getId());
            }            
        }
        
        PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, preferredPaymentMethod, null);
        if(paymentGateway!=null && (preferredPaymentMethod.getPaymentType().equals(DIRECTDEBIT) || preferredPaymentMethod.getPaymentType().equals(CARD))) {
            try {
                if(accountOperationsToPayIds != null && !accountOperationsToPayIds.isEmpty()) {
                    if(preferredPaymentMethod.getPaymentType().equals(CARD)) {
                        if (preferredPaymentMethod instanceof HibernateProxy) {
                            preferredPaymentMethod = (PaymentMethod) ((HibernateProxy) preferredPaymentMethod).getHibernateLazyInitializer()
                                    .getImplementation();
                        }
                        CardPaymentMethod paymentMethod = (CardPaymentMethod) preferredPaymentMethod;
                        paymentService.refundByCard(customerAccount, amountToPay, paymentMethod.getCardNumber(),
                            paymentMethod.getCardNumber(), paymentMethod.getHiddenCardNumber(),
                            paymentMethod.getExpirationMonthAndYear(), paymentMethod.getCardType(), accountOperationsToPayIds,
                            true, true, paymentGateway);
                    } else {
                        paymentService.refundByMandat(customerAccount, amountToPay, accountOperationsToPayIds, true, true, paymentGateway);
                    }
                }
            } catch (Exception exception) {
                throw new BusinessException("Error occurred during payment process for customer " + customerAccount.getCode(), exception);
            }
        }        
        
        SecurityDepositTransaction securityDepositTransaction = new SecurityDepositTransaction();
        securityDepositTransaction.setAmount(securityDepositToUpdate.getCurrentBalance());
        securityDepositTransaction.setTransactionCategory(OperationCategoryEnum.DEBIT);
        securityDepositTransaction.setOperation(SecurityDepositOperationEnum.REFUND_SECURITY_DEPOSIT);
        securityDepositTransaction.setSecurityDeposit(securityDepositToUpdate);
        AccountOperation aO = new AccountOperation();
        aO.setTransactionCategory(OperationCategoryEnum.DEBIT);
        aO.setTransactionDate(new Date());      
        accountOperationService.create(aO);
        securityDepositTransaction.setAccountOperation(aO);
        securityDepositTransactionService.create(securityDepositTransaction);        
        securityDepositToUpdate.setRefundReason(securityDepositInput.getRefundReason());
        securityDepositToUpdate.setStatus(SecurityDepositStatusEnum.REFUNDED);
        securityDepositToUpdate.setCurrentBalance(new BigDecimal(0));
        update(securityDepositToUpdate);
        auditLogService.trackOperation("REFUND", new Date(), securityDepositToUpdate, securityDepositToUpdate.getCode());
    }
}
