package org.meveo.service.securityDeposit.impl;

import java.math.BigDecimal;

import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.securityDeposit.SecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositOperationEnum;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.securityDeposit.SecurityDepositTransaction;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RefundService;

@Stateless
public class SecurityDepositService extends BusinessService<SecurityDeposit> {
    
    @Inject
    private FinanceSettingsService financeSettingsService;
    
    @Inject
    private ServiceInstanceService serviceInstanceService;
    
    @Inject
    CustomerAccountService customerAccountService;    

    @Inject
    private SecurityDepositTransactionService securityDepositTransactionService;

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
            if(serviceInstance.getSubscription() != null && serviceInstance.getSubscription().getId() != securityDeposit.getSubscription().getId()){
                throw new InvalidParameterException("ServiceInstance must have the same chosen in subscription");
            }
        }
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
}
