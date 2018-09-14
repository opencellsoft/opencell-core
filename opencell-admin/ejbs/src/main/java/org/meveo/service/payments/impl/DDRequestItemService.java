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
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.admin.util.ArConfig;
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
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.filter.FilterService;

/**
 * @author anasseh
 * @author Edward P. Legaspi
 * 
 * @lastModifiedVersion 5.2
 */
@Stateless
public class DDRequestItemService extends PersistenceService<DDRequestItem> {

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private DDRequestLOTService dDRequestLOTService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    private AutomatedPaymentService automatedPaymentService;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private MatchingCodeService matchingCodeService;

    @Inject
    private FilterService filterService;

    @Inject
    private PaymentHistoryService paymentHistoryService;

    public DDRequestLOT createDDRquestLot(Date fromDueDate, Date toDueDate, DDRequestBuilder ddRequestBuilder, Filter filter, String paymentPerAOorCA)
            throws BusinessEntityException, Exception {
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
        dDRequestLOTService.create(ddRequestLOT);
        List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
        int rejectedInvoice = 0;
        int nbItems = 0;
        String allErrors = "";
        for (Long caID : caIds) {
            List<AccountOperation> listAoToPayOrRefund = accountOperationService.getAOsToPay(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate, caID);
            System.out.println("\n\n\n\nn\n\n\n\n\n listAoToPayOrRefund.size:"+(listAoToPayOrRefund == null ? null : listAoToPayOrRefund.size()));
            if ("CA".equals(paymentPerAOorCA)) {
                System.out.println("\n\n\n\nn\n\n\n\n\n 111111111");
                List<Long> aoIds = new ArrayList<Long>();
                BigDecimal amountToPay = BigDecimal.ZERO;
                for (AccountOperation ao : listAoToPayOrRefund) {
                    System.out.println("\n\n\n\nn\n\n\n\n\n 222222222");
                    String errorMsg = getMissingField(ao);
                    aoIds.add(ao.getId());
                    if (errorMsg != null) {
                        System.out.println("\n\n\n\nn\n\n\n\n\n 33333333");
                        allErrors += errorMsg + " ; ";
                        rejectedInvoice++;
                    } else {  
                        System.out.println("\n\n\n\nn\n\n\n\n\n 444444444");
                        amountToPay = amountToPay.add(ao.getUnMatchingAmount());
                    }
                }
                if (!aoIds.isEmpty()) {
                    System.out.println("\n\n\n\nn\n\n\n\n\n 5555555555");
                    DDRequestItem ddRequestItem = createDDRequestItem(amountToPay, ddRequestLOT, "billingAccountName", allErrors, listAoToPayOrRefund);
                    nbItems++;
                    totalAmount = totalAmount.add(ddRequestItem.getAmount());
                    for (AccountOperation ao : listAoToPayOrRefund) {
                        System.out.println("\n\n\n\nn\n\n\n\n\n 66666666666");
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
                    nbItems++;
                    if (errorMsg == null) {
                        rejectedInvoice++;
                    } else {
                        totalAmount = totalAmount.add(ddRequestItem.getAmount());
                    }
                    ao.setDdRequestItem(ddRequestItem);
                }
            }
        }
        System.out.println("\n\n\n\nn\n\n\n\n\n nbItems:"+nbItems);
        System.out.println("\n\n\n\nn\n\n\n\n\n rejectedInvoice:"+rejectedInvoice);
        
        ddRequestLOT.setInvoicesNumber(nbItems);
        ddRequestLOT.setRejectedInvoices(rejectedInvoice);
        if (rejectedInvoice > 0) {
            ddRequestLOT.setRejectedCause(StringUtils.truncate(allErrors, 255, true));
            ddRequestLOT = dDRequestLOTService.updateNoCheck(ddRequestLOT);
        }
        if (nbItems + rejectedInvoice > 0) {
            ddRequestLOT.setDdrequestItems(ddrequestItems);
            ddRequestLOT.setInvoicesAmount(totalAmount);
            ddRequestLOT = dDRequestLOTService.updateNoCheck(ddRequestLOT);

            log.info("ddRequestLOT created , totalAmount: {}", ddRequestLOT.getInvoicesAmount());
            log.info("Successful createDDRquestLot fromDueDate: {} toDueDate: {}", fromDueDate, toDueDate);
        } else {
            throw new BusinessEntityException("No ddRequestItems!");
        }

        return ddRequestLOT;

    }

    public void createPaymentsForDDRequestLot(DDRequestLOT ddRequestLOT) throws BusinessException {
        log.info("createPaymentsForDDRequestLot ddRequestLotId: {}", ddRequestLOT.getId());

        if (ddRequestLOT.isPaymentCreated()) {
            throw new BusinessException("Payment Already created.");
        }

        OCCTemplate directDebitTemplate = oCCTemplateService.getDirectDebitOCCTemplate();
        if (directDebitTemplate == null) {
            throw new BusinessException("OCC doesn't exist. codeParam=bayad.ddrequest.occCode");
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
                    automatedPayment = createPayment(PaymentMethodEnum.DIRECTDEBIT, directDebitTemplate, ddrequestItem.getAmount(),
                        ddrequestItem.getAccountOperations().get(0).getCustomerAccount(), ddrequestItem.getReference(), ddRequestLOT.getFileName(), ddRequestLOT.getSendDate(),
                        DateUtils.addDaysToDate(new Date(), ArConfig.getDateValueAfter()), ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(),
                        ddrequestItem.getAccountOperations(), true, MatchingTypeEnum.A_DERICT_DEBIT);
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

    }

    public AutomatedPayment createPayment(PaymentMethodEnum paymentMethodEnum, OCCTemplate occTemplate, BigDecimal amount, CustomerAccount customerAccount, String reference,
            String bankLot, Date depositDate, Date bankCollectionDate, Date dueDate, Date transactionDate, List<AccountOperation>  occForMatching, boolean isToMatching,
            MatchingTypeEnum matchingTypeEnum) throws BusinessException {
        log.info("create payment for amount:" + amount + " paymentMethodEnum:" + paymentMethodEnum + " isToMatching:" + isToMatching + "  customerAccount:"
                + customerAccount.getCode() + "...");

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
        automatedPayment.setMatchingStatus(MatchingStatusEnum.L);
        automatedPayment.setUnMatchingAmount(BigDecimal.ZERO);
        automatedPayment.setMatchingAmount(amount);

        automatedPaymentService.create(automatedPayment);
        if (isToMatching) {
            MatchingCode matchingCode = new MatchingCode();
            BigDecimal amountToMatch = BigDecimal.ZERO;

            AccountOperation accountOperation = (AccountOperation) occForMatching.get(0);
            amountToMatch = accountOperation.getUnMatchingAmount();
            accountOperation.setMatchingAmount(accountOperation.getMatchingAmount().add(amountToMatch));
            accountOperation.setUnMatchingAmount(accountOperation.getUnMatchingAmount().subtract(amountToMatch));
            accountOperation.setMatchingStatus(MatchingStatusEnum.L);
            accountOperationService.updateNoCheck(accountOperation);

            MatchingAmount matchingAmountSingle = new MatchingAmount();
            matchingAmountSingle.updateAudit(currentUser);
            matchingAmountSingle.setAccountOperation(accountOperation);
            matchingAmountSingle.setMatchingCode(matchingCode);
            matchingAmountSingle.setMatchingAmount(amountToMatch);
            accountOperation.getMatchingAmounts().add(matchingAmountSingle);
            matchingCode.getMatchingAmounts().add(matchingAmountSingle);

            MatchingAmount matchingAmount = new MatchingAmount();
            matchingAmount.updateAudit(currentUser);
            matchingAmount.setAccountOperation(automatedPayment);
            matchingAmount.setMatchingCode(matchingCode);
            matchingAmount.setMatchingAmount(automatedPayment.getAmount());

            automatedPayment.getMatchingAmounts().add(matchingAmount);
            matchingCode.getMatchingAmounts().add(matchingAmount);

            matchingCode.setMatchingAmountDebit(amount);
            matchingCode.setMatchingAmountCredit(amount);
            matchingCode.setMatchingDate(new Date());
            matchingCode.setMatchingType(matchingTypeEnum);
            matchingCodeService.create(matchingCode);
            log.info("matching created  for 1 automatedPayment ");
        } else {
            log.info("no matching created ");
        }
        log.info("automatedPayment created for amount:" + automatedPayment.getAmount());
        return automatedPayment;
    }

    public void rejectPayment(DDRequestItem ddRequestItem, String rejectCause) throws BusinessException {

        AutomatedPayment automatedPayment = ddRequestItem.getAutomatedPayment();
        log.debug("automatedPayment.getAccountingCode():" + automatedPayment.getAccountingCode().getCode());
        matchingCodeService.unmatching(automatedPayment.getMatchingAmounts().get(0).getMatchingCode().getId());

        automatedPayment.setMatchingStatus(MatchingStatusEnum.R);
        automatedPayment.setComment(rejectCause);
        automatedPaymentService.updateNoCheck(automatedPayment);
    }

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
            }
        }
        dDRequestLOT.setReturnFileName(ddRejectFileInfos.getFileName());
        dDRequestLOTService.updateNoCheck(dDRequestLOT);
    }

    public String getMissingField(AccountOperation accountOperation) {

        CustomerAccount ca = accountOperation.getCustomerAccount();
        if (ca == null) {
            return "recordedInvoice.ca";
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

    private DDRequestItem createDDRequestItem(BigDecimal amountToPay, DDRequestLOT ddRequestLOT, String billingAccountName, String errorMsg, List<AccountOperation> listAO)
            throws BusinessException {
        DDRequestItem ddDequestItem = new DDRequestItem();
        ddDequestItem.setErrorMsg(errorMsg);
        ddDequestItem.setAmount(amountToPay);
        ddDequestItem.setBillingAccountName(billingAccountName);
        ddDequestItem.setDdRequestLOT(ddRequestLOT);
        ddDequestItem.setDueDate(listAO.get(0).getDueDate());
        ddDequestItem.setPaymentInfo(listAO.get(0).getPaymentInfo());
        ddDequestItem.setPaymentInfo1(listAO.get(0).getPaymentInfo1());
        ddDequestItem.setPaymentInfo2(listAO.get(0).getPaymentInfo2());
        ddDequestItem.setPaymentInfo3(listAO.get(0).getPaymentInfo3());
        ddDequestItem.setPaymentInfo4(listAO.get(0).getPaymentInfo4());
        ddDequestItem.setPaymentInfo5(listAO.get(0).getPaymentInfo5());

        log.info("ddrequestItem: {} amount {} ", ddDequestItem.getId(), amountToPay);

        create(ddDequestItem);
        return ddDequestItem;
    }
}
