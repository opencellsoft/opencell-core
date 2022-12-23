package org.meveo.apiv2.accounts.service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.job.DateRange;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.CounterInstanceDto;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.ParentInput;
import org.meveo.apiv2.generic.exception.ConflictException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
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
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterPeriodService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UsageChargeInstanceService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateMappingService;
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

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private BillingAccountService billingAccountService;

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
    private CounterPeriodService counterPeriodService;
    @Inject
    private ProductChargeTemplateMappingService productChargeTemplateMappingService;

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

        // Attache to new user account
        subscription.setUserAccount(newOwner);
        subscriptionService.updateNoCheck(subscription);
        
        var usageServiceInstance = usageChargeInstanceService.findUsageChargeInstanceBySubscriptionId(subscription.getId());
        changeToNewUserAccount(usageServiceInstance, newOwner);
        var oneshotServiceInstance = oneShotChargeInstanceService.findOneShotChargeInstancesBySubscriptionId(subscription.getId());
        changeToNewUserAccount(oneshotServiceInstance, newOwner);
        var recurringServiceInstance = recurringChargeInstanceService.findRecurringChargeInstanceBySubscriptionId(subscription.getId());
        changeToNewUserAccount(recurringServiceInstance, newOwner);

        // The change must be logged (audit log + make Subscription.userAccount into auditable field)
        createAuditLog(Subscription.class.getName());
        auditableFieldService.createFieldHistory(subscription, "userAccount", AuditChangeTypeEnum.OTHER, oldUserAccount, newOwner.getCode());
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

    public List<Long> createCounterInstance(CounterInstanceDto dto) {
        CounterTemplate counterTemplate = checkFieldsAndGetCounterTemplate(dto);

        List<Long> createdCounterInstances = new ArrayList<>();

        List<ServiceInstance> serviceInstances = new ArrayList<>(
                serviceInstanceService.findByCodeAndCodeSubscription(dto.getProductCode(), dto.getSubscriptionCode()));

        if (CollectionUtils.isEmpty(serviceInstances)) {
            throw new EntityDoesNotExistsException("No service instance found for [product code=" + dto.getProductCode() + ", subscription code=" + dto.getSubscriptionCode() + "]");
        }

        // si level SI, on boucle et on traite pour chaque serviceInstance retrouvé
        // si level subscritpion : on traite le premier en respectant un critere de date à voir
        if (serviceInstances.size() > 1 && CounterTemplateLevel.SU == counterTemplate.getCounterLevel()) {
            serviceInstances.sort(Comparator.comparing(ServiceInstance::getStatusDate));
            CounterInstance counterInstance = counterInstanceService.counterInstanciationWithoutForceCommit(serviceInstances.get(0), counterTemplate, false);
            processCounterInstanceAndPeriod(counterInstance, dto, serviceInstances.get(0), counterTemplate, createdCounterInstances);
        } else {
            serviceInstances.forEach(si -> {
                CounterInstance counterInstance = counterInstanceService.counterInstanciationWithoutForceCommit(si, counterTemplate, false);
                processCounterInstanceAndPeriod(counterInstance, dto, si, counterTemplate, createdCounterInstances);
            });
        }

        return createdCounterInstances;

    }

    public List<Long> updateCounterInstance(Long id, CounterInstanceDto dto) {
        CounterInstance counterInstance = counterInstanceService.findById(id);

        if (counterInstance == null) {
            throw new EntityDoesNotExistsException("No CounterInstance found with id : " + id);
        }

        CounterTemplate counterTemplate = checkFieldsAndGetCounterTemplate(dto);

        List<Long> createdCounterInstances = new ArrayList<>();

        List<ServiceInstance> serviceInstances = serviceInstanceService.findByCodeAndCodeSubscription(dto.getProductCode(), dto.getSubscriptionCode());

        if (CollectionUtils.isEmpty(serviceInstances)) {
            throw new BusinessApiException("No service instance found for [code=" + dto.getProductCode() + ", subscription codd=" + dto.getSubscriptionCode() + "]");
        }

        // si level SI, on boucle et on traite pour chaque serviceInstance retrouvé
        // si level subscritpion : on traite le premier en respectant un critere de date à voir
        if (serviceInstances.size() > 1 && CounterTemplateLevel.SU == counterTemplate.getCounterLevel()) {
            serviceInstances.sort(Comparator.comparing(ServiceInstance::getStatusDate));
            processCounterInstanceAndPeriod(counterInstance, dto, serviceInstances.get(0), counterTemplate, createdCounterInstances);
        } else {
            serviceInstances.forEach(si -> processCounterInstanceAndPeriod(counterInstance, dto, si, counterTemplate, createdCounterInstances));
        }

        return createdCounterInstances;

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

        if (CollectionUtils.isEmpty(dto.getChargeInstances())) {
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

        // Period start and end date
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

    private void processCounterInstanceAndPeriod(CounterInstance counterInstance, CounterInstanceDto dto, ServiceInstance si, CounterTemplate counterTemplate, List<Long> createdCounterInstances) {
        createdCounterInstances.add(counterInstance.getId());

        dto.getChargeInstances().forEach(s -> {
            ChargeInstance charge = chargeInstanceService.findByCode(s);

            if (!productChargeTemplateMappingService.checkExistenceByProductAndChargeAndCounterTemplate(dto.getProductCode(), charge.getCode(), counterTemplate.getCode())) {
                throw new BusinessApiException("ChargeInstance with [type=" + charge.getChargeType() + ", code=" + charge.getCode()
                        + "] is not linked to Product [code=" + si.getCode()
                        + "] and CounterTemplate [code=" + counterTemplate.getCode() + "]");
            }

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
                    counterInstanceService.createPeriod(counterInstance, charge.getChargeDate(), periodDto.getStartDate(), charge, periodDto.getValue(), periodDto.getLevel(), false);
                } else {
                    counterInstanceService.updatePeriod(existingCounterPeriod, counterInstance, charge.getChargeDate(), periodDto.getStartDate(), charge, periodDto.getValue(), periodDto.getLevel());
                }

                periodes.add(new DateRange(startP, endP));
            });

        });
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
}
