package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.invoice.InvoiceSubTotalsDto;
import org.meveo.api.dto.invoice.SubTotalsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.billing.InvoiceSubTotals;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.PersistenceService;

@Stateless
public class InvoiceSubTotalsService  extends PersistenceService<InvoiceSubTotals> {
    @Inject
    private InvoiceTypeService invoiceTypeService;
    @Inject
    private BillingRunService billingRunService;
    
    public void addSubTotals(InvoiceSubTotalsDto invoiceSubTotalsDto) {        
        if (invoiceSubTotalsDto.getInvoiceType() == null || 
                (invoiceSubTotalsDto.getInvoiceType().getId() == null 
                    && invoiceSubTotalsDto.getInvoiceType().getCode() == null)) {
            throw new MissingParameterException("following parameters are required: invoiceType");
        }
        
        Long invoiceTypeId = invoiceSubTotalsDto.getInvoiceType().getId();
        String invoiceTypeCode = invoiceSubTotalsDto.getInvoiceType().getCode();
        
        InvoiceType invoiceType = null;
        if (invoiceTypeId != null)
            invoiceType = invoiceTypeService.findById(invoiceTypeId);
        else if (invoiceTypeCode!= null)
            invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);
        
        if (invoiceType == null) {
            throw new EntityDoesNotExistsException("InvoiceType[Id=" + invoiceTypeId + ", Code=" + invoiceTypeCode + "] does not exists.");
        }
        
        for(SubTotalsDto subTotalsDto : invoiceSubTotalsDto.getSubTotals()) {
            InvoiceSubTotals invoiceSubTotal = new InvoiceSubTotals();            
            Long subTotalId = subTotalsDto.getId();           
            
            if (subTotalId != null) {
                invoiceSubTotal = findById(subTotalId);
                if (invoiceSubTotal != null) {
                    updateInvoiceSubTotalsFromDto(invoiceType, subTotalsDto, invoiceSubTotal);
                    update(invoiceSubTotal);  
                }else {
                    throw new BusinessApiException("InvoiceSubTotals n'existe pas !"); 
                }
            }
            else {
                updateInvoiceSubTotalsFromDto(invoiceType, subTotalsDto, invoiceSubTotal);                
                create(invoiceSubTotal);  
            }
        }
    }

    private void updateInvoiceSubTotalsFromDto(InvoiceType invoiceType, SubTotalsDto subTotalsDto, InvoiceSubTotals invoiceSubTotal) {
        String subTotalEL = subTotalsDto.getEl();
        String subTotalLabel = subTotalsDto.getLabel();
        invoiceSubTotal.setInvoiceType(invoiceType);
        invoiceSubTotal.setSubTotalEl(subTotalEL);
        invoiceSubTotal.setLabel(subTotalLabel);
        invoiceSubTotal.setLabelI18n(billingRunService.convertMultiLanguageToMapOfValues(subTotalsDto.getLanguageLabels() ,null));
    }

}
