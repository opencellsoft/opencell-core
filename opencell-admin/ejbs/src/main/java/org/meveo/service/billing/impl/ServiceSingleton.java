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

package org.meveo.service.billing.impl;

import com.mifmif.common.regex.Generex;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.InvoiceNumberAssigned;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGatewayRumSequence;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.sequence.Sequence;
import org.meveo.model.sequence.SequenceTypeEnum;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.SequenceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.valueOf;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.meveo.model.sequence.SequenceTypeEnum.ALPHA_UP;
import static org.meveo.model.sequence.SequenceTypeEnum.CUSTOMER_NO;
import static org.meveo.model.sequence.SequenceTypeEnum.NUMERIC;
import static org.meveo.model.sequence.SequenceTypeEnum.REGEXP;
import static org.meveo.model.sequence.SequenceTypeEnum.RUM;
import static org.meveo.model.sequence.SequenceTypeEnum.SEQUENCE;
import static org.meveo.model.sequence.SequenceTypeEnum.UUID;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

/**
 * A singleton service to handle synchronized calls. DO not change lock mode to Write
 *
 * @author Andrius Karpavicius
 * @author Edward Legaspi
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @author anasseh
 * @lastModifiedVersion 10.0
 */
@Singleton
@Lock(LockType.WRITE)
public class ServiceSingleton {

    private static final String OPEN_ORDER_QUOTE_PREFIX = "OOQ-";
    private static final String OPEN_ORDER_PREFIX = "OOR-";

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private InvoiceSequenceService invoiceSequenceService;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private SellerService sellerService;

    @Inject
    private ProviderService providerService;

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    @InvoiceNumberAssigned
    private Event<Invoice> invoiceNumberAssignedEventProducer;

    @Inject
    private Logger log;

    @Inject
    private SequenceService sequenceService;

    @Inject
    private JobExecutionService jobExecutionService;
    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private FinanceSettingsService financeSettingsService;

    private static Map<Character, Character> mapper = Map.of('0', 'Q',
            '1', 'R', '2', 'S', '3', 'T', '4', 'U', '5',
            'V', '6', 'W', '7', 'X', '8', 'Y', '9', 'Z');

    private static Map<Long, AtomicInteger> invoicingTempNumber = new HashMap<>();
    
	private static final String GENERATED_CODE_KEY = "generatedCode";

    private Random random = new SecureRandom();

    public String getTempInvoiceNumber(Long billingRunId){
    	// #MEL when remove brs from this map?
    	if(!invoicingTempNumber.containsKey(billingRunId)) {
    		AtomicInteger counter = new AtomicInteger(0);
    		invoicingTempNumber.put(billingRunId, counter);
    	}
    	AtomicInteger counter = invoicingTempNumber.get(billingRunId);
    	final String index = ""+counter.incrementAndGet();
    	return ""+billingRunId+"-"+("000000000"+index).substring(index.length());
    }
    

    /**
     * Gets the sequence from the seller or its parent hierarchy. Otherwise return the sequence from invoiceType.
     *
     * @param invoiceType {@link InvoiceType}
     * @param seller {@link Seller}
     * @return {@link InvoiceSequence}
     */
    private InvoiceSequence getSequenceFromSellerHierarchy(InvoiceType invoiceType, Seller seller) {
        InvoiceSequence sequence = invoiceType.getSellerSequenceSequenceByType(seller);

        if (sequence == null) {
            // gets the sequence from parent seller
            if (seller.getSeller() != null) {
                sequence = getSequenceFromSellerHierarchy(invoiceType, seller.getSeller());
            } else {
                sequence = invoiceType.getInvoiceSequence();
            }
        }

        return sequence;
    }

    /**
     * Get invoice number sequence.
     *
     * @param invoiceDate Invoice date
     * @param invoiceType Invoice type
     * @param seller Seller
     * @param cfName CFT name
     * @param incrementBy A number to increment by
     * @return An invoice numbering sequence with Sequence.previousInvoiceNb set to previous value of Sequence.currentInvoiceNb and Sequence.currentInvoiceNb incremented by
     *         numberOfInvoices value
     * @throws BusinessException business exception
     */
    private InvoiceSequence incrementInvoiceNumberSequence(Date invoiceDate, InvoiceType invoiceType, Seller seller, String cfName, long incrementBy) throws BusinessException {
        Long currentNbFromCF = null;
        Long previousInvoiceNb = null;

        Object currentValObj = customFieldInstanceService.getCFValue(seller, cfName, invoiceDate);
        if (currentValObj != null) {
            currentNbFromCF = (Long) currentValObj;
            previousInvoiceNb = currentNbFromCF;
            currentNbFromCF = currentNbFromCF + incrementBy;
            customFieldInstanceService.setCFValue(seller, cfName, currentNbFromCF, invoiceDate);

        } else {
            currentValObj = customFieldInstanceService.getCFValue(appProvider, cfName, invoiceDate);
            if (currentValObj != null) {
                currentNbFromCF = (Long) currentValObj;
                previousInvoiceNb = currentNbFromCF;
                currentNbFromCF = currentNbFromCF + incrementBy;
                customFieldInstanceService.setCFValue(appProvider, cfName, currentNbFromCF, invoiceDate);
            }
        }

        InvoiceSequence sequence = getSequenceFromSellerHierarchy(invoiceType, seller);

        if (sequence == null) {
            sequence = new InvoiceSequence();
            sequence.setCurrentNumber(0L);
            sequence.setSequenceSize(9);
            sequence.setCode(invoiceType.getCode());
            invoiceSequenceService.create(sequence);
            invoiceType.setInvoiceSequence(sequence);
        }

        if (currentNbFromCF != null) {
            sequence.setCurrentNumber(currentNbFromCF);
        } else {
            if (invoiceType.isUseSelfSequence()) {
                if (sequence.getCurrentNumber() == null) {
                    sequence.setCurrentNumber(0L);
                }
                previousInvoiceNb = sequence.getCurrentNumber();
                sequence.setCurrentNumber(sequence.getCurrentNumber() + incrementBy);
            } else {
                InvoiceSequence sequenceGlobal = new InvoiceSequence();
                sequenceGlobal.setSequenceSize(sequence.getSequenceSize());

                previousInvoiceNb = invoiceTypeService.getCurrentGlobalInvoiceBb();
                sequenceGlobal.setCurrentNumber(previousInvoiceNb + incrementBy);
                sequenceGlobal.setPreviousInvoiceNb(previousInvoiceNb);
                invoiceTypeService.setCurrentGlobalInvoiceBb(previousInvoiceNb + incrementBy);
                return sequenceGlobal;
            }
        }

        // As previousInVoiceNb is a transient value, set it after the update is called
        sequence.setPreviousInvoiceNb(previousInvoiceNb);

        return sequence;
    }

    /**
     * Reserve invoice numbers for a given invoice type, seller and invoice date match. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     *
     * @param invoiceTypeId Invoice type identifier
     * @param sellerId Seller identifier
     * @param invoiceDate Invoice date
     * @param numberOfInvoices Number of invoice numbers to reserve for
     * @return An invoice numbering sequence with Sequence.previousInvoiceNb set to previous value of Sequence.currentInvoiceNb and Sequence.currentInvoiceNb incremented by
     *         numberOfInvoices value
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public InvoiceSequence reserveInvoiceNumbers(Long invoiceTypeId, Long sellerId, Date invoiceDate, long numberOfInvoices) throws BusinessException {

        log.debug("Reserving {} invoice numbers for {}/{}/{}", numberOfInvoices, invoiceTypeId, sellerId, invoiceDate);

        InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
        String cfName = invoiceTypeService.getCustomFieldCode(invoiceType);

        Seller seller = sellerService.findById(sellerId);
        seller = seller.findSellerForInvoiceNumberingSequence(cfName, invoiceDate, invoiceType);

        return incrementInvoiceNumberSequence(invoiceDate, invoiceType, seller, cfName, numberOfInvoices);
    }

    /**
     * Create an invoice type. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     *
     * @param occCode OCC code
     * @param occCodeDefaultValue OCC default value
     * @param invoiceTypeCode Invoice type code
     * @param operationCategory Operation category
     * @return An invoice type entity
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public InvoiceType createInvoiceType(String occCode, String occCodeDefaultValue, String invoiceTypeCode, OperationCategoryEnum operationCategory) throws BusinessException {

        // Last check that invoice type does not exist yet
        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
        if (invoiceType != null) {
            return invoiceType;
        }

        OCCTemplate occTemplate = null;
        String occTemplateCode = null;
        try {
            occTemplateCode = (String) customFieldInstanceService.getOrCreateCFValueFromParamValue(occCode, occCodeDefaultValue, appProvider, true);
            log.debug("occTemplateCode:" + occTemplateCode);
            occTemplate = oCCTemplateService.findByCode(occTemplateCode);
        } catch (Exception e) {
            log.error("error while getting occ template ", e);
            throw new BusinessException("Cannot found OCC Template for invoice");
        }

        if (occTemplate == null) {
            occTemplate = new OCCTemplate();
            occTemplate.setCode(occTemplateCode);
            occTemplate.setDescription(occTemplateCode);
            occTemplate.setOccCategory(operationCategory);
            oCCTemplateService.create(occTemplate);
        }

        invoiceType = new InvoiceType();
        invoiceType.setCode(invoiceTypeCode);
        invoiceType.setOccTemplate(occTemplate);
        invoiceTypeService.create(invoiceType);
        return invoiceType;
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public GenericSequence getNextSequenceNumber(SequenceTypeEnum type) {
        Provider provider = providerService.findById(Provider.CURRENT_PROVIDER_ID, true);
        GenericSequence sequence = provider.getRumSequence();
        if (type == CUSTOMER_NO) {
            sequence = provider.getCustomerNoSequence();
        }
        if (sequence == null) {
            sequence = new GenericSequence();
        }
        sequence.setCurrentSequenceNb(sequence.getCurrentSequenceNb() + 1L);
        if (CUSTOMER_NO == type) {
            provider.setCustomerNoSequence(sequence);
        }
        if (RUM == type) {
            provider.setRumSequence(sequence);
        }
        providerService.updateNoCheck(provider);
        return sequence;
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCustomerNumberSequence(GenericSequence genericSequence) {
        appProvider.setCustomerNoSequence(genericSequence);
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CustomerSequence getPaymentGatewayRumSequenceNumber(CustomerSequence customerSequence) {
        customerSequence.getGenericSequence().setCurrentSequenceNb(customerSequence.getGenericSequence().getCurrentSequenceNb() + 1L);

        return customerSequence;
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PaymentGatewayRumSequence getPaymentGatewayRumSequenceNumber(PaymentGatewayRumSequence rumSequence) {
        rumSequence.getGenericSequence().setCurrentSequenceNb(rumSequence.getGenericSequence().getCurrentSequenceNb() + 1L);

        return rumSequence;
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CustomGenericEntityCode getGenericCodeEntity(String entityClass) throws BusinessException {
        CustomGenericEntityCode customGenericEntityCode = null;
        if (!StringUtils.isBlank(entityClass)) {
            customGenericEntityCode = customGenericEntityCodeService.findByClass(entityClass);
            if (customGenericEntityCode != null) {
                Sequence sequence = customGenericEntityCode.getSequence();
                sequence.setCurrentNumber(sequence.getCurrentNumber() != null
                        ? sequence.getCurrentNumber() + 1 : 0);
            }
        }
        return customGenericEntityCode;
    }

    /**
     * Assign invoice number to a virtual invoice. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     *
     * @param invoice invoice
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Invoice assignInvoiceNumberVirtual(Invoice invoice) throws BusinessException {
        return assignInvoiceNumber(invoice, false);
    }

    /**
     * Assign invoice number to an invoice. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     *
     * @param invoice invoice
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Invoice assignInvoiceNumber(Invoice invoice) throws BusinessException {
        return assignInvoiceNumber(invoice, true);
    }


    /**
     * Validate and assign invoice number to an invoice.
     * @param invoiceId invoice identifier
     * @param refreshExchangeRate refresh exchange rate
     * @throws BusinessException business exception
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Invoice validateAndAssignInvoiceNumber(Long invoiceId, boolean refreshExchangeRate) throws BusinessException {
    	Invoice invoice = invoiceService.findById(invoiceId);
    	if (invoice == null) {
    		throw new EntityDoesNotExistsException(Invoice.class, invoiceId);
    	}
        if(refreshExchangeRate && invoice.canBeRefreshed()) {
            invoiceService.refreshConvertedAmounts(invoice, invoice.getTradingCurrency().getCurrentRate(),
                    invoice.getTradingCurrency().getCurrentRateFromDate());
        }
    	invoice.setStatus(InvoiceStatusEnum.VALIDATED);
    	invoice.setRejectedByRule(null);
    	invoice.setRejectReason(null);
    	return assignInvoiceNumber(invoice, true);
    }

    private BigDecimal getExchangeRate(Invoice invoice) {

        BigDecimal exchangeRateToApply = null;

        if (invoice.getTradingCurrency() != null) {

            ExchangeRate calculatedExchangeRate = invoice.getTradingCurrency().getExchangeRate(invoice.getInvoiceDate());

            exchangeRateToApply = calculatedExchangeRate != null ? calculatedExchangeRate.getExchangeRate() : invoice.getTradingCurrency().getCurrentRate();

        }
        return exchangeRateToApply;
    }

    public CpqQuote assignCpqQuoteNumber(CpqQuote cpqQuote) {
        InvoiceType invoiceType = invoiceTypeService.retrieveIfNotManaged(cpqQuote.getOrderInvoiceType());
        if(invoiceType == null)
        	throw new EntityDoesNotExistsException(InvoiceType.class, cpqQuote.getOrderInvoiceType().getCode());
        String cfName = invoiceTypeService.getCustomFieldCode(invoiceType);
        Customer cust = cpqQuote.getApplicantAccount().getCustomerAccount().getCustomer();

        Seller seller = cpqQuote.getSeller();

        if (seller == null && cust.getSeller() != null) {
            seller = cust.getSeller().findSellerForInvoiceNumberingSequence(cfName, cpqQuote.getSendDate(), invoiceType);
        }
        seller = sellerService.refreshOrRetrieve(seller);
        InvoiceSequence sequence = incrementInvoiceNumberSequence(cpqQuote.getSendDate(), invoiceType, seller, cfName, 1);
        int sequenceSize = sequence.getSequenceSize();

        InvoiceTypeSellerSequence invoiceTypeSellerSequence = null;
        InvoiceTypeSellerSequence invoiceTypeSellerSequencePrefix = getInvoiceTypeSellerSequence(invoiceType, seller);
        String prefix = invoiceType.getPrefixEL();
        if (invoiceTypeSellerSequencePrefix != null) {
            prefix = invoiceTypeSellerSequencePrefix.getPrefixEL();

        } else if (seller != null) {
            invoiceTypeSellerSequence = invoiceType.getSellerSequenceByType(seller);
            if (invoiceTypeSellerSequence != null) {
                prefix = invoiceTypeSellerSequence.getPrefixEL();
            }
        }

        if (prefix != null && !StringUtils.isBlank(prefix)) {
            prefix = InvoiceService.evaluatePrefixElExpression(prefix, cpqQuote);

        } else {
            prefix = "";
        }

        long nextInvoiceNb = sequence.getCurrentNumber();
        String invoiceNumber = StringUtils.getLongAsNChar(nextInvoiceNb, sequenceSize);
        cpqQuote.setQuoteNumber(prefix + invoiceNumber);
    	return cpqQuote;
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CommercialOrder assignCommercialOrderNumber(CommercialOrder order) {

        InvoiceType invoiceType = invoiceTypeService.retrieveIfNotManaged(order.getOrderInvoiceType());
        if(invoiceType == null)
        	throw new EntityDoesNotExistsException(InvoiceType.class, order.getOrderInvoiceType().getCode());
        String cfName = invoiceTypeService.getCustomFieldCode(invoiceType);
        Customer cust = order.getBillingAccount().getCustomerAccount().getCustomer();

        Seller seller = order.getSeller();

        if (seller == null && cust.getSeller() != null) {
            seller = cust.getSeller().findSellerForInvoiceNumberingSequence(cfName, order.getOrderDate(), invoiceType);
        }
        seller = sellerService.refreshOrRetrieve(seller);

        InvoiceSequence sequence = incrementInvoiceNumberSequence(order.getOrderDate(), invoiceType, seller, cfName, 1);
        int sequenceSize = sequence.getSequenceSize();

        InvoiceTypeSellerSequence invoiceTypeSellerSequence = null;
        InvoiceTypeSellerSequence invoiceTypeSellerSequencePrefix = getInvoiceTypeSellerSequence(invoiceType, seller);
        String prefix = invoiceType.getPrefixEL();
        if (invoiceTypeSellerSequencePrefix != null) {
            prefix = invoiceTypeSellerSequencePrefix.getPrefixEL();

        } else if (seller != null) {
            invoiceTypeSellerSequence = invoiceType.getSellerSequenceByType(seller);
            if (invoiceTypeSellerSequence != null) {
                prefix = invoiceTypeSellerSequence.getPrefixEL();
            }
        }

        if (prefix != null && !StringUtils.isBlank(prefix)) {
            prefix = InvoiceService.evaluatePrefixElExpression(prefix, order);

        } else {
            prefix = "";
        }

        long nextInvoiceNb = sequence.getCurrentNumber();
        String invoiceNumber = StringUtils.getLongAsNChar(nextInvoiceNb, sequenceSize);
        order.setOrderNumber(prefix + invoiceNumber);
    	return order;
    }
    /**
     * Assign invoice number to an invoice
     *
     * @param invoice invoice
     * @param saveInvoice Should invoice be persisted
     * @throws BusinessException General business exception
     */
    public Invoice assignInvoiceNumber(Invoice invoice, boolean saveInvoice) throws BusinessException {
		if(invoice.getStatus()!=InvoiceStatusEnum.VALIDATED) {
			throw new BusinessException("cannot assign invoice number to invoice with status: "+invoice.getStatus());
		}
        InvoiceType invoiceType = invoiceTypeService.retrieveIfNotManaged(invoice.getInvoiceType());

        String cfName = invoiceTypeService.getCustomFieldCode(invoiceType);
        Customer cust = invoice.getBillingAccount().getCustomerAccount().getCustomer();

        Seller seller = invoice.getSeller();
        if (seller == null && cust.getSeller() != null) {
            seller = cust.getSeller().findSellerForInvoiceNumberingSequence(cfName, invoice.getInvoiceDate(), invoiceType);
        }
        seller = sellerService.refreshOrRetrieve(seller);

        InvoiceSequence sequence = incrementInvoiceNumberSequence(invoice.getInvoiceDate(), invoiceType, seller, cfName, 1);
        long sequenceSize = sequence.getSequenceSize();

        InvoiceTypeSellerSequence invoiceTypeSellerSequence = null;
        InvoiceTypeSellerSequence invoiceTypeSellerSequencePrefix = getInvoiceTypeSellerSequence(invoiceType, seller);
        String prefix = invoiceType.getPrefixEL();
        if (invoiceTypeSellerSequencePrefix != null) {
            prefix = invoiceTypeSellerSequencePrefix.getPrefixEL();

        } else if (seller != null) {
            invoiceTypeSellerSequence = invoiceType.getSellerSequenceByType(seller);
            if (invoiceTypeSellerSequence != null) {
                prefix = invoiceTypeSellerSequence.getPrefixEL();
            }
        }

        if (prefix != null && !StringUtils.isBlank(prefix)) {
            prefix = InvoiceService.evaluatePrefixElExpression(prefix, invoice);

        } else {
            prefix = "";
        }

        long nextInvoiceNb = sequence.getCurrentNumber();
        String invoiceNumber = StringUtils.getLongAsNChar(nextInvoiceNb, sequenceSize);
        // request to store invoiceNo in alias field
        invoice.setAlias(invoiceNumber);
        invoice.setInvoiceNumber((prefix == null ? "" : prefix) + invoiceNumber);
        if (saveInvoice) {
        	invoiceNumberAssignedEventProducer.fire(invoice);
        	if (invoice.getId() == null) {
        		invoiceService.create(invoice);
        	} else {
        		invoice = invoiceService.update(invoice);
        	}
        }
        return invoice;
    }

    public void triggersJobs() {
    	FinanceSettings lastOne = financeSettingsService.getFinanceSetting();
        if (lastOne != null && lastOne.isActivateDunning()) {
        	Arrays.asList("DunningCollectionPlan_Job", "TriggerCollectionPlanLevelsJob", "TriggerReminderDunningLevel_Job").stream()
        	.map(jobInstanceService::findByCode)
        	.filter(Objects::nonNull)
        	.forEach(jibInstance -> jobExecutionService.executeJob(jibInstance, null, JobLauncherEnum.TRIGGER));
        }
    }

    /**
     * Returns {@link InvoiceTypeSellerSequence} from the nearest parent.
     *
     * @param invoiceType {@link InvoiceType}
     * @param seller {@link Seller}
     * @return {@link InvoiceTypeSellerSequence}
     */
    private InvoiceTypeSellerSequence getInvoiceTypeSellerSequence(InvoiceType invoiceType, Seller seller) {
        InvoiceTypeSellerSequence sequence = invoiceType.getSellerSequenceByType(seller);

        if (sequence == null && seller.getSeller() != null) {
            sequence = getInvoiceTypeSellerSequence(invoiceType, seller.getSeller());
        }

        return sequence;
    }

    /**
     * Generate custom generic code
     *
     * @param customGenericEntityCode
     * @return generated code
     */
    public String getGenericCode(CustomGenericEntityCode customGenericEntityCode) {
        return getGenericCode(customGenericEntityCode, null, true, null);
    }

    /**
     * Generate custom generic code
     *
     * @param customGenericEntityCode
     * @param prefixOverride
     * @return generated code
     */
    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getGenericCode(CustomGenericEntityCode customGenericEntityCode, String prefixOverride, boolean updateSequence, String formatEL) throws BusinessException {
        Sequence sequence = customGenericEntityCode.getSequence();
        String generatedCode = null;
        Map<Object, Object> context = new HashMap<>();
        context.put("entity", customGenericEntityCode.getEntityClass());

        if (sequence.getSequenceType() == SEQUENCE) {
            Long lCurrentNumber = sequence.getCurrentNumber();
            //Do not update sequence if we test only generated code
            if(updateSequence) {
                lCurrentNumber = sequenceService.generateSequence(sequence).getCurrentNumber();
            }
            generatedCode = leftPad(valueOf(lCurrentNumber), sequence.getSequenceSize(), '0');
        }

        if(sequence.getSequenceType() == NUMERIC) {
            generatedCode = leftPad((valueOf(random.nextLong()) + now().toEpochMilli()), sequence.getSequenceSize(), '0');
        }

        if(sequence.getSequenceType() == ALPHA_UP) {
            int leftLimit = 97;
            int rightLimit = 122;
            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(sequence.getSequenceSize())
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            String timeStamp = replaceDigitsWithChars(now().toEpochMilli());
            generatedCode = (generatedString + timeStamp).toUpperCase();
        }

        if(sequence.getSequenceType() == UUID) {
            generatedCode = randomUUID().toString();
        }

        if(sequence.getSequenceType() == REGEXP) {
            Generex generex = new Generex(sequence.getSequencePattern());
            generatedCode = generex.random(sequence.getSequenceSize());
        }

        context.put(GENERATED_CODE_KEY, generatedCode);
        String storedFormatEL = formatEL != null ? formatEL : customGenericEntityCode.getFormatEL();

        try {
            formatCode(ofNullable(storedFormatEL).orElse(""), context);
        } catch(BusinessException e) {
            throw new BusinessException("Error in EL");
        }

        return prefixOverride == null || prefixOverride.isBlank()
                ? formatCode(ofNullable(storedFormatEL).orElse(""), context)
                : prefixOverride + generatedCode;
    }

    private String replaceDigitsWithChars(long epochTimestamp) {
        StringBuilder result = Long.toString(epochTimestamp, 26).chars()
                .mapToObj(character ->  replaceNumber((char) character))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
        return result.toString();
    }

    private char replaceNumber(char character) {
        return character >= '0' && character <= '9' ? mapper.get(character) : character;
    }

    private String formatCode(String formatEL, Map<Object, Object> context) throws BusinessException {
        if (formatEL.isEmpty()) {
            return (String) context.get(GENERATED_CODE_KEY);
        }
        if (formatEL.indexOf("#{") < 0) {
            return formatEL + context.get(GENERATED_CODE_KEY);
        }
        return evaluateExpression(formatEL, context, String.class);
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getNextOpenOrderSequence() {
        return numberGenerator(OPEN_ORDER_PREFIX);
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getNextOpenOrderQuoteSequence() {
        return numberGenerator(OPEN_ORDER_QUOTE_PREFIX);
    }

    private String numberGenerator(String prefix) {
        String code = prefix + (Calendar.getInstance().get(Calendar.YEAR) % 100);
        InvoiceSequence sequence = invoiceSequenceService.findByCode(code);
        if (sequence == null) {
            sequence = new InvoiceSequence();
            sequence.setCurrentNumber(1L);
            sequence.setSequenceSize(5);
            sequence.setCode(code);
            invoiceSequenceService.create(sequence);
        }
        String ooqCode =  code + "-" + leftPad(valueOf(sequence.getCurrentNumber()),
                sequence.getSequenceSize(), '0');

        sequence.setCurrentNumber(sequence.getCurrentNumber()+1);

        return ooqCode;
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SecurityDepositTemplate incrementSDTemplateInstanciationNumber(SecurityDepositTemplate securityDepositTemplate) throws BusinessException {
        Integer numberOfInstantiation = securityDepositTemplate.getNumberOfInstantiation();
        securityDepositTemplate.setNumberOfInstantiation(numberOfInstantiation != null ? numberOfInstantiation + 1 : 1);
        return securityDepositTemplate;
    }
}