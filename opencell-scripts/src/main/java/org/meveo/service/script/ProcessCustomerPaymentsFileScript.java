package org.meveo.service.script;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.CustomerPaymentRecordDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.payments.AccountOperationCFsEnum;
import org.meveo.model.payments.ApplicationPropertiesEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentService;

/**
 * @author Abdellatif BARI
 */
@Stateless
public class ProcessCustomerPaymentsFileScript extends Script {

    private static final long serialVersionUID = -6253110051247860364L;

    private transient BillingAccountService billingAccountService = (BillingAccountService) getServiceInterface(BillingAccountService.class.getSimpleName());

    private transient PaymentService paymentService = (PaymentService) getServiceInterface(PaymentService.class.getSimpleName());

    private transient OCCTemplateService oCCTemplateService = (OCCTemplateService) getServiceInterface(OCCTemplateService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> parameters) throws BusinessException {
        try {
            CustomerPaymentRecordDto customerPaymentRecordDto = initContext(parameters);
            processItem(customerPaymentRecordDto);
        } catch (Exception e) {
            log.error("error on process customer payments file {} ", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                // wrap the exception in a business exception and throwing it
                throw new BusinessException(e);
            }
        }
    }

    /**
     * Init context
     *
     * @param parameters parameters
     * @return the customer payment record Dto
     * @throws BusinessException the business exception
     */
    private CustomerPaymentRecordDto initContext(Map<String, Object> parameters) throws BusinessException {
        CustomerPaymentRecordDto customerPaymentRecordDto = (CustomerPaymentRecordDto) parameters.get("record");
        if (customerPaymentRecordDto == null) {
            throw new BusinessException(String.format("Parameter record is missing"));
        }
        customerPaymentRecordDto.setErrorMessage(new StringBuilder());
        String fileName = (String) parameters.get("origin_filename");
        if (StringUtils.isBlank(fileName)) {
            throw new BusinessException(String.format("Parameter origin_filename is missing"));
        }
        customerPaymentRecordDto.setFileName(fileName);
        return customerPaymentRecordDto;
    }

    /**
     * Process item
     *
     * @param customerPaymentRecordDto the customerPayment record Dto
     * @throws BusinessException the business exception
     */
    private void processItem(CustomerPaymentRecordDto customerPaymentRecordDto) throws BusinessException {
        validateItem(customerPaymentRecordDto);
        populateItem(customerPaymentRecordDto);
        if (customerPaymentRecordDto.getErrorMessage().length() > 0) {
            throw new BusinessException(customerPaymentRecordDto.getErrorMessage().toString());
        }
    }

    /**
     * Validate item
     *
     * @param customerPaymentRecordDto the customerPayment record Dto
     * @throws BusinessException the business exception
     */
    private void validateItem(CustomerPaymentRecordDto customerPaymentRecordDto) throws BusinessException {
        if (StringUtils.isBlank(customerPaymentRecordDto.getPaidAmount())) {
            customerPaymentRecordDto.getErrorMessage().append("The payment amount is required.");
        } else {
            customerPaymentRecordDto.setPaidAmount(customerPaymentRecordDto.getPaidAmount().trim());
            if (!isDouble(customerPaymentRecordDto.getPaidAmount())) {
                customerPaymentRecordDto.getErrorMessage().append("The payment amount is invalid");
            }
        }
        if (customerPaymentRecordDto.getDate() == null) {
            customerPaymentRecordDto.getErrorMessage().append("The payment date is required");
        }
        if (StringUtils.isBlank(customerPaymentRecordDto.getEndOfRecord()) || !customerPaymentRecordDto.getEndOfRecord().equalsIgnoreCase("V")) {
            customerPaymentRecordDto.getErrorMessage().append("The end of record must always hard coded as V.");
        }
    }

    /**
     * Check number is double
     *
     * @param strNum the string number
     * @return true is the string is double.
     */
    public static boolean isDouble(String strNum) {
        if (StringUtils.isBlank(strNum)) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Convert String to integer value (possibility to have null as default value)
     *
     * @param str string to be converted
     * @return the integer value
     */
    public static Integer toInteger(String str) {
        return toInteger(str, null);
    }

    /**
     * Convert String to integer value (possibility to have null as default value)
     *
     * @param str string to be converted
     * @param defaultValue the default value
     * @return the integer value
     */
    public static Integer toInteger(String str, Integer defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(str.trim());
            } catch (NumberFormatException ne) {
                return defaultValue;
            }
        }
    }

    /**
     * Populate tariff plan item
     *
     * @param customerPaymentRecordDto the customerPayment record Dto
     * @throws BusinessException the business exception
     */
    private void populateItem(CustomerPaymentRecordDto customerPaymentRecordDto) throws BusinessException {
        // if everything is OK and no errors
        if (customerPaymentRecordDto.getErrorMessage().length() == 0) {
            Integer accountNumber = toInteger(customerPaymentRecordDto.getAccountNumber());
            if (accountNumber != null) {
                customerPaymentRecordDto.setAccountNumber(accountNumber.toString());
            }

            BillingAccount billingAccount = billingAccountService.findByNumber(customerPaymentRecordDto.getAccountNumber());
            if (billingAccount != null) {
                // create payment.
                createPayment(customerPaymentRecordDto, billingAccount);
            } else { // the billing account is not found in Opencell
                log.warn("The billing account " + customerPaymentRecordDto.getAccountNumber() + " is not found ");
                postUnmatchedPayment(customerPaymentRecordDto);
            }
        }
    }

    /**
     * post unmatched payment into the suspense billing account
     *
     * @param customerPaymentRecordDto the customerPayment record Dto
     * @throws BusinessException the business exception
     */
    private void postUnmatchedPayment(CustomerPaymentRecordDto customerPaymentRecordDto) throws BusinessException {
        // get the suspense billing account
        String suspenseBillingAccountCode = ApplicationPropertiesEnum.SUSPENSE_BILLING_ACCOUNT_CODE.getProperty();
        BillingAccount billingAccount = billingAccountService.findByCode(suspenseBillingAccountCode);
        if (billingAccount != null) {
            // Create payment and put it into the suspense billing account
            createPayment(customerPaymentRecordDto, billingAccount);
        } else {
            log.error("The suspense billing account {} is not found", suspenseBillingAccountCode);
            customerPaymentRecordDto.getErrorMessage().append("The suspense billing account " + suspenseBillingAccountCode + " is not found ");
        }
    }

    /**
     * create the customer payment
     *
     * @param billingAccount the billing account
     * @return the payment
     * @throws BusinessException the business exception
     */
    private Payment createNewPayment(CustomerPaymentRecordDto customerPaymentRecordDto, BillingAccount billingAccount) throws BusinessException {

        String occTemplateCode = ApplicationPropertiesEnum.TEMPLATE_BATCH_PAYMENT_CREDIT.getProperty();
        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }

        BigDecimal amount = new BigDecimal(customerPaymentRecordDto.getPaidAmount()).divide(BigDecimal.valueOf(100));
        /*
         * if (!StringUtils.isBlank(checkDigit)) { checkDigit = checkDigit.trim(); if (checkDigit.equals("-")) { amount = amount.negate(); } }
         */

        Payment payment = new Payment();
        paymentService.calculateAmountsByTransactionCurrency(payment, billingAccount.getCustomerAccount(),
                amount,null, new Date());

        payment.setCustomerAccount(billingAccount.getCustomerAccount());
        payment.setPaymentMethod(PaymentMethodEnum.CARD);
        // payment.setOrderNumber(?);
        payment.setAccountingCode(occTemplate.getAccountingCode());
        payment.setCode(occTemplate.getCode());
        payment.setDescription(occTemplate.getDescription());
        payment.setTransactionCategory(OperationCategoryEnum.CREDIT); // EIR must provide the category of the transaction CREDIT or DEBIT
        payment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        payment.setReference(customerPaymentRecordDto.getReference());
        payment.setTransactionDate(customerPaymentRecordDto.getDate());
        payment.setCollectionDate(new Date());
        payment.setAccountingDate(new Date());
        // using the date the file is processed as the Payment Date (to be displayed on the invoice)
        payment.setDueDate(new Date());
        payment.setMatchingStatus(MatchingStatusEnum.O);
        payment.setCfValue(AccountOperationCFsEnum.BILLING_ACCOUNT_NUMBER.name(), billingAccount.getExternalRef1());
        // payment.setCfValue(FROM_AccountOperationCFsEnum.BILLING_ACCOUNT_NUMBER.name(), billingAccount.getExternalRef1());
        payment.setCfValue(AccountOperationCFsEnum.PAYMENT_FILE_ACCOUNT_CUSTOMER_NUMBER.name(), customerPaymentRecordDto.getAccountNumber());
        payment.setCfValue(AccountOperationCFsEnum.PAYMENT_FILE_NAME.name(), customerPaymentRecordDto.getFileName());
        return payment;
    }

    /**
     * create the customer payment
     *
     * @param billingAccount the billing account
     * @return the customer payment record Dto
     * @throws BusinessException the business exception
     */
    private void createPayment(CustomerPaymentRecordDto customerPaymentRecordDto, BillingAccount billingAccount) throws BusinessException {
        try {
            Payment payment = createNewPayment(customerPaymentRecordDto, billingAccount);
            paymentService.create(payment);
        } catch (BusinessException e) {
            log.error("Couldn't create Payement. error : {}", e);
            customerPaymentRecordDto.getErrorMessage().append("Couldn't create payment. error : " + e.getMessage());
        }
    }
}