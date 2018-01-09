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
package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.model.billing.Sequence;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

@Stateless
public class BillingRunService extends PersistenceService<BillingRun> {

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    private InvoicingAsync invoicingAsync;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private BillingRunExtensionService billingRunExtensionService;

    @Inject
    private ServiceSingleton serviceSingleton;

    public PreInvoicingReportsDTO generatePreInvoicingReports(BillingRun billingRun) throws BusinessException {
        log.debug("start generatePreInvoicingReports.......");

        PreInvoicingReportsDTO preInvoicingReportsDTO = new PreInvoicingReportsDTO();

        preInvoicingReportsDTO.setBillingCycleCode(billingRun.getBillingCycle() != null ? billingRun.getBillingCycle().getCode() : null);
        preInvoicingReportsDTO.setBillingAccountNumber(billingRun.getBillingAccountNumber());
        preInvoicingReportsDTO.setLastTransactionDate(billingRun.getLastTransactionDate());
        preInvoicingReportsDTO.setInvoiceDate(billingRun.getInvoiceDate());
        preInvoicingReportsDTO.setBillableBillingAccountNumber(billingRun.getBillableBillingAcountNumber());
        preInvoicingReportsDTO.setAmoutWitountTax(billingRun.getPrAmountWithoutTax());

        BillingCycle billingCycle = billingRun.getBillingCycle();

        Date startDate = billingRun.getStartDate();
        Date endDate = billingRun.getEndDate();
        endDate = endDate != null ? endDate : new Date();
        List<BillingAccount> billingAccounts = new ArrayList<BillingAccount>();

        if (billingCycle != null) {
            billingAccounts = billingAccountService.findBillingAccounts(billingCycle, startDate, endDate);
        } else {
            String[] baIds = billingRun.getSelectedBillingAccounts().split(",");
            for (String id : Arrays.asList(baIds)) {
                Long baId = Long.valueOf(id);
                billingAccounts.add(billingAccountService.findById(baId));
            }
        }

        log.debug("BA in PreInvoicingReport: {}", billingAccounts.size());
        Integer checkBANumber = 0;
        Integer directDebitBANumber = 0;
        Integer tipBANumber = 0;
        Integer wiretransferBANumber = 0;
        Integer creditDebitCardBANumber = 0;

        Integer checkBillableBANumber = 0;
        Integer directDebitBillableBANumber = 0;
        Integer tipBillableBANumber = 0;
        Integer wiretransferBillableBANumber = 0;
        Integer creditDebitCardBillableBANumber = 0;

        BigDecimal checkBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal directDebitBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal tipBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal wiretransferBillableBAAmountHT = BigDecimal.ZERO;
        BigDecimal creditDebitCardBillableBAAmountHT = BigDecimal.ZERO;

        for (BillingAccount billingAccount : billingAccounts) {

            PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;
            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }
            switch (paymentMethodEnum) {
            case CHECK:
                checkBANumber++;
                break;
            case DIRECTDEBIT:
                directDebitBANumber++;
                break;
            case WIRETRANSFER:
                wiretransferBANumber++;
                break;

            case CARD:
                creditDebitCardBANumber++;
                break;

            default:
                break;
            }

        }

        for (BillingAccount billingAccount : billingRun.getBillableBillingAccounts()) {
            PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
            PaymentMethodEnum paymentMethodEnum = null;
            if (preferedPaymentMethod != null) {
                paymentMethodEnum = preferedPaymentMethod.getPaymentType();
            }
            switch (paymentMethodEnum) {
            case CHECK:
                checkBillableBANumber++;
                checkBillableBAAmountHT = checkBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                break;
            case DIRECTDEBIT:
                directDebitBillableBANumber++;
                directDebitBillableBAAmountHT = directDebitBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                break;
            case WIRETRANSFER:
                wiretransferBillableBANumber++;
                wiretransferBillableBAAmountHT = wiretransferBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());
                break;

            case CARD:
                creditDebitCardBillableBANumber++;
                creditDebitCardBillableBAAmountHT = creditDebitCardBillableBAAmountHT.add(billingAccount.getBrAmountWithoutTax());

            default:
                break;
            }
        }

        preInvoicingReportsDTO.setCheckBANumber(checkBANumber);
        preInvoicingReportsDTO.setCheckBillableBAAmountHT(round(checkBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setCheckBillableBANumber(checkBillableBANumber);
        preInvoicingReportsDTO.setDirectDebitBANumber(directDebitBANumber);
        preInvoicingReportsDTO.setDirectDebitBillableBAAmountHT(round(directDebitBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setDirectDebitBillableBANumber(directDebitBillableBANumber);
        preInvoicingReportsDTO.setTipBANumber(tipBANumber);
        preInvoicingReportsDTO.setTipBillableBAAmountHT(round(tipBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setTipBillableBANumber(tipBillableBANumber);
        preInvoicingReportsDTO.setWiretransferBANumber(wiretransferBANumber);
        preInvoicingReportsDTO.setWiretransferBillableBAAmountHT(round(wiretransferBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setWiretransferBillableBANumber(wiretransferBillableBANumber);
        preInvoicingReportsDTO.setCreditDebitCardBANumber(creditDebitCardBANumber);
        preInvoicingReportsDTO.setCreditDebitCardBillableBAAmountHT(round(creditDebitCardBillableBAAmountHT, 2));
        preInvoicingReportsDTO.setCreditDebitCardBillableBANumber(creditDebitCardBillableBANumber);

        return preInvoicingReportsDTO;
    }

    public PostInvoicingReportsDTO generatePostInvoicingReports(BillingRun billingRun) throws BusinessException {
        log.info("generatePostInvoicingReports billingRun=" + billingRun.getId());
        PostInvoicingReportsDTO postInvoicingReportsDTO = new PostInvoicingReportsDTO();

        BigDecimal globalAmountHT = BigDecimal.ZERO;
        BigDecimal globalAmountTTC = BigDecimal.ZERO;

        Integer positiveInvoicesNumber = 0;
        BigDecimal positiveInvoicesAmountHT = BigDecimal.ZERO;
        BigDecimal positiveInvoicesAmount = BigDecimal.ZERO;
        BigDecimal positiveInvoicesTaxAmount = BigDecimal.ZERO;

        Integer negativeInvoicesNumber = 0;
        BigDecimal negativeInvoicesAmountHT = BigDecimal.ZERO;
        BigDecimal negativeInvoicesTaxAmount = BigDecimal.ZERO;
        BigDecimal negativeInvoicesAmount = BigDecimal.ZERO;

        Integer emptyInvoicesNumber = 0;
        Integer electronicInvoicesNumber = 0;

        Integer checkInvoicesNumber = 0;
        Integer directDebitInvoicesNumber = 0;
        Integer tipInvoicesNumber = 0;
        Integer wiretransferInvoicesNumber = 0;
        Integer creditDebitCardInvoicesNumber = 0;

        BigDecimal checkAmuontHT = BigDecimal.ZERO;
        BigDecimal directDebitAmuontHT = BigDecimal.ZERO;
        BigDecimal tipAmuontHT = BigDecimal.ZERO;
        BigDecimal wiretransferAmuontHT = BigDecimal.ZERO;
        BigDecimal creditDebitCardAmountHT = BigDecimal.ZERO;

        BigDecimal checkAmuont = BigDecimal.ZERO;
        BigDecimal directDebitAmuont = BigDecimal.ZERO;
        BigDecimal tipAmuont = BigDecimal.ZERO;
        BigDecimal wiretransferAmuont = BigDecimal.ZERO;
        BigDecimal creditDebitCardAmount = BigDecimal.ZERO;

        for (Invoice invoice : billingRun.getInvoices()) {

            if (invoice.getAmountWithoutTax() != null && invoice.getAmountWithTax() != null) {
                switch (invoice.getPaymentMethod()) {
                case CHECK:
                    checkInvoicesNumber++;
                    checkAmuontHT = checkAmuontHT.add(invoice.getAmountWithoutTax());
                    checkAmuont = checkAmuont.add(invoice.getAmountWithTax());
                    break;
                case DIRECTDEBIT:
                    directDebitInvoicesNumber++;
                    directDebitAmuontHT = directDebitAmuontHT.add(invoice.getAmountWithoutTax());
                    directDebitAmuont = directDebitAmuont.add(invoice.getAmountWithTax());
                    break;
                case WIRETRANSFER:
                    wiretransferInvoicesNumber++;
                    wiretransferAmuontHT = wiretransferAmuontHT.add(invoice.getAmountWithoutTax());
                    wiretransferAmuont = wiretransferAmuont.add(invoice.getAmountWithTax());
                    break;
                case CARD:
                    creditDebitCardInvoicesNumber++;
                    creditDebitCardAmountHT = creditDebitCardAmountHT.add(invoice.getAmountWithoutTax());
                    creditDebitCardAmount = creditDebitCardAmount.add(invoice.getAmountWithTax());
                    break;

                default:
                    break;
                }
            }

            if (invoice.getAmountWithoutTax() != null && invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0) {
                positiveInvoicesNumber++;
                positiveInvoicesAmountHT = positiveInvoicesAmountHT.add(invoice.getAmountWithoutTax());
                positiveInvoicesTaxAmount = positiveInvoicesTaxAmount.add(invoice.getAmountTax() == null ? BigDecimal.ZERO : invoice.getAmountTax());
                positiveInvoicesAmount = positiveInvoicesAmount.add(invoice.getAmountWithTax());
            } else if (invoice.getAmountWithoutTax() == null || invoice.getAmountWithoutTax().compareTo(BigDecimal.ZERO) == 0) {
                emptyInvoicesNumber++;
            } else {
                negativeInvoicesNumber++;
                negativeInvoicesAmountHT = negativeInvoicesAmountHT.add(invoice.getAmountWithoutTax());
                negativeInvoicesTaxAmount = negativeInvoicesTaxAmount.add(invoice.getAmountTax());
                negativeInvoicesAmount = negativeInvoicesAmount.add(invoice.getAmountWithTax());
            }

            if (invoice.getBillingAccount().getElectronicBilling()) {
                electronicInvoicesNumber++;
            }

            if (invoice.getAmountWithoutTax() != null && invoice.getAmountWithTax() != null) {
                globalAmountHT = globalAmountHT.add(invoice.getAmountWithoutTax());
                globalAmountTTC = globalAmountTTC.add(invoice.getAmountWithTax());
            }

        }

        postInvoicingReportsDTO.setInvoicesNumber(billingRun.getInvoices().size());
        postInvoicingReportsDTO.setCheckAmuont(checkAmuont);
        postInvoicingReportsDTO.setCheckAmuontHT(checkAmuontHT);
        postInvoicingReportsDTO.setCheckInvoicesNumber(checkInvoicesNumber);
        postInvoicingReportsDTO.setDirectDebitAmuont(directDebitAmuont);
        postInvoicingReportsDTO.setDirectDebitAmuontHT(directDebitAmuontHT);
        postInvoicingReportsDTO.setDirectDebitInvoicesNumber(directDebitInvoicesNumber);
        postInvoicingReportsDTO.setElectronicInvoicesNumber(electronicInvoicesNumber);
        postInvoicingReportsDTO.setEmptyInvoicesNumber(emptyInvoicesNumber);

        postInvoicingReportsDTO.setPositiveInvoicesAmountHT(positiveInvoicesAmountHT);
        postInvoicingReportsDTO.setPositiveInvoicesAmount(positiveInvoicesAmount);
        postInvoicingReportsDTO.setPositiveInvoicesTaxAmount(positiveInvoicesTaxAmount);
        postInvoicingReportsDTO.setPositiveInvoicesNumber(positiveInvoicesNumber);

        postInvoicingReportsDTO.setNegativeInvoicesAmountHT(negativeInvoicesAmountHT);
        postInvoicingReportsDTO.setNegativeInvoicesAmount(negativeInvoicesAmount);
        postInvoicingReportsDTO.setNegativeInvoicesTaxAmount(negativeInvoicesTaxAmount);
        postInvoicingReportsDTO.setNegativeInvoicesNumber(negativeInvoicesNumber);

        postInvoicingReportsDTO.setTipAmuont(tipAmuont);
        postInvoicingReportsDTO.setTipAmuontHT(tipAmuontHT);
        postInvoicingReportsDTO.setTipInvoicesNumber(tipInvoicesNumber);
        postInvoicingReportsDTO.setWiretransferAmuont(wiretransferAmuont);
        postInvoicingReportsDTO.setWiretransferAmuontHT(wiretransferAmuontHT);
        postInvoicingReportsDTO.setWiretransferInvoicesNumber(wiretransferInvoicesNumber);

        postInvoicingReportsDTO.setCreditDebitCardAmount(creditDebitCardAmount);
        postInvoicingReportsDTO.setCreditDebitCardAmountHT(creditDebitCardAmountHT);
        postInvoicingReportsDTO.setCreditDebitCardInvoicesNumber(creditDebitCardInvoicesNumber);
        postInvoicingReportsDTO.setGlobalAmount(globalAmountHT);

        return postInvoicingReportsDTO;
    }

    public static BigDecimal round(BigDecimal amount, int decimal) {
        if (amount == null) {
            return null;
        }
        amount = amount.setScale(decimal, RoundingMode.HALF_UP);

        return amount;
    }

    public void cancel(BillingRun billingRun) throws BusinessException {
        billingRun.setStatus(BillingRunStatusEnum.CANCELED);
        update(billingRun);
    }

    @SuppressWarnings("unchecked")
    public void cleanBillingRun(BillingRun billingRun) {
        Query queryTrans = getEntityManager().createQuery("update " + RatedTransaction.class.getName()
                + " set invoice=null,invoiceAgregateF=null,invoiceAgregateR=null,invoiceAgregateT=null,status=:status where billingRun=:billingRun");
        queryTrans.setParameter("billingRun", billingRun);
        queryTrans.setParameter("status", RatedTransactionStatusEnum.OPEN);
        queryTrans.executeUpdate();

        Query queryAgregate = getEntityManager().createQuery("from " + InvoiceAgregate.class.getName() + " where billingRun=:billingRun");
        queryAgregate.setParameter("billingRun", billingRun);
        List<InvoiceAgregate> invoiceAgregates = (List<InvoiceAgregate>) queryAgregate.getResultList();
        for (InvoiceAgregate invoiceAgregate : invoiceAgregates) {

            getEntityManager().remove(invoiceAgregate);
        }
        getEntityManager().flush();

        Query queryInvoices = getEntityManager().createQuery("delete from " + Invoice.class.getName() + " where billingRun=:billingRun");
        queryInvoices.setParameter("billingRun", billingRun);
        queryInvoices.executeUpdate();

        Query queryBA = getEntityManager().createQuery("update " + BillingAccount.class.getName() + " set billingRun=null where billingRun=:billingRun");
        queryBA.setParameter("billingRun", billingRun);
        queryBA.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public boolean isActiveBillingRunsExist() {
        QueryBuilder qb = new QueryBuilder(BillingRun.class, "c");
        qb.startOrClause();
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.NEW);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.PREVALIDATED);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.POSTINVOICED);
        qb.addCriterionEnum("c.status", BillingRunStatusEnum.PREINVOICED);
        qb.endOrClause();
        List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

        return billingRuns != null && billingRuns.size() > 0 ? true : false;
    }

    public void retateBillingRunTransactions(BillingRun billingRun) throws BusinessException {
        for (RatedTransaction ratedTransaction : billingRun.getRatedTransactions()) {
            WalletOperation walletOperation = walletOperationService.findById(ratedTransaction.getWalletOperationId());
            walletOperation.setStatus(WalletOperationStatusEnum.TO_RERATE);
            walletOperationService.update(walletOperation);
        }
    }

    public List<BillingRun> getbillingRuns(BillingRunStatusEnum... status) {
        return getBillingRuns(null, status);
    }

    @SuppressWarnings("unchecked")
    public List<BillingRun> getBillingRuns(String code, BillingRunStatusEnum... status) {

        BillingRunStatusEnum bRStatus;
        log.debug("getbillingRuns ");
        QueryBuilder qb = new QueryBuilder(BillingRun.class, "c", null);

        if (code != null) {
            qb.addCriterion("c.billingCycle.code", "=", code, false);
        }

        qb.startOrClause();
        if (status != null) {
            for (int i = 0; i < status.length; i++) {
                bRStatus = status[i];
                qb.addCriterionEnum("c.status", bRStatus);
            }
        }
        qb.endOrClause();

        List<BillingRun> billingRuns = qb.getQuery(getEntityManager()).getResultList();

        return billingRuns;
    }

    public List<Long> getBillingAccountIds(BillingRun billingRun) {

        BillingCycle billingCycle = billingRun.getBillingCycle();

        log.debug("getBillingAccount ids for billingRun {}", billingRun.getId());

        if (billingCycle != null) {
            Date startDate = billingRun.getStartDate();
            Date endDate = billingRun.getEndDate();

            if (startDate != null && endDate == null) {
                endDate = new Date();
            }

            return billingAccountService.findBillingAccountIds(billingCycle, startDate, endDate);

        } else {
            List<Long> result = new ArrayList<Long>();
            String[] baIds = billingRun.getSelectedBillingAccounts().split(",");

            for (String id : Arrays.asList(baIds)) {
                Long baId = Long.valueOf(id);
                result.add(baId);
            }

            return result;
        }
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void createAgregatesAndInvoice(BillingRun billingRun, long nbRuns, long waitingMillis) throws BusinessException {
        List<Long> billingAccountIds = getEntityManager().createNamedQuery("BillingAccount.listIdsByBillingRunId", Long.class).setParameter("billingRunId", billingRun.getId())
            .getResultList();
        SubListCreator subListCreator = null;

        try {
            subListCreator = new SubListCreator(billingAccountIds, (int) nbRuns);
        } catch (Exception e1) {
            throw new BusinessException("cannot create  agregates and invoice with nbRuns=" + nbRuns);
        }

        List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
        while (subListCreator.isHasNext()) {
            asyncReturns.add(invoicingAsync.createAgregatesAndInvoiceAsync((List<Long>) subListCreator.getNextWorkSet(), billingRun));
            try {
                Thread.sleep(waitingMillis);
            } catch (InterruptedException e) {
                log.error("Failed to create agregates and invoice waiting for thread", e);
                throw new BusinessException(e);
            }
        }
        for (Future<String> futureItsNow : asyncReturns) {
            try {
                futureItsNow.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to create agregates and invoice getting future", e);
                throw new BusinessException(e);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void assignInvoiceNumberAndIncrementBAInvoiceDates(BillingRun billingRun, long nbRuns, long waitingMillis) throws BusinessException {
        List<InvoicesToNumberInfo> invoiceSummary = invoiceService.getInvoicesToNumberSummary(billingRun.getId());
        // Reserve invoice number for each invoice type/seller/invoice date combination
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
            Sequence sequence = serviceSingleton.reserveInvoiceNumbers(invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
                invoicesToNumberInfo.getInvoiceDate(), invoicesToNumberInfo.getNrOfInvoices());
            invoicesToNumberInfo.setNumberingSequence(sequence);
        }

        // Find and process invoices
        for (InvoicesToNumberInfo invoicesToNumberInfo : invoiceSummary) {
            List<Long> invoices = invoiceService.getInvoiceIds(billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(),
                invoicesToNumberInfo.getInvoiceDate());
            // Validate that what was retrieved as summary matches the details
            if (invoices.size() != invoicesToNumberInfo.getNrOfInvoices().intValue()) {
                throw new BusinessException(
                    String.format("Number of invoices retrieved %s dont match the expected number %s for %s/%s/%s/%s", invoices.size(), invoicesToNumberInfo.getNrOfInvoices(),
                        billingRun.getId(), invoicesToNumberInfo.getInvoiceTypeId(), invoicesToNumberInfo.getSellerId(), invoicesToNumberInfo.getInvoiceDate()));
            }

            SubListCreator subListCreator = null;

            try {
                subListCreator = new SubListCreator(invoices, (int) nbRuns);
            } catch (Exception e1) {
                throw new BusinessException("Failed to subdivide an invoice list with nbRuns=" + nbRuns);
            }

            List<Future<String>> asyncReturns = new ArrayList<Future<String>>();
            while (subListCreator.isHasNext()) {
                asyncReturns.add(invoicingAsync.assignInvoiceNumberAndIncrementBAInvoiceDatesAsync((List<Long>) subListCreator.getNextWorkSet(), invoicesToNumberInfo));
                try {
                    Thread.sleep(waitingMillis);
                } catch (InterruptedException e) {
                    log.error("Failed to create agregates and invoice waiting for thread", e);
                    throw new BusinessException(e);
                }
            }
            for (Future<String> futureItsNow : asyncReturns) {
                try {
                    futureItsNow.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Failed to create agregates and invoice getting future", e);
                    throw new BusinessException(e);
                }
            }
        }
    }

    public BillingRun launchExceptionalInvoicing(List<Long> billingAccountIds, Date invoiceDate, Date lastTransactionDate, BillingProcessTypesEnum processType)
            throws BusinessException {
        log.info("launchExceptionelInvoicing...");

        ParamBean param = ParamBean.getInstance();
        String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
        boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
        log.info("launchInvoicing allowManyInvoicing=#", isAllowed);
        if (isActiveBillingRunsExist() && !isAllowed) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.alreadyLunched"));
        }

        BillingRun billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setProcessDate(new Date());
        billingRun.setProcessType(processType);
        String selectedBillingAccounts = "";
        String sep = "";
        boolean isBillable = false;

        if (lastTransactionDate == null) {
            lastTransactionDate = new Date();
        }

        BillingAccount currentBA = null;
        for (Long baId : billingAccountIds) {
            currentBA = billingAccountService.findById(baId);
            if (currentBA == null) {
                throw new BusinessException("BillingAccount whit id=" + baId + " does not exists");
            }
            selectedBillingAccounts = selectedBillingAccounts + sep + baId;
            sep = ",";
            if (!isBillable && ratedTransactionService.isBillingAccountBillable(currentBA, null, lastTransactionDate)) {
                isBillable = true;
            }
        }

        if (!isBillable) {
            throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));
        }
        log.debug("selectedBillingAccounts=" + selectedBillingAccounts);
        billingRun.setSelectedBillingAccounts(selectedBillingAccounts);

        billingRun.setInvoiceDate(invoiceDate);
        billingRun.setLastTransactionDate(lastTransactionDate);
        create(billingRun);
        commit();
        return billingRun;
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void validate(BillingRun billingRun, long nbRuns, long waitingMillis) throws Exception {
        log.debug("validate, billingRun id={} status={}", billingRun.getId(), billingRun.getStatus());

        if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {

            billingRunExtensionService.updateBRAmounts(billingRun.getId());

            List<Long> billingAccountIds = getBillingAccountIds(billingRun);
            log.info("Nb billingAccounts to process={}", (billingAccountIds != null ? billingAccountIds.size() : 0));

            if (billingAccountIds != null && billingAccountIds.size() > 0) {
                int billableBA = 0;
                SubListCreator subListCreator = new SubListCreator(billingAccountIds, (int) nbRuns);
                List<Future<Integer>> asyncReturns = new ArrayList<Future<Integer>>();
                while (subListCreator.isHasNext()) {
                    Future<Integer> count = invoicingAsync.updateBillingAccountTotalAmountsAsync((List<Long>) subListCreator.getNextWorkSet(), billingRun);
                    asyncReturns.add(count);
                    try {
                        Thread.sleep(waitingMillis);
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }

                for (Future<Integer> futureItsNow : asyncReturns) {
                    billableBA += futureItsNow.get().intValue();
                }

                log.info("Total billableBA:" + billableBA);

                billingRunExtensionService.updateBillingRun(billingRun, billingAccountIds.size(), billableBA, BillingRunStatusEnum.PREINVOICED, new Date());

                if (billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC || appProvider.isAutomaticInvoicing()) {
                    log.info("Will proceed to create aggregates and invoice");
                    createAgregatesAndInvoice(billingRun, nbRuns, waitingMillis);
                    billingRunExtensionService.updateBillingRun(billingRun, null, null, BillingRunStatusEnum.POSTINVOICED, null);
                }
            }

        } else if (BillingRunStatusEnum.PREVALIDATED.equals(billingRun.getStatus())) {
            createAgregatesAndInvoice(billingRun, nbRuns, waitingMillis);
            billingRunExtensionService.updateBillingRun(billingRun, null, null, BillingRunStatusEnum.POSTINVOICED, null);

        } else if (BillingRunStatusEnum.POSTVALIDATED.equals(billingRun.getStatus())) {
            assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, nbRuns, waitingMillis);
            billingRunExtensionService.updateBillingRun(billingRun, null, null, BillingRunStatusEnum.VALIDATED, null);
        }
    }

    public void forceValidate(Long billingRunId) throws BusinessException {
        BillingRun billingRun = findById(billingRunId);
        if (billingRun == null) {
            throw new BusinessException("Cant find BillingRun with id:" + billingRunId);
        }
        detach(billingRun);
        log.debug("forceValidate, billingRun status={}", billingRun.getStatus());
        switch (billingRun.getStatus()) {

        case POSTINVOICED:
        case POSTVALIDATED:
            assignInvoiceNumberAndIncrementBAInvoiceDates(billingRun, 1, 0);
            billingRunExtensionService.updateBillingRun(billingRun, null, null, BillingRunStatusEnum.VALIDATED, null);
            break;

        case PREINVOICED:
        case PREVALIDATED:
            createAgregatesAndInvoice(billingRun, 1, 0);
            billingRunExtensionService.updateBillingRun(billingRun, 1, 0, BillingRunStatusEnum.POSTINVOICED, null);
            break;

        case VALIDATED:
        case CANCELED:
        case NEW:
        default:
            throw new BusinessException("BillingRun with status " + billingRun.getStatus() + " cannot be validated");
        }
    }

    public boolean launchInvoicingRejectedBA(BillingRun br) throws BusinessException {
        boolean result = false;
        BillingRun billingRun = new BillingRun();
        billingRun.setStatus(BillingRunStatusEnum.NEW);
        billingRun.setProcessDate(new Date());
        BillingCycle billingCycle = br.getBillingCycle();
        if (billingCycle != null && billingCycle.getInvoiceDateProductionDelay() != null) {
            billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), billingCycle.getInvoiceDateProductionDelay()));
        } else {
            billingRun.setInvoiceDate(br.getProcessDate());
        }
        if (billingCycle != null && billingCycle.getTransactionDateDelay() != null) {
            billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(), billingCycle.getTransactionDateDelay()));
        } else {
            billingRun.setLastTransactionDate(billingRun.getProcessDate());
        }
        billingRun.setProcessType(br.getProcessType());
        String selectedBillingAccounts = "";
        String sep = "";
        for (RejectedBillingAccount ba : br.getRejectedBillingAccounts()) {
            selectedBillingAccounts = selectedBillingAccounts + sep + ba.getId();
            sep = ",";
            if (!result && ratedTransactionService.isBillingAccountBillable(ba.getBillingAccount(), null, billingRun.getLastTransactionDate())) {
                result = true;
                break;
            }
        }
        if (result) {
            log.debug("selectedBillingAccounts=" + selectedBillingAccounts);
            billingRun.setSelectedBillingAccounts(selectedBillingAccounts);
            create(billingRun);
        }
        return result;
    }

}
