package org.meveo.service.script;

import java.text.ParseException;
import java.util.Map;
import java.util.stream.Stream;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.account.UserAccountApi;
import org.meveo.api.dto.account.NameDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.model.billing.UserAccount;

public class UserAccountImportScript extends GenericMassImportScript {

    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "USER_ACCOUNT";
    private static final String ENTITY_NAME = "UserAccount";

    public enum UserAccountActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final UserAccountApi userAccountApi = (UserAccountApi) getServiceInterface(UserAccountApi.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                // VALIDATE ENTITY
                String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
                if (!ENTITY.equals(OC_ENTITY)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
                }
                // VALIDATE ACTION
                String OC_ACTION = (String) recordMap.get("OC_ACTION");
                if (Stream.of(UserAccountActionEnum.values()).noneMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }

                UserAccountActionEnum action = UserAccountActionEnum.valueOf(OC_ACTION);

                UserAccountDto userAccountDto = validateAndGetCustomer(action, recordMap);

                if (UserAccountActionEnum.CREATE.equals(action)) {
                    setAccessValues(recordMap, userAccountDto);
                    UserAccount userAccount = userAccountApi.create(userAccountDto);
                    this.setCFValues(recordMap, userAccount, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private UserAccountDto validateAndGetCustomer(UserAccountActionEnum action, Map<String, Object> recordMap) throws Exception {
        UserAccountDto userAccountDto = null;

        String billingAccountCode = (String) recordMap.get("OC_BILLINGACCOUNT_CODE");
        if (billingAccountCode.isEmpty()) {
            throw new ValidationException("billing_account_code is required");
        }

        String userAccountCode = (String) recordMap.get("OC_USERACCOUNT_CODE");
        if (userAccountCode.isEmpty()) {
            throw new ValidationException("user_account_code is required");
        }

        userAccountDto = new UserAccountDto();

        return userAccountDto;
    }

    private void setAccessValues(Map<String, Object> recordMap, UserAccountDto userAccountDto) throws ParseException {
        userAccountDto.setBillingAccount((String) recordMap.get("OC_BILLINGACCOUNT_CODE"));
        userAccountDto.setCode((String) recordMap.get("OC_USERACCOUNT_CODE"));
        String ocParentUACode = (String) recordMap.get("OC_PARENT_UA_CODE");
        if (!ocParentUACode.isEmpty()) {
            userAccountDto.setParentUserAccountCode(ocParentUACode);
        }
        userAccountDto.setDescription((String) recordMap.get("OC_USERACCOUNT_ DESCRIPTION"));
		
        //userAccountDto.setRegistrationNo((String) recordMap.get("OC_UA_REGISTRATION"));
        userAccountDto.setVatNo((String) recordMap.get("OC_UA_TVANUMBER"));

        NameDto name = new NameDto();
        name.setTitle((String) recordMap.get("OC_UA_TITLE"));
        name.setFirstName((String) recordMap.get("OC_UA_FNAME"));
        name.setLastName((String) recordMap.get("OC_UA_LNAME"));
        userAccountDto.setName(name);

        userAccountDto.setIsCompany((String) recordMap.get("OC_UA_ORGANIZATION") == "X");
    }
}