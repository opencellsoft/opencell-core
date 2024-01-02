package org.meveo.service.script;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.account.CustomerApi;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;

public class CustomerImportScript extends GenericMassImportScript {

    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "CUSTOMER";
    private static final String ENTITY_NAME = "Customer";

    public enum CustomerActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final CustomerCategoryService customerCategoryService = (CustomerCategoryService) getServiceInterface("CustomerCategoryService");
    private final CustomerService customerService = (CustomerService) getServiceInterface("CustomerService");
    private final CustomerApi customerApi = (CustomerApi) getServiceInterface(CustomerApi.class.getSimpleName());

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
                if (Stream.of(CustomerActionEnum.values()).noneMatch(e -> e.toString().equals(ocAction))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + ocAction);
                }

                CustomerActionEnum action = CustomerActionEnum.valueOf(ocAction);

                CustomerDto customerDto = validateAndGetCustomer(action, recordMap);

                if (CustomerActionEnum.DELETE.equals(action)) {
                    customerApi.remove(customerDto.getCode());
                } else {
                    setCustomerValues(recordMap, customerDto);
                    if (CustomerActionEnum.CREATE.equals(action)) {
                        Customer customer = customerApi.create(customerDto);

                        this.setCFValues(recordMap, customer, ENTITY_NAME);
                    } else if (CustomerActionEnum.UPDATE.equals(action)) {
                        customerApi.update(customerDto);
                    }

                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private CustomerDto validateAndGetCustomer(CustomerActionEnum action, Map<String, Object> recordMap) {
        CustomerDto customerDto;

        String customerCode = (String) recordMap.get("OC_CUSTOMER_CODE");
        if (customerCode.isEmpty()) {
            throw new ValidationException("customer_code is required");
        }
        Customer customer = customerService.findByCode(customerCode);

        // For update and delete : check if there is a record
        if (!CustomerActionEnum.CREATE.equals(action)) {
            if (customer == null) {
                throw new ValidationException("no customer found for customer_code: '" + customerCode + "'");
            }
            customerDto = customerApi.find(customerCode);
            // Create action : check if there is no record
        } else {
            customerDto = new CustomerDto();
            String ocCustomerCategory = (String) recordMap.get("OC_CU_CATEGORY");
            CustomerCategory customerCategory = customerCategoryService.findByCode(ocCustomerCategory);
            if (customerCategory == null) {
                throw new ValidationException("no customer category found for code: '" + ocCustomerCategory + "'");
            }
        }
        return customerDto;
    }

    private void setCustomerValues(Map<String, Object> recordMap, CustomerDto customerDto) {
        customerDto.setCode((String) recordMap.get("OC_CUSTOMER_CODE"));
        customerDto.setDescription((String) recordMap.get("OC_CUSTOMER_DESCRIPTION"));
		
       // customerDto.setRegistrationNo((String) recordMap.get("OC_CU_REGISTRATION"));
        customerDto.setVatNo((String) recordMap.get("OC_CU_TVANUMBER"));
        customerDto.setSeller((String) recordMap.get("OC_CUSTOMER_SELLER"));
        customerDto.setCustomerCategory((String) recordMap.get("OC_CU_CATEGORY"));
        customerDto.setIsCompany(Objects.equals(recordMap.get("OC_CU_IS_COMPANY"), "X"));
        NameDto nameDto = new NameDto();
        nameDto.setTitle((String) recordMap.get("OC_CU_TITLE"));
        nameDto.setFirstName((String) recordMap.get("OC_CU_FIRST_NAME"));
        nameDto.setLastName((String) recordMap.get("OC_CU_LAST_NAME"));
        customerDto.setName(nameDto);
    }
}