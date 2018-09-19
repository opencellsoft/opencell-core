package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentLevelEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.filter.FilterService;

/**
 * The Class DDRequestItemService.
 *
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class DDRequestItemService extends PersistenceService<DDRequestItem> {

    /** The recorded invoice service. */
    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    /** The customer account service. */
    @Inject
    private CustomerAccountService customerAccountService;

    /** The d D request LOT service. */
    @Inject
    private DDRequestLOTService dDRequestLOTService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The automated payment service. */
    @Inject
    private AutomatedPaymentService automatedPaymentService;

    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;

    /** The matching code service. */
    @Inject
    private MatchingCodeService matchingCodeService;

    /** The filter service. */
    @Inject
    private FilterService filterService;

    /** The payment history service. */
    @Inject
    private PaymentHistoryService paymentHistoryService;

    /**
     * Creates the DDRequest lot.
     *
     * @param fromDueDate the from due date
     * @param toDueDate the to due date
     * @param ddRequestBuilder the dd request builder
     * @param filter the filter
     * @return the DD request LOT
     * @throws BusinessEntityException the business entity exception
     * @throws Exception the exception
     */
    public DDRequestLOT createDDRquestLot(Date fromDueDate, Date toDueDate, DDRequestBuilder ddRequestBuilder, Filter filter) throws BusinessEntityException, Exception {
        log.info("createDDRquestLot fromDueDate: {}   toDueDate: {}", fromDueDate, toDueDate);
        List<Long> caIds = new ArrayList<Long>();
        List<CustomerAccount> listCA = new ArrayList<CustomerAccount>();
        if (filter == null) {
            if (fromDueDate == null) {
                throw new BusinessEntityException("fromDuDate is empty");
            }
            if (toDueDate == null) {
                throw new BusinessEntityException("toDueDate is empty");
            }
            if (fromDueDate.after(toDueDate)) {
                throw new BusinessEntityException("fromDueDate is after toDueDate");
            }

            caIds = customerAccountService.getCAidsForPayment(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate);
        } else {
            listCA = (List<CustomerAccount>) filterService.filteredListAsObjects(filter);
            for (CustomerAccount ca : listCA) {
                caIds.add(ca.getId());
            }
        }

        if ((caIds == null) || (caIds.isEmpty())) {
            throw new BusinessEntityException("no invoices!");
        }

        log.info("number ca to pay: {}", caIds.size());
        BigDecimal totalAmount = BigDecimal.ZERO;
        DDRequestLOT ddRequestLOT = new DDRequestLOT();
        ddRequestLOT.setDdRequestBuilder(ddRequestBuilder);
        ddRequestLOT.setSendDate(new Date());
        dDRequestLOTService.create(ddRequestLOT);
        List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
        int rejectedInvoice = 0;
        int nbItems = 0;
        String allErrors = "";
        for (Long caID : caIds) {
            List<AccountOperation> listAoToPayOrRefund = accountOperationService.getAOsToPay(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate, caID);
            if (ddRequestBuilder.getPaymentLevel() == PaymentLevelEnum.CA) {
                List<Long> aoIds = new ArrayList<Long>();
                BigDecimal amountToPay = BigDecimal.ZERO;
                String caFullName = null;
                for (AccountOperation ao : listAoToPayOrRefund) {
                    String errorMsg = getMissingField(ao);
                    caFullName = ao.getCustomerAccount().getName().getFirstName();
                    aoIds.add(ao.getId());
                    if (errorMsg != null) {
                        allErrors += errorMsg + " ; ";
                        rejectedInvoice++;
                    } else {
                        amountToPay = amountToPay.add(ao.getUnMatchingAmount());
                    }
                }
                if (!aoIds.isEmpty()) {
                    DDRequestItem ddRequestItem = createDDRequestItem(amountToPay, ddRequestLOT, caFullName, allErrors, listAoToPayOrRefund);
                    ddRequestLOT.getDdrequestItems().add(ddRequestItem);
                    nbItems++;
                    totalAmount = totalAmount.add(ddRequestItem.getAmount());
                    for (AccountOperation ao : listAoToPayOrRefund) {
                        ao.setDdRequestItem(ddRequestItem);
                    }
                }
            } else {
                for (AccountOperation ao : listAoToPayOrRefund) {
                    String errorMsg = getMissingField(ao);                   
                    List<Long> aoIds = new ArrayList<Long>();
                    aoIds.add(ao.getId());
                    DDRequestItem ddRequestItem = createDDRequestItem(ao.getUnMatchingAmount(), ddRequestLOT, ao.getCustomerAccount().getName().getFullName(), errorMsg,
                        Arrays.asList(ao));
                    ddRequestLOT.getDdrequestItems().add(ddRequestItem);
                    nbItems++;
                    if (errorMsg != null) {
                        rejectedInvoice++;
                    } else {                       
                        totalAmount = totalAmount.add(ao.getUnMatchingAmount());
                    }
                    ao.setDdRequestItem(ddRequestItem);
                }
            }
        }
        ddRequestLOT.setInvoicesNumber(nbItems);
        ddRequestLOT.setRejectedInvoices(rejectedInvoice);

        ddRequestLOT.setRejectedCause(StringUtils.truncate(allErrors, 255, true));
        ddRequestLOT.setDdrequestItems(ddrequestItems);
        ddRequestLOT.setInvoicesAmount(totalAmount);
        log.info("ddRequestLOT created , totalAmount: {}", ddRequestLOT.getInvoicesAmount());
        log.info("Successful createDDRquestLot fromDueDate: {} toDueDate: {}", fromDueDate, toDueDate);

        // return ddRequestLOT;
        //
        // }
        //
        // /**
        // * Creates the payments for DD request lot.
        // *
        // * @param ddRequestLOT the dd request LOT
        // * @throws BusinessException the business exception
        // * @throws NoAllOperationUnmatchedException the no all operation unmatched exception
        // * @throws UnbalanceAmountException the unbalance amount exception
        // */
        // public void createPaymentsForDDRequestLot(DDRequestLOT ddRequestLOT) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        log.info("createPaymentsForDDRequestLot ddRequestLotId: {}, size:{}", ddRequestLOT.getId(), ddRequestLOT.getDdrequestItems().size());
        // ddRequestLOT = dDRequestLOTService.refreshOrRetrieve(ddRequestLOT);
        if (ddRequestLOT.isPaymentCreated()) {
            throw new BusinessException("Payment Already created.");
        }

        for (int i = 0; i < ddRequestLOT.getDdrequestItems().size(); i++) {

            DDRequestItem ddrequestItem = ddRequestLOT.getDdrequestItems().get(i);
            AutomatedPayment automatedPayment = null;
            PaymentErrorTypeEnum paymentErrorTypeEnum = null;
            PaymentStatusEnum paymentStatusEnum = null;
            String errorMsg = null;
            if (!ddrequestItem.hasError()) {
                if (BigDecimal.ZERO.compareTo(ddrequestItem.getAmount()) == 0) {
                    log.info("invoice: {}  balanceDue:{}  no DIRECTDEBIT transaction", ddrequestItem.getReference(), BigDecimal.ZERO);
                } else {
                    automatedPayment = createPayment(PaymentMethodEnum.DIRECTDEBIT, ddrequestItem.getAmount(), ddrequestItem.getAccountOperations().get(0).getCustomerAccount(),
                        ddrequestItem.getReference(), ddRequestLOT.getFileName(), ddRequestLOT.getSendDate(), DateUtils.addDaysToDate(new Date(), ArConfig.getDateValueAfter()),
                        ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(), ddrequestItem.getAccountOperations(), true, MatchingTypeEnum.A_DERICT_DEBIT);
                    ddrequestItem.setAutomatedPayment(automatedPayment);
                    updateNoCheck(ddrequestItem);
                    paymentStatusEnum = PaymentStatusEnum.PENDING;
                }
            } else {
                paymentErrorTypeEnum = PaymentErrorTypeEnum.ERROR;
                paymentStatusEnum = PaymentStatusEnum.ERROR;
                errorMsg = ddrequestItem.getErrorMsg();
            }
            paymentHistoryService.addHistory(ddrequestItem.getAccountOperations().get(0).getCustomerAccount(), automatedPayment, null,
                (ddrequestItem.getAmount().multiply(new BigDecimal(100))).longValue(), paymentStatusEnum, errorMsg, errorMsg, paymentErrorTypeEnum, OperationCategoryEnum.CREDIT,
                ddRequestLOT.getDdRequestBuilder().getCode(), ddrequestItem.getAccountOperations().get(0).getCustomerAccount().getPreferredPaymentMethod());

        }
        ddRequestLOT.setPaymentCreated(true);
        dDRequestLOTService.updateNoCheck(ddRequestLOT);

        log.info("Successful createPaymentsForDDRequestLot ddRequestLotId: {}", ddRequestLOT.getId());
        return ddRequestLOT;
    }

    /**
     * Creates the payment.
     *
     * @param paymentMethodEnum the payment method enum
     * @param amount the amount
     * @param customerAccount the customer account
     * @param reference the reference
     * @param bankLot the bank lot
     * @param depositDate the deposit date
     * @param bankCollectionDate the bank collection date
     * @param dueDate the due date
     * @param transactionDate the transaction date
     * @param occForMatching the occ for matching
     * @param isToMatching the is to matching
     * @param matchingTypeEnum the matching type enum
     * @return the automated payment
     * @throws BusinessException the business exception
     * @throws NoAllOperationUnmatchedException the no all operation unmatched exception
     * @throws UnbalanceAmountException the unbalance amount exception
     */
    public AutomatedPayment createPayment(PaymentMethodEnum paymentMethodEnum, BigDecimal amount, CustomerAccount customerAccount, String reference, String bankLot,
            Date depositDate, Date bankCollectionDate, Date dueDate, Date transactionDate, List<AccountOperation> occForMatching, boolean isToMatching,
            MatchingTypeEnum matchingTypeEnum) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        log.info("create payment for amount:" + amount + " paymentMethodEnum:" + paymentMethodEnum + " isToMatching:" + isToMatching + "  customerAccount:"
                + customerAccount.getCode() + "...");

        ParamBean paramBean = paramBeanFactory.getInstance();
        String occTemplateCode = paramBean.getProperty("occ.payment.dd", "PAY_DDT");

        OCCTemplate occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        if (occTemplate == null) {
            throw new BusinessException("Cannot find OCC Template with code=" + occTemplateCode);
        }

        AutomatedPayment automatedPayment = new AutomatedPayment();
        automatedPayment.setPaymentMethod(paymentMethodEnum);
        automatedPayment.setAmount(amount);
        automatedPayment.setUnMatchingAmount(amount);
        automatedPayment.setMatchingAmount(BigDecimal.ZERO);
        automatedPayment.setAccountingCode(occTemplate.getAccountingCode());
        automatedPayment.setOccCode(occTemplate.getCode());
        automatedPayment.setOccDescription(occTemplate.getDescription());
        automatedPayment.setTransactionCategory(occTemplate.getOccCategory());
        automatedPayment.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());
        automatedPayment.setCustomerAccount(customerAccount);
        automatedPayment.setReference(reference);
        automatedPayment.setBankLot(bankLot);
        automatedPayment.setDepositDate(depositDate);
        automatedPayment.setBankCollectionDate(bankCollectionDate);
        automatedPayment.setDueDate(dueDate);
        automatedPayment.setTransactionDate(transactionDate);
        automatedPayment.setMatchingStatus(MatchingStatusEnum.O);
        automatedPayment.setUnMatchingAmount(amount);
        automatedPayment.setMatchingAmount(BigDecimal.ZERO);
        // automatedPayment.setTransactionDate(new Date());
        automatedPaymentService.create(automatedPayment);
        if (isToMatching) {
            List<Long> aoIds = new ArrayList<Long>();
            for (AccountOperation ao : occForMatching) {
                aoIds.add(ao.getId());
            }
            aoIds.add(automatedPayment.getId());
            occForMatching.add(automatedPayment);
            matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIds, null, MatchingTypeEnum.A);
            log.info("matching created  for 1 automatedPayment ");
        } else {
            log.info("no matching created ");
        }
        log.info("automatedPayment created for amount:" + automatedPayment.getAmount());
        return automatedPayment;
    }

    /**
     * Reject payment.
     *
     * @param ddRequestItem the dd request item
     * @param rejectCause the reject cause
     * @throws BusinessException the business exception
     */
    public void rejectPayment(DDRequestItem ddRequestItem, String rejectCause) throws BusinessException {

        AutomatedPayment automatedPayment = ddRequestItem.getAutomatedPayment();
        log.debug("automatedPayment.getAccountingCode():" + automatedPayment.getAccountingCode().getCode());
        matchingCodeService.unmatching(automatedPayment.getMatchingAmounts().get(0).getMatchingCode().getId());

        automatedPayment.setMatchingStatus(MatchingStatusEnum.R);
        automatedPayment.setComment(rejectCause);
        automatedPaymentService.updateNoCheck(automatedPayment);
    }

    /**
     * Process reject file.
     *
     * @param ddRejectFileInfos the dd reject file infos
     * @throws BusinessException the business exception
     */
    public void processRejectFile(DDRejectFileInfos ddRejectFileInfos) throws BusinessException {
        DDRequestLOT dDRequestLOT = null;
        if (!StringUtils.isBlank(ddRejectFileInfos.getDdRequestLotId())) {
            dDRequestLOT = dDRequestLOTService.findById(Long.valueOf(ddRejectFileInfos.getDdRequestLotId()));
        }
        if (dDRequestLOT == null) {
            throw new BusinessException("DDRequestLOT doesn't exist. id=" + ddRejectFileInfos.getDdRequestLotId());
        }

        if (ddRejectFileInfos.isTheDDRequestFileWasRejected()) {
            // original message rejected at protocol level control
            for (DDRequestItem ddRequestItem : dDRequestLOT.getDdrequestItems()) {
                if (!ddRequestItem.hasError()) {
                    rejectPayment(ddRequestItem, "RJCT");
                }
            }
            dDRequestLOT.setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
        } else {
            for (Entry<String, String> entry : ddRejectFileInfos.getListInvoiceRefsRejected().entrySet()) {
                RecordedInvoice invoice = recordedInvoiceService.getRecordedInvoice(entry.getKey());

                // rejectPayment(invoice, entry.getValue());
                // rejectPayment(invoice, entry.getValue());
                // rejectPayment(invoice, entry.getValue());
                // rejectPayment(invoice, entry.getValue());
                // rejectPayment(invoice, entry.getValue());
                // rejectPayment(invoice, entry.getValue());

            }
        }
        dDRequestLOT.setReturnFileName(ddRejectFileInfos.getFileName());
        dDRequestLOTService.updateNoCheck(dDRequestLOT);
    }

    /**
     * Gets the missing field.
     *
     * @param accountOperation the account operation
     * @return the missing field
     */
    public String getMissingField(AccountOperation accountOperation) {

        CustomerAccount ca = accountOperation.getCustomerAccount();
        if (ca == null) {
            return "recordedInvoice.ca";
        }
        if (ca.getName() == null) {
            return "ca.name";
        }
        PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            if (((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification() == null) {
                return "paymentMethod.mandateIdentification";
            }
            if (((DDPaymentMethod) preferedPaymentMethod).getMandateDate() == null) {
                return "paymentMethod.mandateDate";
            }
        } else {
            return "DDPaymentMethod";
        }

        if (accountOperation.getUnMatchingAmount() == null) {
            return "invoice.amount";
        }
        if (StringUtils.isBlank(appProvider.getDescription())) {
            return "provider.description";
        }
        BankCoordinates providerBC = appProvider.getBankCoordinates();
        if (providerBC == null) {
            return "provider.bankCoordinates";
        }
        if (providerBC.getIban() == null) {
            return "provider.iban";
        }
        if (providerBC.getBic() == null) {
            return "provider.bic";
        }
        if (providerBC.getIcs() == null) {
            return "provider.ics";
        }
        if (accountOperation.getReference() == null) {
            return "recordedInvoice.reference";
        }
        if (ca.getDescription() == null) {
            return "ca.description";
        }
        return null;
    }

    /**
     * Creates the DD request item.
     *
     * @param amountToPay the amount to pay
     * @param ddRequestLOT the dd request LOT
     * @param caFullName the ca full name
     * @param errorMsg the error msg
     * @param listAO the list AO
     * @return the DD request item
     * @throws BusinessException the business exception
     */
    private DDRequestItem createDDRequestItem(BigDecimal amountToPay, DDRequestLOT ddRequestLOT, String caFullName, String errorMsg, List<AccountOperation> listAO)
            throws BusinessException {
        DDRequestItem ddDequestItem = new DDRequestItem();
        ddDequestItem.setErrorMsg(errorMsg);
        ddDequestItem.setAmount(amountToPay);
        ddDequestItem.setDdRequestLOT(ddRequestLOT);
        ddDequestItem.setBillingAccountName(caFullName);
        ddDequestItem.setDueDate(listAO.get(0).getDueDate());
        ddDequestItem.setPaymentInfo(listAO.get(0).getPaymentInfo());
        ddDequestItem.setPaymentInfo1(listAO.get(0).getPaymentInfo1());
        ddDequestItem.setPaymentInfo2(listAO.get(0).getPaymentInfo2());
        ddDequestItem.setPaymentInfo3(listAO.get(0).getPaymentInfo3());
        ddDequestItem.setPaymentInfo4(listAO.get(0).getPaymentInfo4());
        ddDequestItem.setPaymentInfo5(listAO.get(0).getPaymentInfo5());
        ddDequestItem.setAccountOperations(listAO);
        create(ddDequestItem);
        log.info("ddrequestItem: {} amount {} ", ddDequestItem.getId(), amountToPay);
        return ddDequestItem;
    }
}
