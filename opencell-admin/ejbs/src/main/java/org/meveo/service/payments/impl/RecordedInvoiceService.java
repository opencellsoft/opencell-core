/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.payments.impl;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.order.Order;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
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
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class RecordedInvoiceService extends PersistenceService<RecordedInvoice> {

    @Inject
    private InvoiceAgregateService invoiceAgregateService;

    @Inject
    private OCCTemplateService occTemplateService;

    @Inject
    private AccountOperationService accountOperationService;

    /**
     * @param recordedInvoiceId recored invoice id
     * @throws BusinessException business exception
     * @deprecated use accountOperationService.addLitigation
     */
    @Deprecated
    public void addLitigation(Long recordedInvoiceId) throws BusinessException {
    	accountOperationService.addLitigation(recordedInvoiceId);
    }

    /**
     * @param recordedInvoice recorded invoice
     * @throws BusinessException business exception.
     * @deprecated use accountOperationService.addLitigation
     */
    @Deprecated
    public void addLitigation(RecordedInvoice recordedInvoice) throws BusinessException {
    	accountOperationService.addLitigation(recordedInvoice);
    }

    /**
     * @param recordedInvoiceId recored invoice id
     * @throws BusinessException business exception.
     * @deprecated use accountOperationService.cancelLitigation
     */
    @Deprecated
    public void cancelLitigation(Long recordedInvoiceId) throws BusinessException {
    	accountOperationService.cancelLitigation(recordedInvoiceId);
    }

    /**
     * @param recordedInvoice recored invoice
     * @throws BusinessException business exception.
     * @deprecated use accountOperationService.cancelLitigation
     */
    @Deprecated
    public void cancelLitigation(RecordedInvoice recordedInvoice) throws BusinessException {

    	accountOperationService.cancelLitigation(recordedInvoice);
    }

    /**
     * @param reference invoice reference
     * @param invoiceType 
     * @return true if recored invoice exist
     */
    public boolean isRecordedInvoiceExist(String reference, InvoiceType invoiceType) {
        RecordedInvoice recordedInvoice = getRecordedInvoice(reference,invoiceType);
        if(recordedInvoice==null) {
        	return false;
        }
        return true;
    }

    /**
     * @param invoiceNumber invoice's reference.
     * @param invoiceType invoice's type.
     * @return instance of RecoredInvoice.
     */
    public RecordedInvoice getRecordedInvoice(String invoiceNumber, InvoiceType invoiceType){
        RecordedInvoice recordedInvoice = null;
        try {
            String qlString = "from " + RecordedInvoice.class.getSimpleName() + " where reference =:reference  and invoice.invoiceType=:invoiceType";
			Query query = getEntityManager().createQuery(qlString).setParameter("reference", invoiceNumber).setParameter("invoiceType", invoiceType);
			recordedInvoice = (RecordedInvoice) query.getSingleResult();
        } catch (Exception e) {        	
        }
        return recordedInvoice;
    }
    
    /**
     * @param invoiceNumber invoice's reference.
     * @return list of RecoredInvoice.
     */
	public List<RecordedInvoice> getRecordedInvoice(String invoiceNumber) {
    	List<RecordedInvoice> recordedInvoices = null;
        try {
            String qlString = "from " + RecordedInvoice.class.getSimpleName() + " where reference =:reference";
            recordedInvoices = (List<RecordedInvoice>)getEntityManager().createQuery(qlString).setParameter("reference", invoiceNumber).getResultList();
        } catch (Exception e) {
        	log.warn("exception trying to get recordedInvoice for reference "+invoiceNumber+": "+e.getMessage());
        }
        return recordedInvoices;
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

        if (expression.indexOf(ValueExpressionWrapper.VAR_INVOICE) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_INVOICE, invoice);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_RUN) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_RUN, billingRun);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_BILLING_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, billingAccount.getCustomerAccount());
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CUSTOMER_SHORT) >= 0) {
            userMap.put("c", billingAccount.getCustomerAccount().getCustomer());
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_PROVIDER) >= 0) {
            userMap.put(ValueExpressionWrapper.VAR_PROVIDER, appProvider);
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

    	if (invoice.getInvoiceType().isInvoiceAccountable() && InvoiceStatusEnum.VALIDATED.equals(invoice.getStatus())) {
    		@SuppressWarnings("unchecked")
            List<CategoryInvoiceAgregate> cats = (List<CategoryInvoiceAgregate>) invoiceAgregateService.listByInvoiceAndType(invoice, "R");
            List<RecordedInvoiceCatAgregate> listRecordedInvoiceCatAgregate = new ArrayList<>();

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
            if (remainingAmountWithTaxForRecordedIncoice != null && remainingAmountWithTaxForRecordedIncoice.compareTo(BigDecimal.ZERO) < 0) {
                String occTemplateCode = evaluateStringExpression(invoice.getInvoiceType().getOccTemplateNegativeCodeEl(), invoice, invoice.getBillingRun());
                if (!StringUtils.isBlank(occTemplateCode)) {
                    occTemplate = occTemplateService.findByCode(occTemplateCode);
                }

                if (occTemplate == null) {
                    occTemplate = invoice.getInvoiceType().getOccTemplateNegative();
                }

            } else {
                String occTemplateCode = evaluateStringExpression(invoice.getInvoiceType().getOccTemplateCodeEl(), invoice, invoice.getBillingRun());
                if (!StringUtils.isBlank(occTemplateCode)) {
                    occTemplate = occTemplateService.findByCode(occTemplateCode);
                }

                if (occTemplate == null) {
                    occTemplate = invoice.getInvoiceType().getOccTemplate();
                    if (occTemplate == null) {
                        return;
                    }
                }
                
            }

            RecordedInvoice recordedInvoice = createRecordedInvoice(remainingAmountWithoutTaxForRecordedIncoice, remainingAmountWithTaxForRecordedIncoice,
                remainingAmountTaxForRecordedIncoice, invoice.getNetToPay(), invoice, occTemplate, true);

            // Link the recorded invoice to subscription
            recordedInvoice.setSubscription(invoice.getSubscription());
            recordedInvoice.setJournal(occTemplate.getJournal());
            create(recordedInvoice);

            for (RecordedInvoiceCatAgregate recordedInvoiceCatAgregate : listRecordedInvoiceCatAgregate) {
                recordedInvoiceCatAgregate.setRecordedInvoice(recordedInvoice);
                create(recordedInvoiceCatAgregate);
            }
            invoice.setRecordedInvoice(recordedInvoice);
    	} else if(!InvoiceStatusEnum.VALIDATED.equals(invoice.getStatus())) {
    		log.warn(" Invoice status is not validated : id {}, status {}", invoice.getId(), invoice.getStatus());
    	} else {
    		log.warn(" Invoice type is not accountable : {} ", invoice.getInvoiceType());
    	}
    }
    
    @Override
    public void create(RecordedInvoice entity) throws BusinessException {
        accountOperationService.handleAccountingPeriods(entity);
        accountOperationService.fillOperationNumber(entity);
        super.create(entity);
    }

    @SuppressWarnings("unchecked")
    private <T extends RecordedInvoice> T createRecordedInvoice(BigDecimal amountWithoutTax, BigDecimal amountWithTax,
                                                                BigDecimal amountTax, BigDecimal netToPay, Invoice invoice,
                                                                OCCTemplate occTemplate, boolean isRecordedInvoice)
            throws InvoiceExistException, ImportInvoiceException, BusinessException {

        InvoiceType invoiceType = invoice.getInvoiceType();
		if (isRecordedInvoiceExist((isRecordedInvoice ? "" : "IC_") + invoice.getInvoiceNumber(), invoiceType)) {
            throw new InvoiceExistException("Invoice number " + invoice.getInvoiceNumber() + " with type "+invoiceType.getCode()+ " already exist");
        }

        CustomerAccount customerAccount = null;
        T recordedInvoice = null;
        BillingAccount billingAccount = invoice.getBillingAccount();

        if (isRecordedInvoice) {
            recordedInvoice = (T) new RecordedInvoice();
            recordedInvoice.setNetToPay(netToPay);

            List<String> orderNums = new ArrayList<>();
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

        recordedInvoice.setReference((isRecordedInvoice ? "" : "IC_") + invoice.getInvoiceNumber());
        recordedInvoice.setInvoice(invoice);
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
        }
        if (amountWithoutTax != null) {
            amountWithoutTax = amountWithoutTax.abs();
        }
        if (amountTax != null) {
            amountTax = amountTax.abs();
        }
        if (amountWithTax != null) {
            amountWithTax = amountWithTax.abs();
        }
        recordedInvoice.setAccountingCode(occTemplate.getAccountingCode());
        recordedInvoice.setCode(occTemplate.getCode());
        recordedInvoice.setDescription(occTemplate.getDescription());
        recordedInvoice.setTransactionCategory(occTemplate.getOccCategory());
        recordedInvoice.setAccountCodeClientSide(occTemplate.getAccountCodeClientSide());

        recordedInvoice.setAmount(amountWithTax);
        recordedInvoice.setUnMatchingAmount(amountWithTax);
        recordedInvoice.setMatchingAmount(BigDecimal.ZERO);

        recordedInvoice.setAmountWithoutTax(amountWithoutTax);
        recordedInvoice.setTaxAmount(amountTax);
        recordedInvoice.setSeller(invoice.getSeller());
        recordedInvoice.setCollectionDate(invoice.getInitialCollectionDate());
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

        recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
        recordedInvoice.setAccountingDate(invoice.getInvoiceDate());
        recordedInvoice.setPaymentMethod(invoice.getPaymentMethodType());

        return recordedInvoice;
    }

    /**
     * @return
     */
    public List<Long> queryInvoiceIdsForPS() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public List<Object[]> getAgedReceivables(CustomerAccount customerAccount, Date startDate, PaginationConfiguration paginationConfiguration,
                                             Integer stepInDays, Integer numberOfPeriods, String invoiceNumber, String customerAccountDescription) {
    	String datePattern = "yyyy-MM-dd";
        StringBuilder query = new StringBuilder("Select ao.customerAccount.id ,sum (case when ao.dueDate > '")
                .append(DateUtils.formatDateWithPattern(startDate, datePattern))
                .append("'  then  ao.unMatchingAmount else 0 end ) as notYetDue,");
    	if(stepInDays != null && numberOfPeriods != null) {
    	    String alias;
    	    int step;
    	    if(numberOfPeriods > 1) {
                query.append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(startDate, datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -stepInDays), datePattern)+"' then  ao.unMatchingAmount else 0 end ) as sum_1_" + stepInDays + ",")
                        .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(startDate, datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -stepInDays), datePattern)+"' then  ao.amountWithoutTax else 0 end ) as sum_1_" + stepInDays + "_awt,")
                        .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(startDate, datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -stepInDays), datePattern)+"' then  ao.taxAmount else 0 end ) as sum_1_" + stepInDays + "_tax,");
                for (int iteration = 1; iteration < numberOfPeriods - 1; iteration++) {
                    step = iteration * stepInDays;
                    alias = "as sum_"+ (stepInDays * iteration + 1) + "_" + (step * 2);
                    query.append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -(step + stepInDays)), datePattern)+"' then  ao.unMatchingAmount else 0 end ) ")
                            .append(alias).append(" , ")
                            .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -(step + stepInDays)), datePattern)+"' then  ao.amountWithoutTax else 0 end ) ")
                            .append(alias).append("_awt, ")
                            .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -(step + stepInDays)), datePattern)+"' then  ao.taxAmount else 0 end ) ")
                            .append(alias).append("_tax, ");
                }
            }
            step = numberOfPeriods > 1  ? stepInDays * (numberOfPeriods - 1) : stepInDays;
            query.append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern)+"'  then  ao.unMatchingAmount else 0 end ) as sum_" + step + "_up,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern)+"'  then  ao.amountWithoutTax else 0 end ) as sum_" + step + "_up_awt,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern)+"'  then  ao.taxAmount else 0 end ) as sum_" + step + "_up_tax,");
        } else {
    	    query.append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(startDate, datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern)+"' then  ao.unMatchingAmount else 0 end ) as sum_1_30,")
    	            .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(startDate, datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern)+"' then  ao.amountWithoutTax else 0 end ) as sum_1_30_awt,")
    	            .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(startDate, datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern)+"' then  ao.taxAmount else 0 end ) as sum_1_30_tax,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern)+"' then  ao.unMatchingAmount else 0 end ) as sum_31_60,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern)+"' then  ao.amountWithoutTax else 0 end ) as sum_31_60_awt,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern)+"' then  ao.taxAmount else 0 end ) as sum_31_60_tax,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern)+"' then  ao.unMatchingAmount else 0 end ) as sum_61_90,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern)+"' then  ao.amountWithoutTax else 0 end ) as sum_61_90_awt,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern)+"' and ao.dueDate >'"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern)+"' then  ao.taxAmount else 0 end ) as sum_61_90_tax,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern)+"'  then  ao.unMatchingAmount else 0 end ) as sum_90_up,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern)+"'  then  ao.amountWithoutTax else 0 end ) as sum_90_up_awt,")
                    .append("sum (case when ao.dueDate <='"+DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern)+"'  then  ao.taxAmount else 0 end ) as sum_90_up_tax,");
        }
        query.append(" ao.customerAccount.dunningLevel, ao.customerAccount.name, ao.customerAccount.description, ao.dueDate, ao.invoice.tradingCurrency.currency.currencyCode, ao.invoice.id, ao.invoice.invoiceNumber, ao.invoice.amountWithTax, ao.customerAccount.code ")
                .append("from ")
                .append(RecordedInvoice.class.getSimpleName())
                .append(" as ao");
        QueryBuilder qb = new QueryBuilder(query.toString());
        qb.addSql("(ao.matchingStatus='"+MatchingStatusEnum.O+"' or ao.matchingStatus='"+MatchingStatusEnum.P+"') ");
        ofNullable(customerAccount).ifPresent(ca -> qb.addSql("ao.customerAccount.code = '" + ca.getCode() +"'"));
        ofNullable(customerAccountDescription).ifPresent(caDescription
                -> qb.addSql("ao.customerAccount.description = '" + caDescription +"'"));
        ofNullable(invoiceNumber).ifPresent(invNumber -> qb.addSql("ao.invoice.invoiceNumber = '" + invNumber +"'"));
        qb.addGroupCriterion("ao.customerAccount.id, ao.customerAccount.dunningLevel, ao.customerAccount.name, ao.customerAccount.description, ao.dueDate, ao.amount, ao.invoice.tradingCurrency.currency.currencyCode, ao.invoice.id, ao.invoice.invoiceNumber, ao.invoice.amountWithTax, ao.customerAccount.code");
        qb.addPaginationConfiguration(paginationConfiguration);
        
        return qb.getQuery(getEntityManager()).getResultList();
    }
    
    public Long getCountAgedReceivables(CustomerAccount customerAccount) {
		String sql = "select count  (distinct concat(concat(amount, due_date), customer_account_id)) from ar_account_operation where transaction_type='I'";
        if(customerAccount != null) {
            sql = sql.concat(" and customer_account_id="+ customerAccount.getId());
        }
        return (Long) getEntityManager().createNativeQuery(sql).getSingleResult();
    }
    
    /**
     * Find by invoice id.
     *
     * @param invoiceId invoice's id
     * @return found recorded invoice
     */
    public RecordedInvoice findByInvoiceId(Long invoiceId) throws BusinessException {
        QueryBuilder qb = new QueryBuilder(RecordedInvoice.class, "ri", null);
        qb.addCriterionEntity("ri.invoice.id", invoiceId);
        try {
            return (RecordedInvoice) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.info("Invoice with id {} was not found. Returning null.", invoiceId);
            return null;  
        }
    }

}
