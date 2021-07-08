package org.meveo.apiv2.account;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AccountManagementService {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private CustomerService customerService;

    @Inject
    private WalletOperationService walletOperationService;

    public void changeCustomerAccountParentAccount(String customerAccountCodeOrId, String id, String code) {
        if(Strings.isBlank(id) && Strings.isBlank(code)){
            throw new InvalidParameterException("parent account id or code are required for this operation.");
        }
        CustomerAccount customerAccount = null;
        try{
            long customerAccountId = Long.parseLong(customerAccountCodeOrId);
            customerAccount = customerAccountService.findById(customerAccountId, Arrays.asList("paymentMethods"));
        }catch (NumberFormatException e){
            customerAccount = customerAccountService.findByCode(customerAccountCodeOrId, Arrays.asList("paymentMethods"));
        }
        if(Objects.isNull(customerAccount)){
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCodeOrId);
        }
        Customer newCustomerParent = !Strings.isBlank(id) ? customerService.findById(Long.parseLong(id), Arrays.asList("customerAccounts")) : customerService.findByCode(code, Arrays.asList("customerAccounts"));
        if(Objects.isNull(newCustomerParent)){
            if(!Strings.isBlank(id)){
                 throw new EntityDoesNotExistsException(Customer.class, id);
            }
            throw new EntityDoesNotExistsException(Customer.class, code);
        }
        Customer oldCustomerParent = customerService.findById(customerAccount.getCustomer().getId(), Arrays.asList("customerAccounts"));
        customerAccount.setCustomer(newCustomerParent);
        customerAccountService.update(customerAccount);

        customerAccount = customerAccountService.findById(customerAccount.getId(), Arrays.asList("billingAccounts"));
        customerAccount.getBillingAccounts().stream()
                .map(billingAccount -> customerAccountService.getEntityManager()
                        .createNamedQuery("WalletOperation.listOpenWOsToRateByBA", WalletOperation.class)
                        .setParameter("billingAccount", billingAccount)
                        .getResultList())
                .flatMap(List::stream)
                .peek(walletOperation -> walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE))
                .forEach(walletOperation -> walletOperationService.update(walletOperation));

        log.info("the parent customer for the customer account {}, changed from {} to {}", customerAccount.getCode(), oldCustomerParent.getCode(), newCustomerParent.getCode());
    }
}
