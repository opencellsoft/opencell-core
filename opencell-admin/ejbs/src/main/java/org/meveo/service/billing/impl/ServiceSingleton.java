package org.meveo.service.billing.impl;


import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.Sequence;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * A singleton service to handle synchronized calls. DO not change lock mode to
 * Write
 * 
 * @author Andrius Karpavicius
 */
@Singleton
@Lock(LockType.WRITE)
public class ServiceSingleton {


    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private OCCTemplateService oCCTemplateService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private SellerService sellerService;

    @Inject
    private Logger log;
    
    /**
     * Get invoice number sequence. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     * 
     * @param invoiceDate Invoice date
     * @param invoiceTypeId Invoice type id
     * @param seller Seller
     * @param cfName CFT name
     * @param incrementBy A number to increment by
     * @return An invoice numbering sequence with Sequence.previousInvoiceNb set to previous value of Sequence.currentInvoiceNb and Sequence.currentInvoiceNb incremented by
     *         numberOfInvoices value
     * @throws BusinessException
     */
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Sequence incrementInvoiceNumberSequence(Date invoiceDate, Long invoiceTypeId, Seller seller, String cfName, long incrementBy) throws BusinessException {
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

        InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
        Sequence sequence = invoiceType.getSellerSequenceSequenceByType(seller);
        if (sequence == null) {
            sequence = invoiceType.getSequence();
        }
        if (sequence == null) {
            sequence = new Sequence();
            sequence.setCurrentInvoiceNb(0L);
            sequence.setSequenceSize(9);
            sequence.setPrefixEL("");
            invoiceType.setSequence(sequence);
        }

        if (currentNbFromCF != null) {
            sequence.setCurrentInvoiceNb(currentNbFromCF);
        } else {
            if (sequence.getCurrentInvoiceNb() == null) {
                sequence.setCurrentInvoiceNb(0L);
            }
            previousInvoiceNb = sequence.getCurrentInvoiceNb();
            sequence.setCurrentInvoiceNb(sequence.getCurrentInvoiceNb() + incrementBy);
        }
        invoiceType = invoiceTypeService.update(invoiceType);

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
     * @throws BusinessException
     */
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Sequence reserveInvoiceNumbers(Long invoiceTypeId, Long sellerId, Date invoiceDate, long numberOfInvoices) throws BusinessException {

        log.debug("Reserving {} invoice numbers for {}/{}/{}", numberOfInvoices, invoiceTypeId, sellerId, invoiceDate);

        InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
        String cfName = invoiceTypeService.getCustomFieldCode(invoiceType);

        Seller seller = sellerService.findById(sellerId);
        seller = chooseSeller(seller, cfName, invoiceDate, invoiceType);

        Sequence sequence = incrementInvoiceNumberSequence(invoiceDate, invoiceTypeId, seller, cfName, numberOfInvoices);

        try {
            sequence = (Sequence) BeanUtils.cloneBean(sequence);
            return sequence;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new BusinessException("Failed to close invoice numbering sequence", e);
        }
    }

    /**
     * if the sequence not found on cust.seller, we try in seller.parent (until seller.parent=null)
     * 
     * @param seller
     * @param cfName
     * @param date
     * @param invoiceType
     * @return
     */
    @Lock(LockType.READ)
    public Seller chooseSeller(Seller seller, String cfName, Date date, InvoiceType invoiceType) {
        if (seller.getSeller() == null) {
            return seller;
        }
        if (customFieldInstanceService.hasCFValue(seller, cfName)) {
            return seller;
        }
        if (invoiceType.getSellerSequence() != null && invoiceType.isContainsSellerSequence(seller)) {
            return seller;
        }

        return chooseSeller(seller.getSeller(), cfName, date, invoiceType);

    }

    /**
     * Create an invoice type. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     * 
     * @param occCode OCC code
     * @param occCodeDefaultValue OCC default value
     * @param invoiceTypeCode Invoice type code
     * @param operationCategory Operation category
     * @return An invoice type entity
     * @throws BusinessException
     */
    @Lock(LockType.WRITE)
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

}