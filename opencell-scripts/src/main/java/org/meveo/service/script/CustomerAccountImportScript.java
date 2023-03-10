package org.meveo.service.script;

import java.util.Map;
import java.util.stream.Stream;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.account.CustomerAccountApi;
import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.crm.impl.CustomerService;

public class CustomerAccountImportScript extends GenericMassImportScript {

    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "CUSTOMER_ACCOUNT";
    private static final String ENTITY_NAME = "CustomerAccount";

    public enum CustomerAccountActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final CustomerService customerService = (CustomerService) getServiceInterface("CustomerService");
    private final CustomerAccountApi customerAccountApi = (CustomerAccountApi) getServiceInterface(CustomerAccountApi.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                // VALIDATE ENTITY
                String ocEntity = (String) recordMap.get("OC_ENTITY");
                if (!ENTITY.equals(ocEntity)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + ocEntity);
                }
                // VALIDATE ACTION
                String ocAction = (String) recordMap.get("OC_ACTION");
                if (Stream.of(CustomerAccountActionEnum.values()).noneMatch(e -> e.toString().equals(ocAction))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + ocAction);
                }

                CustomerAccountActionEnum action = CustomerAccountActionEnum.valueOf(ocAction);

                CustomerAccountDto customerAccountDto = validateAndGetCustomerAccount(action, recordMap);

                if (CustomerAccountActionEnum.DELETE.equals(action)) {
                    customerAccountApi.remove(customerAccountDto.getCode());
                } else {
                    setCustomerAccountValues(recordMap, customerAccountDto);
                    if (CustomerAccountActionEnum.CREATE.equals(action)) {
                        CustomerAccount customerAccount = customerAccountApi.create(customerAccountDto);

                        this.setCFValues(recordMap, customerAccount, ENTITY_NAME);
                    } else if (CustomerAccountActionEnum.UPDATE.equals(action)) {
                        customerAccountApi.update(customerAccountDto);
                    }

                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private CustomerAccountDto validateAndGetCustomerAccount(CustomerAccountActionEnum action, Map<String, Object> recordMap) throws Exception {
        CustomerAccountDto customerAccountDto;

        String customerAccountCode = (String) recordMap.get("OC_CUSTOMERACCOUNT_CODE");
        if (customerAccountCode.isEmpty()) {
            throw new ValidationException("customer_account_code is required");
        }

        String customerCode = (String) recordMap.get("OC_CUSTOMER_CODE");
        if (customerCode.isEmpty()) {
            throw new ValidationException("customer_code is required");
        }
        Customer customer = customerService.findByCode(customerCode);

        // For update and delete : check if there is a record
        if (!CustomerAccountActionEnum.CREATE.equals(action)) {
            if (customer == null) {
                throw new ValidationException("no customer found for customer_code: '" + customerCode + "'");
            }
            customerAccountDto = customerAccountApi.find(customerAccountCode, false);
            // Create action : check if there is no record
        } else {
            customerAccountDto = new CustomerAccountDto();
        }
        return customerAccountDto;
    }

    private void setCustomerAccountValues(Map<String, Object> recordMap, CustomerAccountDto customerAccountDto) {
        customerAccountDto.setCustomer((String) recordMap.get("OC_CUSTOMER_CODE"));
        customerAccountDto.setCode((String) recordMap.get("OC_CUSTOMERACCOUNT_CODE"));
        customerAccountDto.setCurrency((String) recordMap.get("OC_CUSTOMERACCOUNT_CURRENCY"));
        customerAccountDto.setDueDateDelayEL((String) recordMap.get("OC_CUSTOMERACCOUNT_PAYTERM"));
        customerAccountDto.setCreditCategory((String) recordMap.get("OC_CUSTOMERACCOUNT_CREDCAT"));
        customerAccountDto.setLanguage((String) recordMap.get("OC_CUSTOMERACCOUNT_LANG"));
    }
}