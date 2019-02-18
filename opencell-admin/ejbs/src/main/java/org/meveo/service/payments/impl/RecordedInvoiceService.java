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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.RecordedInvoiceCatAgregate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.InvoiceAgregateService;

/**
 * RecordedInvoice service implementation.
 * 
 * @author Edward P. Legaspi
 * @author anasseh
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2.1
 */
@Stateless
public class RecordedInvoiceService extends PersistenceService<RecordedInvoice> {

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    private OCCTemplateService occTemplateService;

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

            if (dunningExclusion) {
                invoices = (List<RecordedInvoice>) getEntityManager().createQuery("from " + RecordedInvoice.class.getSimpleName()
                        + " where customerAccount.id=:customerAccountId and matchingStatus= " + MatchingStatusEnum.I + " order by dueDate")
                    .setParameter("customerAccountId", customerAccount.getId()).getResultList();
            } else {
                invoices = (List<RecordedInvoice>) getEntityManager()
                    .createQuery(
                        "from " + RecordedInvoice.class.getSimpleName() + " where customerAccount.id=:customerAccountId and matchingStatus=:matchingStatus order by dueDate")
                    .setParameter("customerAccountId", customerAccount.getId()).setParameter("matchingStatus", o).getResultList();
            }

        } catch (Exception e) {

        }
        return invoices;
    }

    /**
     * @param expression EL expression
     * @param invoice invoice
     * @param billingRun billingRun
     * @return evaluated expression
     * @throws BusinessException business exception
     */
    public String evaluateStringExpression(String expression, Invoice invoice, BillingRun billingRun) throws BusinessException {
        String result = null;
        if (StringUtils.isBlank(expression)) {
            return result;
        }

        Map<Object, Object> userMap = constructElContext(expression, invoice, billingRun);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
        try {
            result = (String) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to string but " + res);
        }
        return result;
    }

    /**
     * @param expression EL expression
     * @param invoice invoice
     * @param billingRun billingRun
     * @return userMap userMap
     */
    private Map<Object, Object> constructElContext(String expression, Invoice invoice, BillingRun billingRun) {

        Map<Object, Object> userMap = new HashMap<Object, Object>();
        BillingAccount billingAccount = invoice.getBillingAccount();

        if (expression.indexOf("invoice") >= 0) {
            userMap.put("invoice", invoice);
        }
        if (expression.indexOf("br") >= 0) {
            userMap.put("br", billingRun);
        }
        if (expression.indexOf("ba") >= 0) {
            userMap.put("ba", billingAccount);
        }
        if (expression.indexOf("ca") >= 0) {
            userMap.put("ca", billingAccount.getCustomerAccount());
        }
        if (expression.indexOf("c") >= 0) {
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf("prov") >= 0) {
            userMap.put("prov", appProvider);
        }

        return userMap;
    }

    /**
     * @param invoice invoice used to generate
     * @throws InvoiceExistException invoice exist exception
     * @throws ImportInvoiceException import invoice exception
     * @throws BusinessException business exception.
     */
    public void generateRecordedInvoice(Invoice invoice) throws InvoiceExistException, ImportInvoiceException, BusinessException {

        @SuppressWarnings("unchecked")
        List<CategoryInvoiceAgregate> cats = (List<CategoryInvoiceAgregate>) invoiceAgregateService.listByInvoiceAndType(invoice, "R");
        List<RecordedInvoiceCatAgregate> listRecordedInvoiceCatAgregate = new ArrayList<RecordedInvoiceCatAgregate>();

        BigDecimal remainingAmountWithoutTaxForRecordedIncoice = invoice.getAmountWithoutTax();
        BigDecimal remainingAmountWithTaxForRecordedIncoice = invoice.getAmountWithTax();
        BigDecimal remainingAmountTaxForRecordedIncoice = invoice.getAmountTax();

        boolean allowMultipleAOperInvoice = "true".equalsIgnoreCase(ParamBean.getInstance().getProperty("ao.generateMultipleAOperInvoice", "true"));
        if (allowMultipleAOperInvoice) {
            for (CategoryInvoiceAgregate catAgregate : cats) {
                BigDecimal remainingAmountWithoutTaxForCat = BigDecimal.ZERO;
                BigDecimal remainingAmountWithTaxForCat = BigDecimal.ZERO;
                BigDecimal remainingAmountTaxForCat = BigDecimal.ZERO;
                for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : catAgregate.getSubCategoryInvoiceAgregates()) {
                    if ((subCategoryInvoiceAgregate.getInvoiceSubCategory().getOccTemplate() != null
                            && subCategoryInvoiceAgregate.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0)
                            || (subCategoryInvoiceAgregate.getInvoiceSubCategory().getOccTemplateNegative() != null
                                    && subCategoryInvoiceAgregate.getAmountWithoutTax().compareTo(BigDecimal.ZERO) < 0)) {
                        RecordedInvoiceCatAgregate recordedInvoiceCatAgregate = createRecordedInvoice(subCategoryInvoiceAgregate.getAmountWithoutTax(),
                            subCategoryInvoiceAgregate.getAmountWithTax(), subCategoryInvoiceAgregate.getAmountTax(), null, invoice,
                            subCategoryInvoiceAgregate.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0 ? subCategoryInvoiceAgregate.getInvoiceSubCategory().getOccTemplate()
                                    : subCategoryInvoiceAgregate.getInvoiceSubCategory().getOccTemplateNegative(),
                            false);
                        recordedInvoiceCatAgregate.setSubCategoryInvoiceAgregate(subCategoryInvoiceAgregate);

                        listRecordedInvoiceCatAgregate.add(recordedInvoiceCatAgregate);
                        remainingAmountWithoutTaxForRecordedIncoice = remainingAmountWithoutTaxForRecordedIncoice.subtract(subCategoryInvoiceAgregate.getAmountWithoutTax());
                        remainingAmountWithTaxForRecordedIncoice = remainingAmountWithTaxForRecordedIncoice.subtract(subCategoryInvoiceAgregate.getAmountWithTax());
                        remainingAmountTaxForRecordedIncoice = remainingAmountTaxForRecordedIncoice.subtract(subCategoryInvoiceAgregate.getAmountTax());
                    } else {
                        remainingAmountWithoutTaxForCat = remainingAmountWithoutTaxForCat.add(subCategoryInvoiceAgregate.getAmountWithoutTax());
                        remainingAmountWithTaxForCat = remainingAmountWithTaxForCat.add(subCategoryInvoiceAgregate.getAmountWithTax());
                        remainingAmountTaxForCat = remainingAmountTaxForCat.add(subCategoryInvoiceAgregate.getAmountTax());
                    }
                }
                if ((catAgregate.getInvoiceCategory().getOccTemplate() != null && catAgregate.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0)
                        || (catAgregate.getInvoiceCategory().getOccTemplateNegative() != null && catAgregate.getAmountWithoutTax().compareTo(BigDecimal.ZERO) < 0)) {
                    RecordedInvoiceCatAgregate recordedInvoiceCatAgregate = createRecordedInvoice(remainingAmountWithoutTaxForCat, remainingAmountWithTaxForCat,
                        remainingAmountTaxForCat, null, invoice,
                        catAgregate.getAmountWithoutTax().compareTo(BigDecimal.ZERO) > 0 ? catAgregate.getInvoiceCategory().getOccTemplate()
                                : catAgregate.getInvoiceCategory().getOccTemplateNegative(),
                        false);
                    recordedInvoiceCatAgregate.setCategoryInvoiceAgregate(catAgregate);
                    listRecordedInvoiceCatAgregate.add(recordedInvoiceCatAgregate);

                    remainingAmountWithoutTaxForRecordedIncoice = remainingAmountWithoutTaxForRecordedIncoice.subtract(remainingAmountWithoutTaxForCat);
                    remainingAmountWithTaxForRecordedIncoice = remainingAmountWithTaxForRecordedIncoice.subtract(remainingAmountWithTaxForCat);
                    remainingAmountTaxForRecordedIncoice = remainingAmountTaxForRecordedIncoice.subtract(remainingAmountTaxForCat);
                }

            }
        }

        OCCTemplate occTemplate = null;
        if (remainingAmountWithTaxForRecordedIncoice.compareTo(BigDecimal.ZERO) < 0) {
            String occTemplateCode = evaluateStringExpression(invoice.getInvoiceType().getOccTemplateNegativeCodeEl(), invoice, invoice.getBillingRun());
            if (!StringUtils.isBlank(occTemplateCode)) {
                occTemplate = occTemplateService.findByCode(occTemplateCode);
            }

            if (occTemplate == null) {
                occTemplate = invoice.getInvoiceType().getOccTemplateNegative();
            }

            if (occTemplate == null) {
                throw new ImportInvoiceException("Cant find negative OccTemplate");
            }
        } else {
            String occTemplateCode = evaluateStringExpression(invoice.getInvoiceType().getOccTemplateCodeEl(), invoice, invoice.getBillingRun());
            if (!StringUtils.isBlank(occTemplateCode)) {
                occTemplate = occTemplateService.findByCode(occTemplateCode);
            }

            if (occTemplate == null) {
                occTemplate = invoice.getInvoiceType().getOccTemplate();
            }

            if (occTemplate == null) {
                throw new ImportInvoiceException("Cant find OccTemplate");
            }
        }

        RecordedInvoice recordedInvoice = createRecordedInvoice(remainingAmountWithoutTaxForRecordedIncoice, remainingAmountWithTaxForRecordedIncoice,
            remainingAmountTaxForRecordedIncoice, invoice.getNetToPay(), invoice, occTemplate, true);

        // Link the recorded invoice to subscription
        recordedInvoice.setSubscription(invoice.getSubscription());

        create(recordedInvoice);

        for (RecordedInvoiceCatAgregate recordedInvoiceCatAgregate : listRecordedInvoiceCatAgregate) {
            recordedInvoiceCatAgregate.setRecordedInvoice(recordedInvoice);
            create(recordedInvoiceCatAgregate);
        }
        invoice.setRecordedInvoice(recordedInvoice);
    }

    @SuppressWarnings("unchecked")
    private <T extends RecordedInvoice> T createRecordedInvoice(BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, BigDecimal netToPay, Invoice invoice,
            OCCTemplate occTemplate, boolean isRecordedIvoince) throws InvoiceExistException, ImportInvoiceException, BusinessException {

        if (isRecordedInvoiceExist((isRecordedIvoince ? "" : "IC_") + invoice.getInvoiceNumber())) {
            throw new InvoiceExistException("Invoice id " + invoice.getId() + " already exist");
        }

        CustomerAccount customerAccount = null;
        T recordedInvoice = null;
        BillingAccount billingAccount = invoice.getBillingAccount();

        if (isRecordedIvoince) {
            recordedInvoice = (T) new RecordedInvoice();
            recordedInvoice.setNetToPay(netToPay);

            List<String> orderNums = new ArrayList<String>();
            if (invoice.getOrders() != null) {
                for (Order order : invoice.getOrders()) {
                    if (order != null) {
                        orderNums.add(order.getOrderNumber());
                    }
                }
                recordedInvoice.setOrderNumber(StringUtils.concatenate("|", orderNums));
            }
        } else {
            recordedInvoice = (T) new RecordedInvoiceCatAgregate();

        }

        recordedInvoice.setReference((isRecordedIvoince ? "" : "IC_") + invoice.getInvoiceNumber());
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
        recordedInvoice.setSeller(invoice.getSeller());

        try {
            recordedInvoice.setDueDate(DateUtils.setTimeToZero(invoice.getDueDate()));
        } catch (Exception e) {
            log.error("error with due date ", e);
            throw new ImportInvoiceException("Error on DueDate");
        }

        try {
            recordedInvoice.setInvoiceDate(DateUtils.setTimeToZero(invoice.getInvoiceDate()));
            recordedInvoice.setTransactionDate(DateUtils.setTimeToZero(invoice.getInvoiceDate()));
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

    /**
     * @return
     */
    public List<Long> queryInvoiceIdsForPS() {
        // TODO Auto-generated method stub
        return null;
    }
}