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

/**
 * 
 */
package org.meveo.service.payments.impl;

import static org.meveo.commons.utils.NumberUtils.round;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentScheduleInstance;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.model.payments.PaymentScheduleStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.tax.TaxClassService;
import org.meveo.util.ApplicationProvider;

/**
 * The Class PaymentScheduleInstanceItemService.
 *
 * @author anasseh
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class PaymentScheduleInstanceItemService extends PersistenceService<PaymentScheduleInstanceItem> {

    /** The invoice service. */
    @Inject
    private InvoiceService invoiceService;

    /** The payment service. */
    @Inject
    private PaymentService paymentService;

    /** The o CC template service. */
    @Inject
    private OCCTemplateService oCCTemplateService;

    /** The one shot charge template service. */
    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    /** The one shot charge instance service. */
    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    /** The recorded invoice service. */
    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    /** The invoice sub category country service. */
    @Inject
    private TaxService taxService;
    
    @Inject
    private TaxClassService taxClassService;

    /** The Constant HUNDRED. */
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    /** The app provider. */
    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * Gets the items to process.
     *
     * @param processingDate the processing date
     * @return the items to process
     */
    @SuppressWarnings("unchecked")
    public List<PaymentScheduleInstanceItem> getItemsToProcess(Date processingDate) {
        try {
            return (List<PaymentScheduleInstanceItem>) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.listItemsToProcess").setParameter("requestPaymentDateIN", processingDate).getResultList();
        } catch (Exception e) {
            return new ArrayList<PaymentScheduleInstanceItem>();
        }
    }

    /**
     * Process item.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @throws BusinessException the business exception
     */
    public void processItem(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        paymentScheduleInstanceItem = retrieveIfNotManaged(paymentScheduleInstanceItem);
        UserAccount userAccount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getUserAccount();
        BillingAccount billingAccount = userAccount.getBillingAccount();
        CustomerAccount customerAccount = billingAccount.getCustomerAccount();
        InvoiceSubCategory invoiceSubCat = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceSubCategory();
        BigDecimal amount = paymentScheduleInstanceItem.getPaymentScheduleInstance().getAmount();
        Tax tax = taxService.getZeroTax(); // TODO AKK There should be no tax on payment.
        if (tax == null) {
            throw new BusinessException("Cant found tax for invoiceSubCat:" + invoiceSubCat.getCode());
        }
        Amounts amounts = getAmounts(amount, tax);
        List<Long> aoIdsToPay = new ArrayList<Long>();
        Invoice invoice = null;
        RecordedInvoice recordedInvoicePS = null;
        PaymentMethod preferredMethod = customerAccount.getPreferredPaymentMethod();
        InvoiceType invoiceType = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceType();
        if (preferredMethod == null) {
            throw new BusinessException("preferredMethod is null");
        }

        if (paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().isGenerateAdvancePaymentInvoice()) {
      	InvoiceDto invoiceDTO = new InvoiceDto();
        	
        	invoiceDTO.setInvoiceMode(InvoiceModeEnum.AGGREGATED);
        	invoiceDTO.setInvoiceType(invoiceType.getCode());
        	invoiceDTO.setBillingAccountCode(billingAccount.getCode());
        	
        	invoiceDTO.setInvoiceDate(paymentScheduleInstanceItem.getDueDate());
        	invoiceDTO.setDueDate(paymentScheduleInstanceItem.getDueDate());
        	invoiceDTO.setSellerCode(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getSeller().getCode());
        	
        	List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregateDto>();
        	CategoryInvoiceAgregateDto categoryInvoiceAgregateDto = new CategoryInvoiceAgregateDto();
        	categoryInvoiceAgregateDto.setCategoryInvoiceCode(invoiceSubCat.getInvoiceCategory().getCode());
        	categoryInvoiceAgregateDto.setDescription(invoiceSubCat.getInvoiceCategory().getDescription());
        	
        	List<SubCategoryInvoiceAgregateDto> listSubCategoryInvoiceAgregateDto = new ArrayList<SubCategoryInvoiceAgregateDto>();
        	
        	SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = new  SubCategoryInvoiceAgregateDto();
        	subCategoryInvoiceAgregateDto.setInvoiceSubCategoryCode(invoiceSubCat.getCode());
        	subCategoryInvoiceAgregateDto.setDescription(invoiceSubCat.getDescription());
        	subCategoryInvoiceAgregateDto.setAmountWithoutTax(amounts.getAmountWithoutTax());
        	subCategoryInvoiceAgregateDto.setAmountWithTax(amounts.getAmountWithTax());
        	subCategoryInvoiceAgregateDto.setAmountTax(amounts.getAmountTax());
        	listSubCategoryInvoiceAgregateDto.add(subCategoryInvoiceAgregateDto);        	
        	categoryInvoiceAgregateDto.setListSubCategoryInvoiceAgregateDto(listSubCategoryInvoiceAgregateDto);        	
        	categoryInvoiceAgregates.add(categoryInvoiceAgregateDto);        	
        	invoiceDTO.setCategoryInvoiceAgregates(categoryInvoiceAgregates);
        	invoice = invoiceService.createInvoice(invoiceDTO, paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription().getSeller(), billingAccount, invoiceType);        	        	
        }

        recordedInvoicePS = createRecordedInvoicePS(amounts, customerAccount, invoiceType, preferredMethod.getPaymentType(), invoice, aoIdsToPay, paymentScheduleInstanceItem);
        aoIdsToPay.add(recordedInvoicePS.getId());

        paymentScheduleInstanceItem.setRecordedInvoice(recordedInvoicePS);
        if (paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().isDoPayment()) {
            try {
                if (preferredMethod.getPaymentType() == PaymentMethodEnum.CARD) {
                    paymentService.payByCardToken(customerAccount, (amount.multiply(new BigDecimal(100))).longValue(), aoIdsToPay, true, true, null);
                } else if (preferredMethod.getPaymentType() == PaymentMethodEnum.DIRECTDEBIT) {
                    paymentService.payByMandat(customerAccount, (amount.multiply(new BigDecimal(100))).longValue(), aoIdsToPay, true, true, null);
                } else {
                    throw new BusinessException("Payment method " + preferredMethod.getPaymentType() + " not allowed");
                }
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            }
        }

        if (paymentScheduleInstanceItem.isLast()) {
            paymentScheduleInstanceItem.getPaymentScheduleInstance().setStatus(PaymentScheduleStatusEnum.DONE);
            paymentScheduleInstanceItem.getPaymentScheduleInstance().setStatusDate(new Date());
        }
    }

    /**
     * Apply one shot reject PS.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @throws BusinessException the business exception
     */
    public void applyOneShotRejectPS(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        applyOneShotPS(paymentScheduleInstanceItem, true);
    }

    /**
     * Apply one shot PS.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @param isPaymentRejected the is payment rejected
     * @throws BusinessException the business exception
     */
    private void applyOneShotPS(PaymentScheduleInstanceItem paymentScheduleInstanceItem, boolean isPaymentRejected) throws BusinessException {
        InvoiceSubCategory invoiceSubCat = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getAdvancePaymentInvoiceSubCategory();
        Tax tax = taxService.getZeroTax(); // TODO AKK There should be no tax on payment.
        if (tax == null) {
            throw new BusinessException("applyOneShotPS: cant found tax for invoiceSubCat:" + invoiceSubCat.getCode());
        }
        Amounts amounts = getAmounts(paymentScheduleInstanceItem.getPaymentScheduleInstance().getAmount(), tax);
        String paymentlabel = paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getPaymentLabel();
        OneShotChargeTemplate oneShot = createOneShotCharge(invoiceSubCat, paymentlabel,paymentScheduleInstanceItem.getPaymentScheduleInstance().getPaymentScheduleTemplate().getTaxClass());

        try {
            oneShotChargeInstanceService.oneShotChargeApplication(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription(), null, oneShot, null, new Date(),
                new BigDecimal((isPaymentRejected ? "" : "-") + amounts.getAmountWithoutTax()), null, new BigDecimal(1), null, null, null, paymentlabel + (isPaymentRejected ? " (Rejected)" : ""), null, null, true, ChargeApplicationModeEnum.SUBSCRIPTION);

        } catch (RatingException e) {
            log.trace("Failed to apply a one shot charge {}: {}", oneShot, e.getRejectionReason());
            throw e; // e.getBusinessException();

        } catch (BusinessException e) {
            log.error("Failed to apply a one shot charge {}: {}", oneShot, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Apply one shot PS.
     *
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @throws BusinessException the business exception
     */
    public void applyOneShotPS(PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        applyOneShotPS(paymentScheduleInstanceItem, false);
    }

    /**
     * Creates the one shot charge.
     *
     * @param invoiceSubCategory the invoice sub category
     * @param paymentLabel the payment label
     * @return the one shot charge template
     * @throws BusinessException the business exception
     */
    private OneShotChargeTemplate createOneShotCharge(InvoiceSubCategory invoiceSubCategory, String paymentLabel,TaxClass taxClass) throws BusinessException {
        OneShotChargeTemplate oneShot = oneShotChargeTemplateService.findByCode("ADV_PAYMENT");
        if (oneShot == null) {
            oneShot = new OneShotChargeTemplate();
            oneShot.setCode("ADV_PAYMENT");
            oneShot.setDescription(paymentLabel);
            oneShot.setInvoiceSubCategory(invoiceSubCategory);
            oneShot.setOneShotChargeTemplateType(OneShotChargeTemplateTypeEnum.OTHER);
            oneShot.setType(OperationTypeEnum.CREDIT);
            oneShot.setAmountEditable(Boolean.TRUE);
            if(taxClass == null) {
            	taxClass = taxClassService.findByCode("NO_TAX");
            }
            oneShot.setTaxClass(taxClass);
            oneShotChargeTemplateService.create(oneShot);
        }
        return oneShot;
    }

    /**
     * Creates the PS AO.
     *
     * @param amounts the amounts
     * @param customerAccount the customer account
     * @param invoiceType the invoice type
     * @param paymentMethodType the payment method type
     * @param invoice the invoice
     * @param aoIdsToPay the ao ids to pay
     * @param paymentScheduleInstanceItem the payment schedule instance item
     * @return the account operation PS
     * @throws BusinessException the business exception
     */
    public RecordedInvoice createRecordedInvoicePS(Amounts amounts, CustomerAccount customerAccount, InvoiceType invoiceType, PaymentMethodEnum paymentMethodType, Invoice invoice, List<Long> aoIdsToPay,
            PaymentScheduleInstanceItem paymentScheduleInstanceItem) throws BusinessException {
        OCCTemplate occTemplate = oCCTemplateService.getOccTemplateFromInvoiceType(amounts.getAmountWithTax(), invoiceType, null, null);
        RecordedInvoice recordedInvoicePS = new RecordedInvoice();
        recordedInvoicePS.setDueDate(paymentScheduleInstanceItem.getDueDate());
        recordedInvoicePS.setAmount(amounts.getAmountWithTax());
        recordedInvoicePS.setUnMatchingAmount(recordedInvoicePS.getAmount());
        recordedInvoicePS.setMatchingAmount(BigDecimal.ZERO);
        recordedInvoicePS.setAccountingCode(occTemplate.getAccountingCode());
        recordedInvoicePS.setCode(occTemplate.getCode());
        recordedInvoicePS.setDescription(occTemplate.getDescription());
        recordedInvoicePS.setTransactionCategory(occTemplate.getOccCategory());
        recordedInvoicePS.setCustomerAccount(customerAccount);
        recordedInvoicePS.setReference(invoice == null ? "psItemID:" + paymentScheduleInstanceItem.getId() : invoice.getInvoiceNumber());
        recordedInvoicePS.setTransactionDate(new Date());
        recordedInvoicePS.setMatchingStatus(MatchingStatusEnum.O);
        recordedInvoicePS.setTaxAmount(amounts.getAmountTax());
        recordedInvoicePS.setAmountWithoutTax(amounts.getAmountWithoutTax());
        recordedInvoicePS.setPaymentScheduleInstanceItem(paymentScheduleInstanceItem);
        recordedInvoicePS.setInvoice(invoice);
        recordedInvoicePS.setSeller(invoice.getSeller());
        recordedInvoicePS.setSubscription(paymentScheduleInstanceItem.getPaymentScheduleInstance().getServiceInstance().getSubscription());
        recordedInvoiceService.create(recordedInvoicePS);
        return recordedInvoicePS;

    }

    /**
     * Gets the amount tax and amount without tax from amoutWithTax for the right tax application.
     *
     * @param amountWithTax the amount with tax
     * @param tax The tax to apply
     * @return the amounts
     * @throws BusinessException the business exception
     */
    private Amounts getAmounts(BigDecimal amountWithTax, Tax tax) throws BusinessException {

        BigDecimal amountTax, amountWithoutTax;
        Integer rounding = appProvider.getRounding();
        RoundingModeEnum roundingMode = appProvider.getRoundingMode();

        BigDecimal percentPlusOne = BigDecimal.ONE.add(tax.getPercent().divide(HUNDRED, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
        amountTax = amountWithTax.subtract(amountWithTax.divide(percentPlusOne, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
        if (rounding != null && rounding > 0) {
            amountTax = round(amountTax, rounding, roundingMode);
        }
        amountWithoutTax = amountWithTax.subtract(amountTax);
        return new Amounts(amountWithoutTax, amountWithTax, amountTax);
    }

    /**
     * Check payment record invoice.
     *
     * @param recordedInvoice the recorded invoice
     */
    public void checkPaymentRecordInvoice(RecordedInvoice recordedInvoice) {

    }

    /**
     * Count paid items.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the long
     */
    public Long countPaidItems(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (Long) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.countPaidItems").setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Count incoming items.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the long
     */
    public Long countIncomingItems(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (Long) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.countIncomingItems").setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId()).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sum amount paid.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the big decimal
     */
    public BigDecimal sumAmountPaid(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (BigDecimal) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.amountPaidItems").setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId())
                .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sum amount incoming.
     *
     * @param paymentScheduleInstance the payment schedule instance
     * @return the big decimal
     */
    public BigDecimal sumAmountIncoming(PaymentScheduleInstance paymentScheduleInstance) {
        try {
            return (BigDecimal) getEntityManager().createNamedQuery("PaymentScheduleInstanceItem.amountIncomingItems").setParameter("serviceInstanceIdIN", paymentScheduleInstance.getServiceInstance().getId())
                .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}