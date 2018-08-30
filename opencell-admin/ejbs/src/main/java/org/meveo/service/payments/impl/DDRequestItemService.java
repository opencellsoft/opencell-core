package org.meveo.service.payments.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.jaxb.Pain002;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlGrpInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts;
import org.meveo.admin.sepa.jaxb.Pain002.CstmrPmtStsRpt.OrgnlPmtInfAndSts.TxInfAndSts;
import org.meveo.admin.util.ArConfig;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
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
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

/*
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
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

    public DDRequestLOT createDDRquestLot(Date fromDueDate, Date toDueDate, DDRequestBuilder ddRequestBuilder) throws BusinessEntityException, Exception {
        log.info("createDDRquestLot fromDueDate: {}   toDueDate: {}", fromDueDate, toDueDate);

		if (fromDueDate == null) {
			throw new BusinessEntityException("fromDuDate is empty");
		}
		if (toDueDate == null) {
			throw new BusinessEntityException("toDueDate is empty");
		}
		if (fromDueDate.after(toDueDate)) {
			throw new BusinessEntityException("fromDueDate is after toDueDate");
		}
		List<RecordedInvoice> recordedInvoices = recordedInvoiceService.getInvoicesToPay(fromDueDate, toDueDate,PaymentMethodEnum.DIRECTDEBIT);
		if ((recordedInvoices == null) || (recordedInvoices.isEmpty())) {
			throw new BusinessEntityException("no invoices!");
		}

        log.info("number invoices: {}", recordedInvoices.size());

        BigDecimal totalAmount = BigDecimal.ZERO;

        DDRequestLOT ddRequestLOT = new DDRequestLOT();
        ddRequestLOT.setDdRequestBuilder(ddRequestBuilder);
        ddRequestLOT.setInvoicesNumber(Integer.valueOf(recordedInvoices.size()));
        dDRequestLOTService.create(ddRequestLOT);
        List<DDRequestItem> ddrequestItems = new ArrayList<DDRequestItem>();
        int rejectedInvoice = 0;
        String allErrors = "";
        for (RecordedInvoice recordedInvoice : recordedInvoices) {
            String errorMsg = null;
            BigDecimal amountToPay = recordedInvoice.getNetToPay();
            if (amountToPay == null) {
                try {
                    amountToPay = customerAccountService.customerAccountBalanceDueWithoutLitigation(recordedInvoice.getCustomerAccount().getId(), null,
                        recordedInvoice.getDueDate());
                } catch (Exception e) {
                    errorMsg = "cant compute BalanceDueWithoutLitigation ";
                    log.error(errorMsg, e);
                }
            } else {
                if (BigDecimal.ZERO.compareTo(amountToPay) == 0) {
                    errorMsg = "amountToPay = 0";
                } else {
                    errorMsg = getMissingField(recordedInvoice);
                }
            }
            DDRequestItem ddrequestItem = new DDRequestItem();
            ddrequestItem.setErrorMsg(errorMsg);
            ddrequestItem.setAmount(amountToPay);
            ddrequestItem.setBillingAccountName(recordedInvoice.getBillingAccountName());
            ddrequestItem.setDdRequestLOT(ddRequestLOT);
            ddrequestItem.setDueDate(recordedInvoice.getDueDate());
            ddrequestItem.setPaymentInfo(recordedInvoice.getPaymentInfo());
            ddrequestItem.setPaymentInfo1(recordedInvoice.getPaymentInfo1());
            ddrequestItem.setPaymentInfo2(recordedInvoice.getPaymentInfo2());
            ddrequestItem.setPaymentInfo3(recordedInvoice.getPaymentInfo3());
            ddrequestItem.setPaymentInfo4(recordedInvoice.getPaymentInfo4());
            ddrequestItem.setPaymentInfo5(recordedInvoice.getPaymentInfo5());
            ddrequestItem.setReference(recordedInvoice.getReference());
            ddrequestItem.setRecordedInvoice(recordedInvoice);
            log.info("ddrequestItem: {} amount {} ", ddrequestItem.getId(), amountToPay);

            create(ddrequestItem);
            ddrequestItems.add(ddrequestItem);

            if (errorMsg != null) {
                allErrors += errorMsg + " ; ";
                rejectedInvoice++;
            } else {
                totalAmount = totalAmount.add(ddrequestItem.getAmount());
            }
        }
        ddRequestLOT.setRejectedInvoices(rejectedInvoice);

        if (rejectedInvoice > 0) {
            ddRequestLOT.setRejectedCause(StringUtils.truncate(allErrors, 255, true));
            ddRequestLOT = dDRequestLOTService.updateNoCheck(ddRequestLOT);
        }

        if (!ddrequestItems.isEmpty()) {
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
            if (!ddrequestItem.hasError()) {
                if (BigDecimal.ZERO.compareTo(ddrequestItem.getAmount()) == 0) {
                    log.info("invoice: {}  balanceDue:{}  no DIRECTDEBIT transaction", ddrequestItem.getReference(), BigDecimal.ZERO);
                } else {
                    AutomatedPayment automatedPayment = createPayment(PaymentMethodEnum.DIRECTDEBIT, directDebitTemplate, ddrequestItem.getAmount(),
                        ddrequestItem.getRecordedInvoice().getCustomerAccount(), ddrequestItem.getReference(), ddRequestLOT.getFileName(), ddRequestLOT.getSendDate(),
                        DateUtils.addDaysToDate(new Date(), ArConfig.getDateValueAfter()), ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(),
                        ddrequestItem.getRecordedInvoice(), true, MatchingTypeEnum.A_DERICT_DEBIT);
                    ddrequestItem.setAutomatedPayment(automatedPayment);
                    updateNoCheck(ddrequestItem);

                }
            }
        }
        ddRequestLOT.setPaymentCreated(true);
        dDRequestLOTService.updateNoCheck(ddRequestLOT);

        log.info("Successful createPaymentsForDDRequestLot ddRequestLotId: {}", ddRequestLOT.getId());

    }

    public AutomatedPayment createPayment(PaymentMethodEnum paymentMethodEnum, OCCTemplate occTemplate, BigDecimal amount, CustomerAccount customerAccount, String reference,
            String bankLot, Date depositDate, Date bankCollectionDate, Date dueDate, Date transactionDate, RecordedInvoice occForMatching, boolean isToMatching,
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

            AccountOperation accountOperation = (AccountOperation) occForMatching;
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

    public void rejectPayment(RecordedInvoice recordedInvoice, String rejectCause) throws BusinessException {

        AutomatedPayment automatedPayment = recordedInvoice.getPayedDDRequestItem().getAutomatedPayment();
        log.debug("automatedPayment.getAccountingCode():" + automatedPayment.getAccountingCode().getCode());
        matchingCodeService.unmatching(recordedInvoice.getMatchingAmounts().get(0).getMatchingCode().getId());

        automatedPayment.setMatchingStatus(MatchingStatusEnum.R);
        automatedPaymentService.updateNoCheck(automatedPayment);
    }

    // TODO remove coupling between busines rules and file format
    public void processRejectFile(File file, String fileName) throws JAXBException, Exception {
        Pain002 pain002 = (Pain002) JAXBUtils.unmarshaller(Pain002.class, file);

        CstmrPmtStsRpt cstmrPmtStsRpt = pain002.getCstmrPmtStsRpt();

        OrgnlGrpInfAndSts orgnlGrpInfAndSts = cstmrPmtStsRpt.getOrgnlGrpInfAndSts();

        if (orgnlGrpInfAndSts == null) {
            throw new Exception("OriginalGroupInformationAndStatus tag doesn't exist");
        }
        String dDRequestLOTref = orgnlGrpInfAndSts.getOrgnlMsgId();
        if (dDRequestLOTref == null || dDRequestLOTref.indexOf("-") < 0) {
            throw new Exception("Unknown dDRequestLOTref:" + dDRequestLOTref);
        }
        String[] dDRequestLOTrefSplited = dDRequestLOTref.split("-");
        DDRequestLOT dDRequestLOT = dDRequestLOTService.findById(Long.valueOf(dDRequestLOTrefSplited[1]));
        if (dDRequestLOT == null) {
            throw new Exception("DDRequestLOT doesn't exist. id=" + dDRequestLOTrefSplited[1]);
        }
        if (orgnlGrpInfAndSts.getGrpSts() != null && "RJCT".equals(orgnlGrpInfAndSts.getGrpSts())) {
            // original message rejected at protocol level control

            for (DDRequestItem ddRequestItem : dDRequestLOT.getDdrequestItems()) {
                if (!ddRequestItem.hasError()) {
                    rejectPayment(ddRequestItem.getRecordedInvoice(), "RJCT");
                }
            }

            dDRequestLOT.setReturnStatusCode(orgnlGrpInfAndSts.getStsRsnInf().getRsn().getCd());
        } else {
            OrgnlPmtInfAndSts orgnlPmtInfAndSts = cstmrPmtStsRpt.getOrgnlPmtInfAndSts();
            for (TxInfAndSts txInfAndSts : orgnlPmtInfAndSts.getTxInfAndSts()) {
                if ("RJCT".equals(txInfAndSts.getTxSts())) {
                    RecordedInvoice invoice = recordedInvoiceService.getRecordedInvoice(txInfAndSts.getOrgnlEndToEndId());
                    rejectPayment(invoice, "RJCT");
                }
            }
        }
        dDRequestLOT.setReturnFileName(file.getName());
        dDRequestLOTService.updateNoCheck(dDRequestLOT);
    }

    public String getMissingField(RecordedInvoice recordedInvoice) {

        CustomerAccount ca = recordedInvoice.getCustomerAccount();
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

        if (recordedInvoice == null || recordedInvoice.getAmount() == null) {
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
        if (recordedInvoice.getReference() == null) {
            return "recordedInvoice.reference";
        }
        if (ca.getDescription() == null) {
            return "ca.description";
        }
        return null;
    }

}
