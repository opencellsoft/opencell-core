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
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.RecordedInvoiceCatAgregate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceAgregateService;

/**
 * RecordedInvoice service implementation.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @lastModifiedVersion 4.8
 */
@Stateless
public class RecordedInvoiceService extends PersistenceService<RecordedInvoice> {

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    /**
     * @param recordedInvoiceId recored invoice id
     * @throws BusinessException business exception
     */
    public void addLitigation(Long recordedInvoiceId) throws BusinessException {
        if (recordedInvoiceId == null) {
            throw new BusinessException("recordedInvoiceId is null");
        }
        addLitigation(findById(recordedInvoiceId));
    }

    /**
     * @param recordedInvoice recorded invoice
     * @throws BusinessException business exception.
     */
    public void addLitigation(RecordedInvoice recordedInvoice) throws BusinessException {

        if (recordedInvoice == null) {
            throw new BusinessException("recordedInvoice is null");
        }
        log.info("addLitigation recordedInvoice.Reference:" + recordedInvoice.getReference() + "status:" + recordedInvoice.getMatchingStatus());
        if (recordedInvoice.getMatchingStatus() != MatchingStatusEnum.O) {
            throw new BusinessException("recordedInvoice is not open");
        }
        recordedInvoice.setMatchingStatus(MatchingStatusEnum.I);
        update(recordedInvoice);
        log.info("addLitigation recordedInvoice.Reference:" + recordedInvoice.getReference() + " ok");
    }

    /**
     * @param recordedInvoiceId recored invoice id
     * @throws BusinessException business exception.
     */
    public void cancelLitigation(Long recordedInvoiceId) throws BusinessException {
        if (recordedInvoiceId == null) {
            throw new BusinessException("recordedInvoiceId is null");
        }
        cancelLitigation(findById(recordedInvoiceId));
    }

    /**
     * @param recordedInvoice recored invoice
     * @throws BusinessException business exception.
     */
    public void cancelLitigation(RecordedInvoice recordedInvoice) throws BusinessException {

        if (recordedInvoice == null) {
            throw new BusinessException("recordedInvoice is null");
        }
        log.info("cancelLitigation recordedInvoice.Reference:" + recordedInvoice.getReference());
        if (recordedInvoice.getMatchingStatus() != MatchingStatusEnum.I) {
            throw new BusinessException("recordedInvoice is not on Litigation");
        }
        recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
        update(recordedInvoice);
        log.info("cancelLitigation recordedInvoice.Reference:" + recordedInvoice.getReference() + " ok");
    }

    /**
     * @param reference invoice reference
     * @return true if recored invoice exist
     */
    public boolean isRecordedInvoiceExist(String reference) {
        RecordedInvoice recordedInvoice = getRecordedInvoice(reference);
        return recordedInvoice != null;
    }

    /**
     * @param reference invoice's reference.
     * @return instance of RecoredInvoice.
     */
    public RecordedInvoice getRecordedInvoice(String reference) {
        RecordedInvoice recordedInvoice = null;
        try {
            recordedInvoice = (RecordedInvoice) getEntityManager().createQuery("from " + RecordedInvoice.class.getSimpleName() + " where reference =:reference ")
                .setParameter("reference", reference).getSingleResult();
        } catch (Exception e) {
        }
        return recordedInvoice;
    }

    /**
     * @param customerAccount customer account
     * @param o matching status
     * @param dunningExclusion dunning exclusion
     * @return list of recored invoice.
     */
    @SuppressWarnings("unchecked")
    public List<RecordedInvoice> getRecordedInvoices(CustomerAccount customerAccount, MatchingStatusEnum o, boolean dunningExclusion) {
        List<RecordedInvoice> invoices = new ArrayList<RecordedInvoice>();
        try {
            // FIXME Mbarek use NamedQuery
            invoices = (List<RecordedInvoice>) getEntityManager()
                .createQuery("from " + RecordedInvoice.class.getSimpleName()
                        + " where customerAccount.id=:customerAccountId and matchingStatus=:matchingStatus and excludedFromDunning=:dunningExclusion order by dueDate")
                .setParameter("customerAccountId", customerAccount.getId()).setParameter("matchingStatus", MatchingStatusEnum.O).setParameter("dunningExclusion", dunningExclusion)
                .getResultList();
        } catch (Exception e) {

        }
        return invoices;
    }

    /**
     * @param fromDueDate duedate from which we check
     * @param toDueDate duedate to which we check
     * @param paymentMethodEnum payment method enum
     * @return list of recored invoice
     * @throws Exception exception
     */
    @SuppressWarnings("unchecked")
    public List<RecordedInvoice> getInvoicesToPay(Date fromDueDate, Date toDueDate, PaymentMethodEnum paymentMethodEnum) throws Exception {
        try {
            return (List<RecordedInvoice>) getEntityManager().createNamedQuery("RecordedInvoice.listRecordedInvoiceToPayByDate").setParameter("payMethod", paymentMethodEnum)
                .setParameter("fromDueDate", fromDueDate).setParameter("toDueDate", toDueDate).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param invoice invoice used to generate
     * @throws InvoiceExistException invoice exist exception
     * @throws ImportInvoiceException import invoice exception
     * @throws BusinessException business exception.
     */
    public void generateRecordedInvoice(Invoice invoice) throws InvoiceExistException, ImportInvoiceException, BusinessException {
        if (isRecordedInvoiceExist(invoice.getInvoiceNumber())) {
            throw new InvoiceExistException("Invoice id " + invoice.getId() + " already exist");
        }

        List<CategoryInvoiceAgregate> cats = (List<CategoryInvoiceAgregate>) invoiceAgregateService.listByInvoiceAndType(invoice, "R");
        List<RecordedInvoiceCatAgregate> listRecordedInvoiceCatAgregate = new ArrayList<RecordedInvoiceCatAgregate>();

        BigDecimal amountWithoutTaxForRecordedIncoice = invoice.getAmountWithoutTax();
        BigDecimal amountWithTaxForRecordedIncoice = invoice.getAmountWithTax();
        BigDecimal amountTaxForRecordedIncoice = invoice.getAmountTax();
        
        for (CategoryInvoiceAgregate catAgregate : cats) {
            if (catAgregate.getInvoiceCategory().getOccTemplate() != null) {
                BigDecimal tax = BigDecimal.ZERO, ttc = BigDecimal.ZERO;
                for (SubCategoryInvoiceAgregate sub : catAgregate.getSubCategoryInvoiceAgregates()) {
                    tax = tax.add(sub.getAmountTax());
                    ttc = ttc.add(sub.getAmountWithTax());
                }
                RecordedInvoiceCatAgregate recordedInvoiceCatAgregate = createRecordedInvoice(catAgregate.getAmountWithoutTax(), ttc, tax, null, invoice,
                    catAgregate.getInvoiceCategory().getOccTemplate(), false);
                listRecordedInvoiceCatAgregate.add(recordedInvoiceCatAgregate);
                amountWithoutTaxForRecordedIncoice = amountWithoutTaxForRecordedIncoice.subtract(catAgregate.getAmountWithoutTax()) ;
                amountWithTaxForRecordedIncoice = amountWithTaxForRecordedIncoice.subtract(ttc) ;
                amountTaxForRecordedIncoice = amountTaxForRecordedIncoice.subtract(tax) ;
            }
        }

        RecordedInvoice recordedInvoice = createRecordedInvoice(amountWithoutTaxForRecordedIncoice, amountWithTaxForRecordedIncoice, amountTaxForRecordedIncoice, invoice.getNetToPay(), invoice,
            invoice.getInvoiceType().getOccTemplate(), true);
        create(recordedInvoice);

        for (RecordedInvoiceCatAgregate recordedInvoiceCatAgregate : listRecordedInvoiceCatAgregate) {
            recordedInvoiceCatAgregate.setRecordedInvoice(recordedInvoice);
            create(recordedInvoiceCatAgregate);
        }
    }

    private <T extends RecordedInvoice> T createRecordedInvoice(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, BigDecimal netToPay, Invoice ii,
            OCCTemplate occTemplate, boolean isRecordedIvoince) throws InvoiceExistException, ImportInvoiceException, BusinessException {

        CustomerAccount customerAccount = null;
        T recordedInvoice = null;
        BillingAccount billingAccount = ii.getBillingAccount();

        if (isRecordedIvoince) {
            recordedInvoice = (T) new RecordedInvoice();
            recordedInvoice.setReference(ii.getInvoiceNumber());
            recordedInvoice.setNetToPay(netToPay);

            List<String> orderNums = new ArrayList<String>();
            if (ii.getOrders() != null) {
                for (Order order : ii.getOrders()) {
                    if (order != null) {
                        orderNums.add(order.getOrderNumber());
                    }
                }
                recordedInvoice.setOrderNumber(StringUtils.concatenate("|", orderNums));
            }
        } else {
            recordedInvoice = (T) new RecordedInvoiceCatAgregate();
        }

        if (isRecordedInvoiceExist(ii.getInvoiceNumber())) {
            throw new InvoiceExistException("Invoice id " + ii.getId() + " already exist");
        }

        try {
            customerAccount = billingAccount.getCustomerAccount();
            recordedInvoice.setCustomerAccount(customerAccount);
        } catch (Exception e) {
            log.error("error while getting customer account ", e);
            throw new ImportInvoiceException("Cant find customerAccount");
        }

        if (netToPay != null && netToPay.compareTo(BigDecimal.ZERO) < 0) {
            if (occTemplate == null) {
                throw new ImportInvoiceException("Cant find negative OccTemplate");
            }
            netToPay = netToPay.abs();
            if (amountWithoutTax != null) {
                amountWithoutTax = amountWithoutTax.abs();
            }
            if (amountTax != null) {
                amountTax = amountTax.abs();
            }
            if (amountWithTax != null) {
                amountWithTax = amountWithTax.abs();
            }
        }

        recordedInvoice.setAccountingCode(occTemplate.getAccountingCode());
        recordedInvoice.setOccCode(occTemplate.getCode());
        recordedInvoice.setOccDescription(occTemplate.getDescription());
        recordedInvoice.setTransactionCategory(occTemplate.getOccCategory());
        recordedInvoice.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());

        recordedInvoice.setAmount(amountWithTax);
        recordedInvoice.setUnMatchingAmount(amountWithTax);
        recordedInvoice.setMatchingAmount(BigDecimal.ZERO);

        recordedInvoice.setAmountWithoutTax(amountWithoutTax);
        recordedInvoice.setTaxAmount(amountTax);

        try {
            recordedInvoice.setDueDate(DateUtils.setTimeToZero(ii.getDueDate()));
        } catch (Exception e) {
            log.error("error with due date ", e);
            throw new ImportInvoiceException("Error on DueDate");
        }

        try {
            recordedInvoice.setInvoiceDate(DateUtils.setTimeToZero(ii.getInvoiceDate()));
            recordedInvoice.setTransactionDate(DateUtils.setTimeToZero(ii.getInvoiceDate()));
        } catch (Exception e) {
            log.error("error with invoice date", e);
            throw new ImportInvoiceException("Error on invoiceDate");
        }

        PaymentMethod preferedPaymentMethod = billingAccount.getCustomerAccount().getPreferredPaymentMethod();
        if (preferedPaymentMethod != null) {
            recordedInvoice.setPaymentMethod(preferedPaymentMethod.getPaymentType());
            BankCoordinates bankCoordiates = null;
            if (preferedPaymentMethod instanceof DDPaymentMethod) {
                bankCoordiates = ((DDPaymentMethod) preferedPaymentMethod).getBankCoordinates();
            }
            if (bankCoordiates != null) {
                recordedInvoice.setPaymentInfo(bankCoordiates.getIban());
                recordedInvoice.setPaymentInfo1(bankCoordiates.getBankCode());
                recordedInvoice.setPaymentInfo2(bankCoordiates.getBranchCode());
                recordedInvoice.setPaymentInfo3(bankCoordiates.getAccountNumber());
                recordedInvoice.setPaymentInfo4(bankCoordiates.getKey());
                recordedInvoice.setPaymentInfo5(bankCoordiates.getBankName());
                recordedInvoice.setPaymentInfo6(bankCoordiates.getBic());
                recordedInvoice.setBillingAccountName(bankCoordiates.getAccountOwner());
            }
        }
        recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);

        return recordedInvoice;
    }
}