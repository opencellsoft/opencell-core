package org.meveo.apiv2.accounts.service;

import static java.util.Optional.ofNullable;
import static org.meveo.apiv2.accounts.ApplyOneShotChargeListModeEnum.PROCESS_ALL;
import static org.meveo.apiv2.accounts.ApplyOneShotChargeListModeEnum.ROLLBACK_ON_ERROR;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.job.DateRange;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.accounts.AppliedChargeResponseDto;
import org.meveo.apiv2.accounts.AppliedChargeResponseDto.CdrError;
import org.meveo.apiv2.accounts.ApplyOneShotChargeListInput;
import org.meveo.apiv2.accounts.ApplyOneShotChargeListModeEnum;
import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.CounterInstanceDto;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.ParentInput;
import org.meveo.apiv2.accounts.ProcessApplyChargeListResult;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateMappingService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
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
    private BillingAccountService billingAccountService;

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
    private CounterTemplateService counterTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;
    
    @Inject
    private UsageChargeInstanceService usageChargeInstanceService;
    
    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;
    
    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;
    
    @Inject
    private ProductChargeTemplateMappingService productChargeTemplateMappingService;

    @Inject
    private AccountsManagementApiService thisNewTX;
    
    @Inject
    private SubscriptionApi subscriptionApi;

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    AccountingArticleService accountingArticleService;
    
    @Inject
    private FinanceSettingsService financeSettingsService;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

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
        }else if (StringUtils.isNotBlank(consumerInput.getConsumerCode())) {
            newOwner = userAccountService.findByCode(consumerInput.getConsumerCode());
            if (newOwner == null) {
                throw new NotFoundException("user account {code=[code]} doesn't exist".replace("[code]", consumerInput.getConsumerCode()));
            }
        }

		if(!newOwner.getIsConsumer()) {
            throw new BusinessApiException("UserAccount: " + newOwner.getCode() + " is not a consumer. Subscription transfer to this user account is not allowed.");
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
        
        if (action == OpenTransactionsActionEnum.FAIL_DRAFT) {
            Long countRTDraft = ratedTransactionService.countRTBySubscriptionForDraftInvoice(subscription);
            if (countRTDraft > 0) {
                throw new ConflictException("Cannot move subscription {id=[id], code=[code]} with rated items on DRAFT invoices".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode));
            }
        }
        
        if (action == OpenTransactionsActionEnum.FAIL_OPEN_AND_DRAFT) {
            Long countRT = ratedTransactionService.countNotBilledRTBySubscription(subscription);
            if (countRT > 0) {
                throw new ConflictException("Cannot move subscription {id=[id], code=[code]} with OPEN rated items".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode));
            }
            
            Long countRTDraft = ratedTransactionService.countRTBySubscriptionForDraftInvoice(subscription);
            if (countRTDraft > 0) {
                throw new ConflictException("Cannot move subscription {id=[id], code=[code]} with rated items on DRAFT invoices".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode));
            }
        }

        // Attache to new user account
        subscription.setUserAccount(newOwner);
        subscriptionService.updateNoCheck(subscription);
        
        var usageServiceInstance = usageChargeInstanceService.findUsageChargeInstanceBySubscriptionId(subscription.getId());
        changeToNewUserAccount(usageServiceInstance, newOwner);
        var oneshotServiceInstance = oneShotChargeInstanceService.findOneShotChargeInstancesBySubscriptionId(subscription.getId());
        changeToNewUserAccount(oneshotServiceInstance, newOwner);
        var recurringServiceInstance = recurringChargeInstanceService.findRecurringChargeInstanceBySubscriptionId(subscription.getId());
        changeToNewUserAccount(recurringServiceInstance, newOwner);

        // The change must be logged (audit log)
        createAuditLog(Subscription.class.getName());
        return count;
    }
    
    private void changeToNewUserAccount(List<? extends ChargeInstance> serviceInstances, UserAccount newUserAccount) {
        if(!CollectionUtils.isEmpty(serviceInstances)) {
        	for (ChargeInstance chargeInstance : serviceInstances) {
        			chargeInstance.setUserAccount(newUserAccount);
        			chargeInstanceService.update(chargeInstance);
			}
        			
        }
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
        
		Customer newCustomerParent = parentInput.getParentId() != null
				? customerService.findById(parentInput.getParentId(), Arrays.asList("customerAccounts"))
				: customerService.findByCode(parentInput.getParentCode(), Arrays.asList("customerAccounts"));
        
        if (Objects.isNull(newCustomerParent)) {
            if (parentInput.getParentId() != null) {
                throw new EntityDoesNotExistsException(Customer.class, parentInput.getParentId());
            }
            throw new EntityDoesNotExistsException(Customer.class, parentInput.getParentCode());
        }
        
        if (parentInput.getParentId() != null && Strings.isNotBlank(parentInput.getParentCode())) {
        	if (!newCustomerParent.getCode().equals(parentInput.getParentCode())) {
        		throw new ValidationException("Provided parentId and parentCode do not point to the same Customer");
        	}
        }
        
        Customer oldCustomerParent = customerService.findById(customerAccount.getCustomer().getId(), Arrays.asList("customerAccounts"));
        customerAccount.setCustomer(newCustomerParent);
        customerAccountService.update(customerAccount);

        customerAccount = customerAccountService.findById(customerAccount.getId(), Arrays.asList("billingAccounts"));

        createAuditLog(CustomerAccount.class.getName());
        log.info("the parent customer for the customer account {}, changed from {} to {}", customerAccount.getCode(), oldCustomerParent.getCode(), newCustomerParent.getCode());

        if (parentInput.getMarkOpenWalletOperationsToRerate()) {
			boolean isEntityWithHugeVolume = financeSettingsService.isEntityWithHugeVolume("WalletOperation");
			if (isEntityWithHugeVolume) {
				throw new BusinessApiException("WalletOperation entity is defined as a huge entity. Automatic rerating is forbidden. Please, use other channels to rerate.");
			}
			customerAccount.getBillingAccounts().stream()
					.map(billingAccount -> customerAccountService.getEntityManager()
							.createNamedQuery("WalletOperation.listOpenWOsToRateByBA", WalletOperation.class)
							.setParameter("billingAccount", billingAccount).getResultList())
					.flatMap(List::stream)
					.peek(walletOperation -> walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE))
					.forEach(walletOperation -> walletOperationService.update(walletOperation));
		}

    }

    /**
     * Move the customer account from a group to another
     * 
     * @param billingAccountCode
     * @param parentInput
     */
    public void changeBillingAccountParentAccount(String billingAccountCode, ParentInput parentInput) {
        if (parentInput == null || (parentInput.getParentId() == null && Strings.isBlank(parentInput.getParentCode()))) {
            throw new ValidationException("parent account id or code are required for this operation.");
        }

        BillingAccount billingAccount = null;
        try {
            billingAccount = billingAccountService.findByCode(billingAccountCode);
            if (Objects.isNull(billingAccount)) {
                Long id = Long.parseLong(billingAccountCode);
                billingAccount = billingAccountService.findById(id);
                if (Objects.isNull(billingAccount)) {
                    throw new EntityDoesNotExistsException(BillingAccount.class, id);
                }
            }
        } catch (NumberFormatException e) {
        	throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }
        
		CustomerAccount newCustomerAccountParent = parentInput.getParentId() != null
				? customerAccountService.findById(parentInput.getParentId(), Arrays.asList("billingAccounts"))
				: customerAccountService.findByCode(parentInput.getParentCode(), Arrays.asList("billingAccounts"));
        
        if (Objects.isNull(newCustomerAccountParent)) {
            if (parentInput.getParentId() != null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, parentInput.getParentId());
            }
            throw new EntityDoesNotExistsException(CustomerAccount.class, parentInput.getParentCode());
        }
        
        if (parentInput.getParentId() != null && Strings.isNotBlank(parentInput.getParentCode())) {
        	if (!newCustomerAccountParent.getCode().equals(parentInput.getParentCode())) {
        		throw new ValidationException("Provided parentId and parentCode do not point to the same Customer Account");
        	}
        }
        
        CustomerAccount oldCustomerAccountParent = customerAccountService.findById(billingAccount.getCustomerAccount().getId(), Arrays.asList("billingAccounts"));
        billingAccount.setCustomerAccount(newCustomerAccountParent);
        billingAccount.setPaymentMethod(null);
        billingAccountService.update(billingAccount);

        billingAccount = billingAccountService.findById(billingAccount.getId());
        subscriptionService.removePaymentMethodLink(billingAccount);

        createAuditLog(BillingAccount.class.getName());
        log.info("the parent customer account for the billing account {}, changed from {} to {}", billingAccount.getCode(), oldCustomerAccountParent.getCode(), newCustomerAccountParent.getCode());

        if (parentInput.getMarkOpenWalletOperationsToRerate()) {
			boolean isEntityWithHugeVolume = financeSettingsService.isEntityWithHugeVolume("WalletOperation");
			if (isEntityWithHugeVolume) {
				throw new BusinessApiException("WalletOperation entity is defined as a huge entity. Automatic rerating is forbidden. Please, use other channels to rerate.");
			}
			customerAccountService.getEntityManager()
					.createNamedQuery("WalletOperation.listOpenWOsToRateByBA", WalletOperation.class)
					.setParameter("billingAccount", billingAccount).getResultList().stream()
					.peek(walletOperation -> walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE))
					.forEach(walletOperation -> walletOperationService.update(walletOperation));
		}

    }

    public List<Long> createCounterInstance(CounterInstanceDto dto) {
        CounterTemplate counterTemplate = checkFieldsAndGetCounterTemplate(dto);

        List<Long> createdCounterInstances = new ArrayList<>();

        List<ServiceInstance> serviceInstances = getServiceInstances(dto);

        // si level SI, on boucle et on traite pour chaque serviceInstance retrouve
        // si level subscritpion : on traite le premier en respectant un critere de date a voir
        if (serviceInstances.size() > 1 && CounterTemplateLevel.SU == counterTemplate.getCounterLevel()) {
            serviceInstances.sort(Comparator.comparing(ServiceInstance::getStatusDate));
            processCreateCounterInstanceAndPeriod(dto, serviceInstances.get(0), counterTemplate, createdCounterInstances);
        } else {
            serviceInstances.forEach(si -> processCreateCounterInstanceAndPeriod(dto, si, counterTemplate, createdCounterInstances));
        }

        return createdCounterInstances;

    }

    public List<Long> updateCounterInstance(Long id, CounterInstanceDto dto) {
        CounterInstance counterInstance = counterInstanceService.findById(id);

        if (counterInstance == null) {
            throw new EntityDoesNotExistsException("No CounterInstance found with id : " + id);
        }

        CounterTemplate counterTemplate = checkFieldsAndGetCounterTemplate(dto);

        List<Long> updatedCounterInstances = new ArrayList<>();

        List<ServiceInstance> serviceInstances = getServiceInstances(dto);

        // si level SI, on boucle et on traite pour chaque serviceInstance retrouve
        // si level subscritpion : on traite le premier en respectant un critere de date a voir
        if (serviceInstances.size() > 1 && CounterTemplateLevel.SU == counterTemplate.getCounterLevel()) {
            serviceInstances.sort(Comparator.comparing(ServiceInstance::getStatusDate));
            processUpdateCounterInstanceAndPeriod(counterInstance, dto, serviceInstances.get(0), counterTemplate);
        } else {
            serviceInstances.forEach(si -> processUpdateCounterInstanceAndPeriod(counterInstance, dto, si, counterTemplate));
        }

        updatedCounterInstances.add(counterInstance.getId());

        return updatedCounterInstances;

    }

    private List<ServiceInstance> getServiceInstances(CounterInstanceDto dto) {
        List<ServiceInstance> serviceInstances = serviceInstanceService.findByCodeAndCodeSubscription(dto.getProductCode(), dto.getSubscriptionCode());

        if (CollectionUtils.isEmpty(serviceInstances)) {
            throw new EntityDoesNotExistsException("No service instance found for [product code=" + dto.getProductCode() + ", subscription code=" + dto.getSubscriptionCode() + "]");
        }
        return new ArrayList<>(serviceInstances); // to avoid UnsupportedOperationException for sort bellow
    }

    private CounterTemplate checkFieldsAndGetCounterTemplate(CounterInstanceDto dto) {
        if (StringUtils.isBlank(dto.getCounterTemplateCode())) {
            throw new BusinessApiException("CounterTemplate code is mandatory");
        }

        if (StringUtils.isBlank(dto.getProductCode())) {
            throw new BusinessApiException("Product code is mandatory");
        }

        if (StringUtils.isBlank(dto.getSubscriptionCode())) {
            throw new BusinessApiException("Subscription code is mandatory");
        }

        if (StringUtils.isBlank(dto.getChargeInstanceCode())) {
            throw new BusinessApiException("Charges are mandatory");
        }

        CounterTemplate counterTemplate = counterTemplateService.findByCode(dto.getCounterTemplateCode());

        if (counterTemplate == null) {
            throw new EntityDoesNotExistsException("No CounterTemplate found with code : " + dto.getCounterTemplateCode());
        }

        // verifier que le counterTemplate.level match avec les param du payload
        switch (counterTemplate.getCounterLevel()) {
            case CA:
                if (StringUtils.isBlank(dto.getCustomerAccountCode())) {
                    throw new BusinessApiException("CustomerAccount code is mandatory");
                }
                break;
            case BA:
                if (StringUtils.isBlank(dto.getBillingAccountCode())) {
                    throw new BusinessApiException("BillingAccount code is mandatory");
                }
                break;
            case UA:
                if (StringUtils.isBlank(dto.getUserAccountCode())) {
                    throw new BusinessApiException("UserAccount code is mandatory");
                }
                break;
        }

        // Period validations
        if (CollectionUtils.isNotEmpty(dto.getCounterPeriods())) {
            dto.getCounterPeriods().forEach(periodDto -> {
                if(StringUtils.isBlank(periodDto.getCode())) {
                    throw new BusinessApiException("Period code is mandatory");
                }

                if (periodDto.getStartDate() == null || periodDto.getEndDate() == null) {
                    throw new BusinessApiException("Period Start and End date are mandatory");
                }
            });
        }
        return counterTemplate;
    }

    private void processCreateCounterInstanceAndPeriod(CounterInstanceDto dto, ServiceInstance si, CounterTemplate counterTemplate, List<Long> createdCounterInstances) {
        ChargeInstance charge = getAndValidateChargeWithProduct(dto, si, counterTemplate, dto.getChargeInstanceCode());
        CounterInstance counterInstance = counterInstanceService.counterInstanciationWithoutForceCommit(si, counterTemplate, charge, false);
        createdCounterInstances.add(counterInstance.getId());
        validateAndProcessCounterPeriods(dto, counterInstance, charge);
    }

    private void processUpdateCounterInstanceAndPeriod(CounterInstance counterInstance, CounterInstanceDto dto, ServiceInstance si, CounterTemplate counterTemplate) {
        ChargeInstance charge = getAndValidateChargeWithProduct(dto, si, counterTemplate, dto.getChargeInstanceCode());
        validateAndProcessCounterPeriods(dto, counterInstance, charge);
    }

    private void validateAndProcessCounterPeriods(CounterInstanceDto dto, CounterInstance counterInstance, ChargeInstance charge) {
        // Check and build period
        List<DateRange> periodes = new ArrayList<>();

        Optional.ofNullable(dto.getCounterPeriods()).orElse(Collections.emptySet()).forEach(periodDto -> {
            Date startP = DateUtils.setTimeToZero(periodDto.getStartDate());
            Date endP = DateUtils.setTimeToZero(periodDto.getEndDate());
            if (endP != null && startP != null && endP.before(startP)) {
                throw new BusinessApiException("Invalid period dates : Start must be before End [start=" + formatDate(startP) + " - end=" + formatDate(endP) + "]");
            }

            // check period cheauvauchement
            periodes.forEach(dateRange -> {
                Date start = DateUtils.setTimeToZero(dateRange.getFrom());
                Date end = DateUtils.setTimeToZero(dateRange.getTo());
                if (((start.before(startP) || start.equals(startP)) && end.after(startP) && (end.before(endP) || end.equals(endP)))
                        || (((start.after(startP) || start.equals(startP)) && start.before(endP)) && (end.after(endP) || end.equals(endP)))
                        || (start.equals(startP) && end.equals(endP))
                        || (start.before(startP) && end.after(startP) && end.after(endP))
                        || (start.after(startP) && end.after(startP) && end.before(endP))
                ) {
                    throw new BusinessApiException("No overlapping should occur between counter Date Periods : [start=" + formatDate(startP) + " - end=" + formatDate(endP) + "]" +
                            " and [start=" + formatDate(start) + " - end=" + formatDate(end) + "]");
                }
            });

            // fetch existing counterPeriod to check overlapping
            CounterPeriod existingCounterPeriod = counterInstanceService.getCounterPeriodByDate(counterInstance, startP);

            if (existingCounterPeriod == null) {
                counterInstanceService.createPeriod(counterInstance, charge.getChargeDate(), periodDto.getStartDate(), charge, periodDto.getValue(), periodDto.getLevel(), false, periodDto.getEndDate(), true);
            } else {
                counterInstanceService.updatePeriod(existingCounterPeriod, counterInstance, charge.getChargeDate(), periodDto.getStartDate(), charge, periodDto.getValue(), periodDto.getLevel(), periodDto.getEndDate(), true);
            }

            periodes.add(new DateRange(startP, endP));
        });
    }

    private ChargeInstance getAndValidateChargeWithProduct(CounterInstanceDto dto, ServiceInstance si, CounterTemplate counterTemplate, String s) {
        ChargeInstance charge = chargeInstanceService.findByCode(s);

        if (charge == null) {
            throw new EntityDoesNotExistsException("No ChargeInstance found with code : " + dto.getCounterTemplateCode());
        }

        if (!productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(dto.getProductCode(), charge.getCode(), counterTemplate.getCode())) {
            throw new BusinessApiException("ChargeInstance with [type=" + charge.getChargeType() + ", code=" + charge.getCode()
                    + "] is not linked to Product [code=" + si.getCode()
                    + "] and CounterTemplate [code=" + counterTemplate.getCode() + "]");
        }
        return charge;
    }

    private String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
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

	public ProcessApplyChargeListResult applyOneShotChargeList(ApplyOneShotChargeListInput postData) {
		
		if(postData == null) {
			throw new InvalidParameterException("The input parameters are required");
		}

		if(ListUtils.isEmtyCollection(postData.getChargesToApply())) {
			throw new InvalidParameterException("The charges to apply are required");
		}
		
		postData.getChargesToApply().forEach(c -> c.setGenerateRTs(postData.isGenerateRTs()));

		SynchronizedIterator<ApplyOneShotChargeInstanceRequestDto> syncCharges = new SynchronizedIterator<>(postData.getChargesToApply());
		
		ProcessApplyChargeListResult result = new ProcessApplyChargeListResult(postData.getMode(), syncCharges.getSize());
		
		int nbThreads = (postData.getMode() == PROCESS_ALL) ? Runtime.getRuntime().availableProcessors() : 1;
        if (nbThreads > postData.getChargesToApply().size()) {
            nbThreads = postData.getChargesToApply().size();
        }
		
		List<Runnable> tasks = new ArrayList<Runnable>(nbThreads);
        List<Future<?>> futures = new ArrayList<>();
		
        for (int k = 0; k < nbThreads; k++) {
        	tasks.add(() ->
        		this.thisNewTX.applyOneShotChargeInstance(syncCharges, result, postData.isGenerateRTs(), postData.isReturnWalletOperations(),
                        postData.isReturnWalletOperationDetails(), postData.isVirtual())
        	);
		}
        
        for (Runnable task : tasks) {
            futures.add(executor.submit(task));
        }
        
        for (Future<?> future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
                log.error("Failed to execute Mediation API async method", e);
            } catch (ExecutionException e) {
                log.error("Failed to execute Mediation API async method", e);
            }
		}

		// Summary
		AtomicReference<BigDecimal> amountWithTax = new AtomicReference<>(BigDecimal.ZERO);
		AtomicReference<BigDecimal> amountWithoutTax = new AtomicReference<>(BigDecimal.ZERO);
		AtomicReference<BigDecimal> amountTax = new AtomicReference<>(BigDecimal.ZERO);
		AtomicInteger walletOperationCount = new AtomicInteger(0);
		Arrays.stream(result.getAppliedCharges()).forEach(charge -> {
			if (charge != null) {
				amountWithTax.accumulateAndGet(Optional.ofNullable(charge.getAmountWithTax()).orElse(BigDecimal.ZERO), BigDecimal::add);
				amountWithoutTax.accumulateAndGet(Optional.ofNullable(charge.getAmountWithoutTax()).orElse(BigDecimal.ZERO), BigDecimal::add);
				amountTax.accumulateAndGet(Optional.ofNullable(charge.getAmountTax()).orElse(BigDecimal.ZERO), BigDecimal::add);

				walletOperationCount.addAndGet(Optional.ofNullable(charge.getWalletOperationCount()).orElse(0));
			} else {
				log.warn("cdrProcessingResult amouts and WOCount will have default 0 value, due to charge null");
			}
		});

		result.setAmountWithTax(amountWithTax.get());
		result.setAmountWithoutTax(amountWithoutTax.get());
		result.setAmountTax(amountTax.get());
		result.setWalletOperationCount(walletOperationCount.get());

		return result;
	}

	@JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void applyOneShotChargeInstance(SynchronizedIterator<ApplyOneShotChargeInstanceRequestDto> syncCharges, ProcessApplyChargeListResult result,
                                           boolean generateRTs, boolean returnWalletOperations, boolean returnWalletOperationDetails,
                                           boolean isVirtual) {
		
		while(true) {
			SynchronizedIterator<ApplyOneShotChargeInstanceRequestDto>.NextItem<ApplyOneShotChargeInstanceRequestDto> nextWPosition = syncCharges.nextWPosition();
			
			if(nextWPosition == null) {
				break;
			}
			
			int chargePosition = nextWPosition.getPosition();
			ApplyOneShotChargeInstanceRequestDto chargeToApply = nextWPosition.getValue();
			
			try {
				log.info("applyOneShotChargeInstance #{}", chargePosition);
				AppliedChargeResponseDto oshoDto;
				if (result.getMode() == ROLLBACK_ON_ERROR) {
					OneShotChargeInstance osho = subscriptionApi.applyOneShotChargeInstance(chargeToApply, isVirtual);
					oshoDto = createAppliedChargeResponseDto(osho, returnWalletOperations, returnWalletOperationDetails);
				} else {
					oshoDto = methodCallingUtils.callCallableInNewTx(() -> {
						OneShotChargeInstance osho = subscriptionApi.applyOneShotChargeInstance(chargeToApply, isVirtual);
						return createAppliedChargeResponseDto(osho, returnWalletOperations, returnWalletOperationDetails);
					});
				}
					
				result.addAppliedCharge(chargePosition, oshoDto);
				result.getStatistics().addSuccess();
			} catch (Exception e) {
				log.error("Error when applying OSO at position #["+chargePosition+"]" , e);
				result.getStatistics().addFail();
				result.addAppliedCharge(chargePosition, createAppliedChargeResponseErrorDto(e.getMessage()));
				if(result.getMode() == PROCESS_ALL) {
					continue;
				} else if (result.getMode() == ApplyOneShotChargeListModeEnum.STOP_ON_FIRST_FAIL) {
					result.setAppliedCharges(Arrays.copyOf(result.getAppliedCharges(), chargePosition + 1));
					break;
				} else {
					result.setAppliedCharges(new AppliedChargeResponseDto[] {createAppliedChargeResponseErrorDto(e.getMessage())});
					throw new BusinessApiException(e);
				}
			}
		}
		
	}

	private AppliedChargeResponseDto createAppliedChargeResponseDto(OneShotChargeInstance osho, boolean returnWalletOperation, boolean returnWallerOperationDetails) {
		AppliedChargeResponseDto lDto = new AppliedChargeResponseDto();
		
		lDto.setWalletOperationCount(osho.getWalletOperations().size());
		BigDecimal amountWithTax = BigDecimal.ZERO;
		BigDecimal amountWithoutTax = BigDecimal.ZERO;
		BigDecimal amountTax = BigDecimal.ZERO;
		for (WalletOperation wo : osho.getWalletOperations()) {
			if(returnWallerOperationDetails) {
				lDto.getWalletOperations().add(new WalletOperationDto(wo, wo.getAccountingArticle()));
			} else if(returnWalletOperation) {
				WalletOperationDto woDto = new WalletOperationDto();
				woDto.setId(wo.getId());
				lDto.getWalletOperations().add(woDto);
			}
			amountWithTax = amountWithTax.add(wo.getAmountWithTax() != null ? wo.getAmountWithTax() : BigDecimal.ZERO);
            amountWithoutTax = amountWithoutTax.add(wo.getAmountWithoutTax() != null ? wo.getAmountWithoutTax() : BigDecimal.ZERO);
            amountTax = amountTax.add(wo.getAmountTax() != null ? wo.getAmountTax() : BigDecimal.ZERO);
		}
		lDto.setAmountTax(amountTax);
		lDto.setAmountWithoutTax(amountWithoutTax);
		lDto.setAmountWithTax(amountWithTax);
		
		return lDto;
	}

	private AppliedChargeResponseDto createAppliedChargeResponseErrorDto(String errorMessage) {
		AppliedChargeResponseDto lDto = new AppliedChargeResponseDto();
		
		lDto.setError(new CdrError(errorMessage));
		
		return lDto;
	}
	
	public List<Long> getAllParentCustomers(String customerCode) {
		if (StringUtils.isBlank(customerCode)) {
			throw new BusinessApiException("Customer code is mandatory");
		}
		
        Customer customer = ofNullable(customerService.findByCode(customerCode)).orElseThrow(() -> new EntityDoesNotExistsException(Customer.class, customerCode));

        return getAllParentCustomerIds(customer, new ArrayList<Long>());
	}
	
	private List<Long> getAllParentCustomerIds(Customer customer, List<Long> ids) {
		Customer parent = customer.getParentCustomer();
		if (parent == null) {
			return ids;
		} else {
			ids.add(parent.getId());
			return getAllParentCustomerIds(parent, ids);
		}
	}
}
