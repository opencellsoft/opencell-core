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

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static org.meveo.model.sequence.SequenceTypeEnum.ALPHA_UP;
import static org.meveo.model.sequence.SequenceTypeEnum.CUSTOMER_NO;
import static org.meveo.model.sequence.SequenceTypeEnum.NUMERIC;
import static org.meveo.model.sequence.SequenceTypeEnum.REGEXP;
import static org.meveo.model.sequence.SequenceTypeEnum.RUM;
import static org.meveo.model.sequence.SequenceTypeEnum.SEQUENCE;
import static org.meveo.model.sequence.SequenceTypeEnum.UUID;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.InvoiceNumberAssigned;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGatewayRumSequence;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.model.sequence.Sequence;
import org.meveo.model.sequence.SequenceTypeEnum;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.admin.impl.SequenceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

import com.mifmif.common.regex.Generex;

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

    private static Map<Character, Character> mapper = Map.of('0', 'Q',
            '1', 'R', '2', 'S', '3', 'T', '4', 'U', '5',
            'V', '6', 'W', '7', 'X', '8', 'Y', '9', 'Z');


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
                // invoiceType = invoiceTypeService.update(invoiceType);
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

        InvoiceSequence sequence = incrementInvoiceNumberSequence(invoiceDate, invoiceType, seller, cfName, numberOfInvoices);
        return sequence;

        /*
         * try { sequence = (InvoiceSequence) BeanUtils.cloneBean(sequence); return sequence; } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
         * NoSuchMethodException e) { throw new BusinessException("Failed to close invoice numbering sequence", e); }
         */
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
     * Assign invoice number to an invoice
     *
     * @param invoice invoice
     * @param isVirtual Is its a virtual invoice - should invoice be persisted
     * @throws BusinessException General business exception
     */
    private Invoice assignInvoiceNumber(Invoice invoice, boolean isVirtual) throws BusinessException {

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
        if (isVirtual) {

            invoiceNumberAssignedEventProducer.fire(invoice);

            if (invoice.getId() == null) {
                invoiceService.create(invoice);
            } else {
                invoice = invoiceService.update(invoice);
            }
        }
        return invoice;
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

    public String getGenericCode(CustomGenericEntityCode customGenericEntityCode) {
        return getGenericCode(customGenericEntityCode, null);
    }

    @Lock(LockType.WRITE)
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String getGenericCode(CustomGenericEntityCode customGenericEntityCode, String prefixOverride) {
        Sequence sequence = customGenericEntityCode.getSequence();
        String generatedCode = null;
        Map<Object, Object> context = new HashMap<>();
        context.put("entity", customGenericEntityCode.getEntityClass());
        if (sequence.getSequenceType() == SEQUENCE) {
            generatedCode = String.valueOf(sequenceService.generateSequence(sequence).getCurrentNumber());
        }

        if(sequence.getSequenceType() == NUMERIC) {
            Random random = new Random();
            generatedCode = String.valueOf(random.nextLong()) + now().toEpochMilli();
        }

        if(sequence.getSequenceType() == ALPHA_UP) {
            int leftLimit = 97;
            int rightLimit = 122;
            Random random = new Random();
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
        context.put("generatedCode", generatedCode);
        return prefixOverride == null ? formatCode(ofNullable(customGenericEntityCode.getFormatEL()).orElse(""), context)
                : prefixOverride + generatedCode;
    }

    private String formatCode(String formatEL, Map<Object, Object> context) {
        if (formatEL.isEmpty()) {
            return (String) context.get("generatedCode");
        }
        String resultCode = evaluateExpression(formatEL, context, String.class);
        if(formatEL.contains("generatedCode")) {
            return resultCode + context.get("generatedCode");
        }
        return resultCode;
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
}