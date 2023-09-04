package org.meveo.service.script;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.crm.Customer;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.CustomerService;

import java.util.*;
import java.util.stream.Stream;

public class DunningSuspendSubscriptionScript extends Script {

    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "CUSTOMER_ACCOUNT";
    private final CustomerService customerService = (CustomerService) getServiceInterface("CustomerService");
    private final SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");

    public enum SubscriptionActionEnum {
        CREATE, UPDATE, SUSPEND, RESUME, ACTIVATE, TERMINATE
    }

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);

            if (recordMap != null && !recordMap.isEmpty()) {
                String ocEntity = (String) recordMap.get("OC_ENTITY");
                String ocAction = (String) recordMap.get("OC_ACTION");
                String customerAccountCode = (String) recordMap.get("OC_CUSTOMERACCOUNT_CODE");
                String customerCode = (String) recordMap.get("OC_CUSTOMER_CODE");

                if (!ENTITY.equals(ocEntity)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + ocEntity);
                }

                if (Stream.of(SubscriptionActionEnum.values()).noneMatch(e -> e.toString().equals(ocAction)) && SubscriptionActionEnum.SUSPEND.name().equals(ocAction)) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + ocAction);
                }

                if (customerAccountCode.isEmpty()) {
                    throw new ValidationException("customer_account_code is required");
                }

                if (customerCode.isEmpty()) {
                    throw new ValidationException("customer_code is required");
                }

                Customer customer = customerService.findByCode(customerCode);

                if (customer == null) {
                    throw new ValidationException("no customer found for customer_code: '" + customerCode + "'");
                }

                customer.getCustomerAccounts().forEach(
                        customerAccount -> customerAccount.getBillingAccounts().forEach(
                                billingAccount -> billingAccount.getUsersAccounts().forEach(
                                        userAccount -> userAccount.getSubscriptions().forEach(
                                            subscription -> subscriptionService.subscriptionSuspension(subscription, new Date())
                ))));
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }}