/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

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
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.filter.FilterService;

/**
 * The Class DDRequestLOTService.
 */
@Stateless
public class DDRequestLOTService extends PersistenceService<DDRequestLOT> {

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

    /** The dd request builder factory. */
    @Inject
    private DDRequestBuilderFactory ddRequestBuilderFactory;

    /** The dd request item service. */
    @Inject
    private DDRequestItemService ddRequestItemService;

    /**
     * Creates the payment.
     *
     * @param ddRequestItem the dd request item
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
    public AutomatedPayment createPayment(DDRequestItem ddRequestItem, PaymentMethodEnum paymentMethodEnum, BigDecimal amount, CustomerAccount customerAccount, String reference,
            String bankLot, Date depositDate, Date bankCollectionDate, Date dueDate, Date transactionDate, List<AccountOperation> occForMatching, boolean isToMatching,
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
        automatedPayment.setDdRequestItem(ddRequestItem);
        automatedPaymentService.create(automatedPayment);
        if (isToMatching) {
            List<Long> aoIds = new ArrayList<Long>();
            for (AccountOperation ao : occForMatching) {
                aoIds.add(ao.getId());
            }
            aoIds.add(automatedPayment.getId());
            matchingCodeService.matchOperations(null, customerAccount.getCode(), aoIds, null, MatchingTypeEnum.A);
            log.info("matching created  for 1 automatedPayment ");
        } else {
            log.info("no matching created ");
        }
        log.info("automatedPayment created for amount:" + automatedPayment.getAmount());
        return automatedPayment;
    }

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
        List<AccountOperation> listAoToPay = null;
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
            listAoToPay = accountOperationService.getAOsToPay(PaymentMethodEnum.DIRECTDEBIT, fromDueDate, toDueDate);
        } else {
            listAoToPay = (List<AccountOperation>) filterService.filteredListAsObjects(filter);
        }

        if (listAoToPay == null || listAoToPay.isEmpty()) {
            throw new BusinessEntityException("no invoices!");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        DDRequestLOT ddRequestLOT = new DDRequestLOT();
        ddRequestLOT.setDdRequestBuilder(ddRequestBuilder);
        ddRequestLOT.setSendDate(new Date());
        create(ddRequestLOT);
        int nbItemsKo = 0;
        int nbItemsOk = 0;
        String allErrors = "";
        DDRequestBuilderInterface ddRequestBuilderInterface = ddRequestBuilderFactory.getInstance(ddRequestBuilder);
        if (ddRequestBuilder.getPaymentLevel() == PaymentLevelEnum.AO) {
            for (AccountOperation ao : listAoToPay) {
                String errorMsg = getMissingField(ao);
                ddRequestLOT.getDdrequestItems().add(
                    ddRequestItemService.createDDRequestItem(ao.getUnMatchingAmount(), ddRequestLOT, ao.getCustomerAccount().getName().getFullName(), errorMsg, Arrays.asList(ao)));
                if (errorMsg != null) {
                    nbItemsKo++;
                    allErrors += errorMsg + " ; ";
                } else {
                    nbItemsOk++;
                    totalAmount = totalAmount.add(ao.getUnMatchingAmount());
                }
            }
        }
        if (ddRequestBuilder.getPaymentLevel() == PaymentLevelEnum.CA) {
            Map<CustomerAccount, List<AccountOperation>> aosByCA = new HashMap<CustomerAccount, List<AccountOperation>>();
            for (AccountOperation ao : listAoToPay) {
                List<AccountOperation> aos = new ArrayList<AccountOperation>();
                if (aosByCA.containsKey(ao.getCustomerAccount())) {
                    aos = aosByCA.get(ao.getCustomerAccount());
                }
                aos.add(ao);
                aosByCA.put(ao.getCustomerAccount(), aos);
            }
            for (Map.Entry<CustomerAccount, List<AccountOperation>> entry : aosByCA.entrySet()) {
                BigDecimal amountToPayByItem = BigDecimal.ZERO;
                String allErrorsByItem = "";
                String caFullName = entry.getKey().getName().getFirstName();
                for (AccountOperation ao : entry.getValue()) {
                    String errorMsg = getMissingField(ao);
                    if (errorMsg != null) {
                        allErrorsByItem += errorMsg + " ; ";
                    } else {
                        amountToPayByItem = amountToPayByItem.add(ao.getUnMatchingAmount());
                    }
                }
                ddRequestLOT.getDdrequestItems().add(ddRequestItemService.createDDRequestItem(amountToPayByItem, ddRequestLOT, caFullName, allErrorsByItem, entry.getValue()));
                if (StringUtils.isBlank(allErrorsByItem)) {
                    nbItemsOk++;
                    totalAmount = totalAmount.add(amountToPayByItem);
                } else {
                    nbItemsKo++;
                    allErrors += allErrorsByItem + " ; ";
                }
            }
        }
        ddRequestLOT.setNbItemsKo(nbItemsKo);
        ddRequestLOT.setNbItemsOk(nbItemsOk);
        ddRequestLOT.setRejectedCause(StringUtils.truncate(allErrors, 255, true));
        ddRequestLOT.setTotalAmount(totalAmount);
        ddRequestLOT.setFileName(ddRequestBuilderInterface.getDDFileName(ddRequestLOT, appProvider));
        ddRequestBuilderInterface.generateDDRequestLotFile(ddRequestLOT, appProvider);
        ddRequestLOT.setSendDate(new Date());
        log.info("Successful createDDRquestLot fromDueDate: {} toDueDate: {} totalAmount: {}", fromDueDate, toDueDate, ddRequestLOT.getTotalAmount());
        return ddRequestLOT;
    }

    /**
     * Creates the payments for DD request lot.
     *
     * @param ddRequestLOT the dd request LOT
     * @throws BusinessException the business exception
     * @throws NoAllOperationUnmatchedException the no all operation unmatched exception
     * @throws UnbalanceAmountException the unbalance amount exception
     */
    public void createPaymentsForDDRequestLot(DDRequestLOT ddRequestLOT) throws BusinessException, NoAllOperationUnmatchedException, UnbalanceAmountException {
        log.info("createPaymentsForDDRequestLot ddRequestLotId: {}, size:{}", ddRequestLOT.getId(), ddRequestLOT.getDdrequestItems().size());
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
                    automatedPayment = createPayment(ddrequestItem, PaymentMethodEnum.DIRECTDEBIT, ddrequestItem.getAmount(),
                        ddrequestItem.getAccountOperations().get(0).getCustomerAccount(), ddrequestItem.getReference(), ddRequestLOT.getFileName(), ddRequestLOT.getSendDate(),
                        DateUtils.addDaysToDate(new Date(), ArConfig.getDateValueAfter()), ddRequestLOT.getSendDate(), ddRequestLOT.getSendDate(),
                        ddrequestItem.getAccountOperations(), true, MatchingTypeEnum.A_DERICT_DEBIT);
                    ddrequestItem.setAutomatedPayment(automatedPayment);
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
        log.info("Successful createPaymentsForDDRequestLot ddRequestLotId: {}", ddRequestLOT.getId());

    }

    /**
     * Gets the missing field.
     *
     * @param accountOperation the account operation
     * @return the missing field
     */
    public String getMissingField(AccountOperation accountOperation) {
        String prefix = "AO.id:" + accountOperation.getId() + " : ";
        CustomerAccount ca = accountOperation.getCustomerAccount();
        if (ca == null) {
            return prefix + "recordedInvoice.ca";
        }
        if (ca.getName() == null) {
            return prefix + "ca.name";
        }
        PaymentMethod preferedPaymentMethod = ca.getPreferredPaymentMethod();
        if (preferedPaymentMethod != null && preferedPaymentMethod instanceof DDPaymentMethod) {
            if (((DDPaymentMethod) preferedPaymentMethod).getMandateIdentification() == null) {
                return prefix + "paymentMethod.mandateIdentification";
            }
            if (((DDPaymentMethod) preferedPaymentMethod).getMandateDate() == null) {
                return prefix + "paymentMethod.mandateDate";
            }
        } else {
            return prefix + "DDPaymentMethod";
        }

        if (accountOperation.getUnMatchingAmount() == null) {
            return prefix + "invoice.amount";
        }
        if (StringUtils.isBlank(appProvider.getDescription())) {
            return prefix + "provider.description";
        }
        BankCoordinates providerBC = appProvider.getBankCoordinates();
        if (providerBC == null) {
            return prefix + "provider.bankCoordinates";
        }
        if (providerBC.getIban() == null) {
            return prefix + "provider.iban";
        }
        if (providerBC.getBic() == null) {
            return prefix + "provider.bic";
        }
        if (providerBC.getIcs() == null) {
            return prefix + "provider.ics";
        }
        if (accountOperation.getReference() == null) {
            return prefix + "recordedInvoice.reference";
        }
        if (ca.getDescription() == null) {
            return prefix + "ca.description";
        }
        return null;
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
        if (automatedPayment.getMatchingAmounts() != null && !automatedPayment.getMatchingAmounts().isEmpty()) {
            matchingCodeService.unmatching(automatedPayment.getMatchingAmounts().get(0).getMatchingCode().getId());
        }

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
            dDRequestLOT = findById(Long.valueOf(ddRejectFileInfos.getDdRequestLotId()));
        }
        if (dDRequestLOT == null) {
            throw new BusinessException("DDRequestLOT doesn't exist. id=" + ddRejectFileInfos.getDdRequestLotId());
        }

        if (ddRejectFileInfos.isTheDDRequestFileWasRejected()) {
            // original message rejected at protocol level control
            CopyOnWriteArrayList<DDRequestItem> items = new CopyOnWriteArrayList<>(dDRequestLOT.getDdrequestItems());
            for (DDRequestItem ddRequestItem : items) {
                if (!ddRequestItem.hasError()) {
                    rejectPayment(ddRequestItem, "RJCT");
                }
            }
            dDRequestLOT.setReturnStatusCode(ddRejectFileInfos.getReturnStatusCode());
        } else {
            for (Entry<String, String> entry : ddRejectFileInfos.getListInvoiceRefsRejected().entrySet()) {
                DDRequestItem ddRequestItem = ddRequestItemService.findById(new Long(entry.getKey()));
                rejectPayment(ddRequestItem, entry.getValue());
            }
        }
        dDRequestLOT.setReturnFileName(ddRejectFileInfos.getFileName());        
    }
}
