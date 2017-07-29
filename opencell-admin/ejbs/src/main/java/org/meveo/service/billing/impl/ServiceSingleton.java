package org.meveo.service.billing.impl;

import java.util.Date;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.Sequence;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * A singleton service to handle synchronized calls. DO not change lock mode to Write
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
    private Logger log;

    /**
     * Get invoice number sequence. NOTE: method is executed synchronously due to WRITE lock. DO NOT CHANGE IT.
     * 
     * @param invoiceDate Invoice date
     * @param invoiceTypeId Invoice type id
     * @param seller Seller
     * @param cfName CFT name
     * @param step A number to increment by
     * @return An invoice number sequence info
     * @throws BusinessException
     */
    @Lock(LockType.WRITE)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Sequence getInvoiceNumberSequence(Date invoiceDate, Long invoiceTypeId, Seller seller, String cfName, int step) throws BusinessException {
        Long currentNbFromCF = null;
        Object currentValObj = customFieldInstanceService.getCFValue(seller, cfName, invoiceDate);
        if (currentValObj != null) {
            currentNbFromCF = (Long) currentValObj;
            currentNbFromCF = currentNbFromCF + step;
            customFieldInstanceService.setCFValue(seller, cfName, currentNbFromCF, invoiceDate);

        } else {
            currentValObj = customFieldInstanceService.getCFValue(appProvider, cfName, invoiceDate);
            if (currentValObj != null) {
                currentNbFromCF = (Long) currentValObj;
                currentNbFromCF = currentNbFromCF + step;
                customFieldInstanceService.setCFValue(appProvider, cfName, currentNbFromCF, invoiceDate);
            }
        }

        InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
        Sequence sequence = invoiceType.getSellerSequenceSequenceByType(seller);
        if (sequence == null) {
            sequence = invoiceType.getSequence();
        }
        if (sequence != null) {
            sequence.setCurrentInvoiceNb(currentNbFromCF != null ? currentNbFromCF : ((sequence.getCurrentInvoiceNb() == null ? 0L : sequence.getCurrentInvoiceNb()) + step));
            invoiceType = invoiceTypeService.update(invoiceType);
        } else {
            sequence = new Sequence();
            sequence.setCurrentInvoiceNb(1L);
            sequence.setSequenceSize(9);
            sequence.setPrefixEL("");
            invoiceType.setSequence(sequence);
            invoiceTypeService.update(invoiceType);
        }

        return sequence;
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