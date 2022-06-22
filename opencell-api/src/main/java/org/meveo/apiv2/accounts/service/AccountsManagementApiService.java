package org.meveo.apiv2.accounts.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.ParentInput;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AccountsManagementApiService {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private CustomerService customerService;

    @Inject
    private WalletService walletService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private AuditableFieldService auditableFieldService;

    /**
     * Transfer the subscription from a consumer to an other consumer (UA)
     * 
     * @param subscriptionCode
     * @param consumerInput
     * @param action
     * @return Number of WO / RT updated
     */
    public int transferSubscription(String subscriptionCode, ConsumerInput consumerInput, OpenTransactionsActionEnum action) {

        // Check user account
        if (consumerInput == null || (consumerInput.getConsumerId() == null && StringUtils.isBlank(consumerInput.getConsumerCode()))) {
            throw new ValidationException("At least consumer id or code must be non-null");
        }

        if (consumerInput.getConsumerId() != null && StringUtils.isNotBlank(consumerInput.getConsumerCode())) {
            throw new ValidationException("Only one of parameters can be provided");
        }

        UserAccount newOwner = null;

        if (consumerInput.getConsumerId() != null) {
            newOwner = userAccountService.findById(consumerInput.getConsumerId());
            if (newOwner == null) {
                throw new NotFoundException("user account {id=[id]} doesn't exist".replace("[id]", consumerInput.getConsumerId().toString()));
            }
        }

        if (StringUtils.isNotBlank(consumerInput.getConsumerCode())) {
            newOwner = userAccountService.findByCode(consumerInput.getConsumerCode());
            if (newOwner == null) {
                throw new NotFoundException("user account {code=[code]} doesn't exist".replace("[code]", consumerInput.getConsumerCode()));
            }
        }

        // Check subscription
        Subscription subscription = subscriptionService.findByCode(subscriptionCode, Arrays.asList("userAccount"));

        if (subscription == null) {
            throw new NotFoundException("Subscription {code=[code]} doesn't exist".replace("[code]", subscriptionCode));
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
            throw new ConflictException(
                "Cannot move a terminated subscription {id=[id], code=[code]}".replace("[id]", subscription.getId().toString()).replace("[code]", subscriptionCode));
        }

        String oldUserAccount = subscription.getUserAccount().getCode();

        // Check WalletInstance
        WalletInstance newWallet = walletService.findByUserAccount(newOwner);
        if (newWallet == null) {
            throw new NotFoundException(
                "wallet instance doesn't exist for user account {id=[id], code=[code]}".replace("[id]", newOwner.getId().toString()).replace("[code]", newOwner.getCode()));
        }
        newWallet.setUserAccount(newOwner);// To update lazy attribute

        // Check action
        if (action == OpenTransactionsActionEnum.FAIL) {
            Long countWO = walletOperationService.countNotBilledWOBySubscription(subscription);
            if (countWO > 0) {
                throw new ConflictException("Cannot move subscription {id=[id], code=[code]} with OPEN wallet operations".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode));
            }

            Long countRT = ratedTransactionService.countNotBilledRTBySubscription(subscription);
            if (countRT > 0) {
                throw new ConflictException("Cannot move subscription {id=[id], code=[code]} with OPEN rated operations".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode));
            }
        }

        int count = 0;
        if (action == OpenTransactionsActionEnum.MOVE) {
            count += walletOperationService.moveNotBilledWOToUA(newWallet, subscription);
            count += ratedTransactionService.moveNotBilledRTToUA(newWallet, subscription);
        }

        if (action == OpenTransactionsActionEnum.MOVE_AND_RERATE) {
            count += walletOperationService.moveAndRerateNotBilledWOToUA(newWallet, subscription);
            count += ratedTransactionService.moveAndRerateNotBilledRTToUA(newWallet, subscription);
        }

        // Attache to new user account
        subscription.setUserAccount(newOwner);
        subscriptionService.updateNoCheck(subscription);

        // The change must be logged (audit log + make Subscription.userAccount into auditable field)
        createAuditLog(Subscription.class.getName());
        auditableFieldService.createFieldHistory(subscription, "userAccount", AuditChangeTypeEnum.OTHER, oldUserAccount, newOwner.getCode());

        return count;
    }

    /**
     * Move the customer from a group to another
     * 
     * @param customerAccountCode
     * @param parentInput
     */
    public void changeCustomerAccountParentAccount(String customerAccountCode, ParentInput parentInput) {
        if (parentInput == null || (parentInput.getParentId() == null && Strings.isBlank(parentInput.getParentCode()))) {
            throw new ValidationException("parent account id or code are required for this operation.");
        }

        CustomerAccount customerAccount = null;
        try {
            customerAccount = customerAccountService.findByCode(customerAccountCode, Arrays.asList("paymentMethods"));
            if (Objects.isNull(customerAccount)) {
                Long id = Long.parseLong(customerAccountCode);
                customerAccount = customerAccountService.findById(id, Arrays.asList("paymentMethods"));
                if (Objects.isNull(customerAccount)) {
                    throw new EntityDoesNotExistsException(CustomerAccount.class, id);
                }
            }
        } catch (NumberFormatException e) {
            customerAccount = customerAccountService.findByCode(customerAccountCode, Arrays.asList("paymentMethods"));
        }
        Customer newCustomerParent = parentInput.getParentId() != null ? customerService.findById(parentInput.getParentId(), Arrays.asList("customerAccounts"))
                : customerService.findByCode(parentInput.getParentCode(), Arrays.asList("customerAccounts"));
        if (Objects.isNull(newCustomerParent)) {
            if (parentInput.getParentId() != null) {
                throw new EntityDoesNotExistsException(Customer.class, parentInput.getParentId());
            }
            throw new EntityDoesNotExistsException(Customer.class, parentInput.getParentCode());
        }
        Customer oldCustomerParent = customerService.findById(customerAccount.getCustomer().getId(), Arrays.asList("customerAccounts"));
        customerAccount.setCustomer(newCustomerParent);
        customerAccountService.update(customerAccount);

        customerAccount = customerAccountService.findById(customerAccount.getId(), Arrays.asList("billingAccounts"));
        customerAccount.getBillingAccounts().stream()
            .map(billingAccount -> customerAccountService.getEntityManager().createNamedQuery("WalletOperation.listOpenWOsToRateByBA", WalletOperation.class)
                .setParameter("billingAccount", billingAccount).getResultList())
            .flatMap(List::stream).peek(walletOperation -> walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE))
            .forEach(walletOperation -> walletOperationService.update(walletOperation));

        auditableFieldService.createFieldHistory(customerAccount, "customer", AuditChangeTypeEnum.OTHER, newCustomerParent.getCode(), oldCustomerParent.getCode());
        createAuditLog(CustomerAccount.class.getName());
        log.info("the parent customer for the customer account {}, changed from {} to {}", customerAccount.getCode(), oldCustomerParent.getCode(), newCustomerParent.getCode());
    }

    private void createAuditLog(String entity) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActor(currentUser.getUserName());
        auditLog.setCreated(new Date());
        auditLog.setEntity(entity);
        auditLog.setOrigin("API");
        auditLog.setAction("update");
        auditLogService.create(auditLog);
    }
}
