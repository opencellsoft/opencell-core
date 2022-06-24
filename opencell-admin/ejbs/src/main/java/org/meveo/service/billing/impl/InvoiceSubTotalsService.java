package org.meveo.service.billing.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.billing.InvoiceSubTotals;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

@Stateless
public class InvoiceSubTotalsService extends BusinessService<InvoiceSubTotals> {

	
	@SuppressWarnings("unchecked")
	public List<InvoiceSubTotals> findByInvoiceType(InvoiceType invoiceType) throws BusinessException {
		Query query = getEntityManager().createNamedQuery("InvoiceSubTotals.findByInvoiceType").setParameter("invoiceType", invoiceType);
		return query.getResultList();
	}
	

	public List<InvoiceSubTotals> calculateSubTotals(InvoiceType invoiceType, List<InvoiceLine> invoiceLines) throws BusinessException{
		try {
			
			var invoiceSubtotals = findByInvoiceType(invoiceType);
			if(CollectionUtils.isEmpty(invoiceSubtotals)) return Collections.emptyList();
			invoiceSubtotals.forEach( ist -> {
				BigDecimal amountWithTax = BigDecimal.ZERO;
				BigDecimal amountWithoutTax = BigDecimal.ZERO;
				BigDecimal convertedAmountWithoutTax = BigDecimal.ZERO;
				BigDecimal convertedAmountWithTax = BigDecimal.ZERO;
				for (InvoiceLine invl : invoiceLines) {
					boolean isValid = StringUtils.isNotEmpty(ist.getSubTotalEl()) && ValueExpressionWrapper.evaluateExpression(ist.getSubTotalEl(), Boolean.class, invl);
					if(isValid) {
						amountWithTax = amountWithTax.add(invl.getAmountWithTax() != null ? invl.getAmountWithTax() : BigDecimal.ZERO);
						amountWithoutTax = amountWithoutTax.add(invl.getAmountWithoutTax() != null ? invl.getAmountWithoutTax() : BigDecimal.ZERO);
						convertedAmountWithTax = convertedAmountWithTax.add(invl.getConvertedAmountWithTax() != null ? invl.getConvertedAmountWithTax() : BigDecimal.ZERO);
						convertedAmountWithoutTax = convertedAmountWithoutTax.add(invl.getConvertedAmountWithoutTax() != null ? invl.getConvertedAmountWithoutTax() : BigDecimal.ZERO);
					}
				}
				ist.setAmountWithTax(amountWithTax);
				ist.setAmountWithoutTax(amountWithoutTax);
				ist.setConvertedAmountWithoutTax(convertedAmountWithoutTax);
				ist.setConvertedAmountWithTax(convertedAmountWithTax);
			});
			return invoiceSubtotals;
		}catch(Exception e) {
			throw new BusinessException(e);
		}
		
	}
}
