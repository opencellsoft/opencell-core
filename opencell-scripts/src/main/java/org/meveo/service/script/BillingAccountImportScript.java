package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.account.BillingAccountApi;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.model.billing.BillingAccount;

public class BillingAccountImportScript extends GenericMassImportScript {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "BILLING_ACCOUNT";
    private static final String ENTITY_NAME = "BillingAccount";

    public enum BillingAccountActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final BillingAccountApi billingAccountApi = (BillingAccountApi) getServiceInterface(BillingAccountApi.class.getSimpleName());

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
                if (Stream.of(BillingAccountActionEnum.values()).noneMatch(e -> e.toString().equals(ocAction))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + ocAction);
                }

                BillingAccountActionEnum action = BillingAccountActionEnum.valueOf(ocAction);

                BillingAccountDto billingAccountDto = validateAndGetBillingAccount(recordMap);

                if (BillingAccountActionEnum.CREATE.equals(action)) {
                    setBillingAccountValues(recordMap, billingAccountDto);
                    BillingAccount billingAccount = billingAccountApi.create(billingAccountDto);
                    this.setCFValues(recordMap, billingAccount, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private BillingAccountDto validateAndGetBillingAccount(Map<String, Object> recordMap) {
        BillingAccountDto billingAccountDto;

        String customerAccountCode = (String) recordMap.get("OC_CUSTOMERACCOUNT_CODE");
        if (customerAccountCode.isEmpty()) {
            throw new ValidationException("customer_account_code is required");
        }

        String billingAccountCode = (String) recordMap.get("OC_BILLINGACCOUNT_CODE");
        if (billingAccountCode.isEmpty()) {
            throw new ValidationException("billing_account_code is required");
        }

        billingAccountDto = new BillingAccountDto();

        return billingAccountDto;
    }

    private void setBillingAccountValues(Map<String, Object> recordMap, BillingAccountDto billingAccountDto) throws ParseException {
        billingAccountDto.setCustomerAccount((String) recordMap.get("OC_CUSTOMERACCOUNT_CODE"));
        billingAccountDto.setCode((String) recordMap.get("OC_BILLINGACCOUNT_CODE"));
        billingAccountDto.setDescription((String) recordMap.get("OC_BILLINGACCOUNT_DESCRIPTION"));
        billingAccountDto.setBillingCycle((String) recordMap.get("OC_BILLINGACCOUNT_BC"));
        billingAccountDto.setCountry((String) recordMap.get("OC_BILLINGACCOUNT_COUNTRY"));
        billingAccountDto.setLanguage((String) recordMap.get("OC_BILLINGACCOUNT_LANGUAGE"));
        billingAccountDto.setTradingCurrency((String) recordMap.get("OC_BILLINGACCOUNT_CURRENCY"));
        billingAccountDto.setTaxCategoryCode((String) recordMap.get("OC_BILLINGACCOUNT_TAXCAT"));
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setLenient(false);
        String subscriptionDate = (String) recordMap.get("OC_BA_DATE");
        try {
            billingAccountDto.setSubscriptionDate(StringUtils.isEmpty(subscriptionDate) ? null : dateFormat.parse(subscriptionDate));
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for initial agreement date. Please use 'dd/MM/yyyy'");
        }
       // billingAccountDto.setRegistrationNo((String) recordMap.get("OC_BA_REGISTRATION"));
        billingAccountDto.setVatNo((String) recordMap.get("OC_BA_TVANUMBER"));

        NameDto name = new NameDto();
        name.setTitle((String) recordMap.get("OC_BA_TITLE"));
        name.setFirstName((String) recordMap.get("OC_BA_FNAME"));
        name.setLastName((String) recordMap.get("OC_BA_LNAME"));
        billingAccountDto.setName(name);

        TitleDto titleDto = new TitleDto();
        if (Objects.equals(recordMap.get("OC_BA_ORGANIZATION"), "X")) {
            titleDto.setIsCompany(true);
            titleDto.setCode((String) recordMap.get("OC_BA_TITLE"));
            billingAccountDto.setLegalEntityType(titleDto);
        }

        billingAccountDto.setIsCompany(Objects.equals(recordMap.get("OC_BA_ORGANIZATION"), "X"));
        billingAccountDto.setElectronicBilling(Objects.equals(recordMap.get("OC_BA_EBILL"), "X"));
        billingAccountDto.setEmail((String) recordMap.get("OC_BA_EBILLEMAIL"));
        billingAccountDto.setCcedEmails((String) recordMap.get("OC_BA_EBILLEMAILS"));

        AddressDto addressDto = new AddressDto();
        addressDto.setAddress1((String) recordMap.get("OC_BA_ADDRESS"));
        addressDto.setZipCode((String) recordMap.get("OC_BA_ZIPCODE"));
        addressDto.setCity((String) recordMap.get("OC_BA_CITY"));
        addressDto.setState((String) recordMap.get("OC_BA_STATE"));
        addressDto.setCountry((String) recordMap.get("OC_BA_COUNTRY"));
        billingAccountDto.setAddress(addressDto);
    }
}