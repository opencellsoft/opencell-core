package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.invoice.InvoiceSubTotalsDto;
import org.meveo.api.dto.invoice.SubTotalsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceSubTotals;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

@Stateless
public class InvoiceSubTotalsService extends BusinessService<InvoiceSubTotals> {

    @Inject
    private InvoiceTypeService invoiceTypeService;
    @Inject
    private BillingRunService billingRunService;
    
    public List<InvoiceSubTotals> addSubTotals(InvoiceSubTotalsDto invoiceSubTotalsDto) {
        List<InvoiceSubTotals> lstInvoiceSubTotals = new ArrayList<>();
        
        if (invoiceSubTotalsDto.getInvoiceType() == null || 
                (invoiceSubTotalsDto.getInvoiceType().getId() == null 
                    & invoiceSubTotalsDto.getInvoiceType().getCode() == null)) {
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
                    lstInvoiceSubTotals.add(invoiceSubTotal);
                }else {
                    throw new BusinessApiException("InvoiceSubTotals n'existe pas !"); 
                }
            }
            else {
                updateInvoiceSubTotalsFromDto(invoiceType, subTotalsDto, invoiceSubTotal);                
                create(invoiceSubTotal);
                lstInvoiceSubTotals.add(invoiceSubTotal);
            }
        }
        
        return lstInvoiceSubTotals;
    }
    
    public void deleteSubTotals(InvoiceSubTotalsDto invoiceSubTotalsDto) {
        for(SubTotalsDto subTotalsDto : invoiceSubTotalsDto.getSubTotals()) {
            Long subTotalId = subTotalsDto.getId();           
            InvoiceSubTotals invoiceSubTotal = findById(subTotalId);
            if (invoiceSubTotal != null) {
                remove(invoiceSubTotal);
            }
            else {
                throw new EntityDoesNotExistsException(InvoiceSubTotals.class, subTotalId);
            }
        }        
    }

    private void updateInvoiceSubTotalsFromDto(InvoiceType invoiceType, SubTotalsDto subTotalsDto, InvoiceSubTotals invoiceSubTotal) {
        String subTotalEL = subTotalsDto.getEl();
        String subTotalLabel = subTotalsDto.getLabel();
        invoiceSubTotal.setInvoiceType(invoiceType);
        invoiceSubTotal.setSubTotalEl(subTotalEL);
        invoiceSubTotal.setLabel(subTotalLabel);
        invoiceSubTotal.setCode(invoiceType.getCode() + " - " + subTotalLabel);
        invoiceSubTotal.setLabelI18n(billingRunService.convertMultiLanguageToMapOfValues(subTotalsDto.getLanguageLabels() ,null));
    }   
	
	@SuppressWarnings("unchecked")
	public List<InvoiceSubTotals> findByInvoiceType(InvoiceType invoiceType) throws BusinessException {
		return getEntityManager().createNamedQuery("InvoiceSubTotals.findByInvoiceType")
                .setParameter("invoiceType", invoiceType)
                .getResultList();
	}
	

	public List<InvoiceSubTotals> calculateSubTotals(InvoiceType invoiceType, List<InvoiceLine> invoiceLines) throws BusinessException{
		try {
			
			var invoiceSubtotals = findByInvoiceType(invoiceType);
			if(CollectionUtils.isEmpty(invoiceSubtotals)) return Collections.emptyList();
			invoiceSubtotals.forEach( ist -> {
				BigDecimal amountWithTax = BigDecimal.ZERO;
				BigDecimal amountWithoutTax = BigDecimal.ZERO;
				BigDecimal transactionalAmountWithoutTax = BigDecimal.ZERO;
				BigDecimal transactionalAmountWithTax = BigDecimal.ZERO;
				for (InvoiceLine invl : invoiceLines) {
				    Boolean evaluateExpr = ist.getSubTotalEl() != null ? ValueExpressionWrapper.evaluateExpression(ist.getSubTotalEl(), Boolean.class, invl) : null;
					boolean isValid = StringUtils.isNotEmpty(ist.getSubTotalEl()) && evaluateExpr != null ? evaluateExpr : false;
					if(isValid) {
						amountWithTax = amountWithTax.add(invl.getAmountWithTax() != null ? invl.getAmountWithTax() : BigDecimal.ZERO);
						amountWithoutTax = amountWithoutTax.add(invl.getAmountWithoutTax() != null ? invl.getAmountWithoutTax() : BigDecimal.ZERO);
						transactionalAmountWithTax = transactionalAmountWithTax.add(invl.getTransactionalAmountWithTax() != null ? invl.getTransactionalAmountWithTax() : BigDecimal.ZERO);
						transactionalAmountWithoutTax = transactionalAmountWithoutTax.add(invl.getTransactionalAmountWithoutTax() != null ? invl.getTransactionalAmountWithoutTax() : BigDecimal.ZERO);
					}
				}
				ist.setAmountWithTax(amountWithTax);
				ist.setAmountWithoutTax(amountWithoutTax);
				ist.setTransactionalAmountWithoutTax(transactionalAmountWithoutTax);
				ist.setTransactionalAmountWithTax(transactionalAmountWithTax);
			});
			return invoiceSubtotals;
		}catch(Exception e) {
			throw new BusinessException(e);
		}
		
	}
}
