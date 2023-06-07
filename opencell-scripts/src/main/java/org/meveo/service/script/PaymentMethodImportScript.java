package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.PaymentMethodService;

public class PaymentMethodImportScript extends GenericMassImportScript {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "PAYMENT_METHOD";
    private static final String ENTITY_NAME = "PaymentMethod";

    public enum PaymentMethodActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final PaymentMethodService paymentMethodService = (PaymentMethodService) getServiceInterface("PaymentMethodService");
    private final PaymentMethodApi paymentMethodApi = (PaymentMethodApi) getServiceInterface(PaymentMethodApi.class.getSimpleName());

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
                if (Stream.of(PaymentMethodActionEnum.values()).noneMatch(e -> e.toString().equals(ocAction))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + ocAction);
                }

                PaymentMethodActionEnum action = PaymentMethodActionEnum.valueOf(ocAction);

                PaymentMethodDto paymentMethodDto = validateAndGetPaymentMethod(recordMap);

                if (action == PaymentMethodActionEnum.CREATE) {
                    setPaymentMethodValues(recordMap, paymentMethodDto);
                    Long paymentMethodId = paymentMethodApi.create(paymentMethodDto);
                    PaymentMethod paymentMethod = paymentMethodService.findById(paymentMethodId);
                    this.setCFValues(recordMap, paymentMethod, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private PaymentMethodDto validateAndGetPaymentMethod(Map<String, Object> recordMap) {
        PaymentMethodDto paymentMethodDto = new PaymentMethodDto();

        String customerAccountCode = (String) recordMap.get("OC_PMT_CA");
        if (customerAccountCode.isEmpty()) {
            throw new ValidationException("customer_account_code is required");
        }

        return paymentMethodDto;
    }

    private void setPaymentMethodValues(Map<String, Object> recordMap, PaymentMethodDto paymentMethodDto) throws ParseException {
        PaymentMethodEnum ocPaymentMethodType = PaymentMethodEnum.valueOf((String) recordMap.get("OC_PMT_TYPE"));
        paymentMethodDto.setPaymentMethodType(ocPaymentMethodType);
        paymentMethodDto.setPreferred(Objects.equals(recordMap.get("OC_PMT_PREFERRED"), "X"));
        paymentMethodDto.setAlias((String) recordMap.get("OC_PMT_ALIAS"));
        paymentMethodDto.setCustomerAccountCode((String) recordMap.get("OC_PMT_CA"));
        if (!((String) recordMap.get("OC_PMT_cardType")).isEmpty()) {
            paymentMethodDto.setCardType(CreditCardTypeEnum.valueOf((String) recordMap.get("OC_PMT_cardType")));
        }
        if (!((String) recordMap.get("OC_PMT_monthExpiration")).isEmpty()) {
            paymentMethodDto.setMonthExpiration(Integer.parseInt((String) recordMap.get("OC_PMT_monthExpiration")));
        }
        if (!((String) recordMap.get("OC_PMT_yearExpiration")).isEmpty()) {
            paymentMethodDto.setYearExpiration(Integer.parseInt((String) recordMap.get("OC_PMT_yearExpiration")));
        }
        paymentMethodDto.setTokenId((String) recordMap.get("OC_PMT_tokenId"));
        paymentMethodDto.setCardNumber((String) recordMap.get("OC_PMT_cardNumber"));
        paymentMethodDto.setIssueNumber((String) recordMap.get("OC_PMT_issueNumber"));
        paymentMethodDto.setUserId((String) recordMap.get("OC_PMT_userId"));
        paymentMethodDto.setEmail((String) recordMap.get("OC_PMT_email"));
        paymentMethodDto.setOwner((String) recordMap.get("OC_PMT_accountOwner"));
        if (!((String) recordMap.get("OC_PMT_referenceDocumentCode")).isEmpty()) {
            paymentMethodDto.setReferenceDocumentCode((String) recordMap.get("OC_PMT_referenceDocumentCode"));
        }

        if (ocPaymentMethodType == PaymentMethodEnum.DIRECTDEBIT) {
            BankCoordinatesDto bankCoordinatesDto = new BankCoordinatesDto();
            bankCoordinatesDto.setBankCode((String) recordMap.get("OC_PMT_bankCode"));
            bankCoordinatesDto.setBranchCode((String) recordMap.get("OC_PMT_branchCode"));
            bankCoordinatesDto.setAccountNumber((String) recordMap.get("OC_PMT_accountNumber"));
            bankCoordinatesDto.setKey((String) recordMap.get("OC_PMT_key"));
            bankCoordinatesDto.setIban((String) recordMap.get("OC_PMT_iban"));
            bankCoordinatesDto.setBic((String) recordMap.get("OC_PMT_bic"));
            bankCoordinatesDto.setAccountOwner((String) recordMap.get("OC_PMT_accountOwner"));
            bankCoordinatesDto.setBankName((String) recordMap.get("OC_PMT_bankName"));
            bankCoordinatesDto.setBankId((String) recordMap.get("OC_PMT_bankId"));
            bankCoordinatesDto.setIssuerNumber((String) recordMap.get("OC_PMT_issuerNumber"));
            bankCoordinatesDto.setIssuerName((String) recordMap.get("OC_PMT_issuerName"));
            bankCoordinatesDto.setIcs((String) recordMap.get("OC_PMT_ics"));
            paymentMethodDto.setBankCoordinates(bankCoordinatesDto);
        }

        paymentMethodDto.setMandateIdentification((String) recordMap.get("OC_PMT_mandateIdentification"));

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setLenient(false);
        String ocPaymentMethodMandateDate = (String) recordMap.get("OC_PMT_mandateDate");
        try {
            Date mandateDate = StringUtils.isEmpty(ocPaymentMethodMandateDate) ? null : dateFormat.parse(ocPaymentMethodMandateDate);
            paymentMethodDto.setMandateDate(mandateDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for mandate date. Please use dd/MM/yyyy");
        }

    }
}