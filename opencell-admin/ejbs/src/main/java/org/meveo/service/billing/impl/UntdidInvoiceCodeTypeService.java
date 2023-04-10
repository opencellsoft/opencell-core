package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidInvoiceCodeType;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidInvoiceCodeTypeService extends PersistenceService<UntdidInvoiceCodeType> {

    public UntdidInvoiceCodeType getByCode(String allowanceCode) {
        if (allowanceCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceCodeType.class, "i");
        qb.addCriterion("code", "=", allowanceCode, false);

        try {
            return (UntdidInvoiceCodeType) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }    
    
    @SuppressWarnings("unchecked")
    public List<UntdidInvoiceCodeType> getListInvoiceCodeTypeByName() {
        log.debug("start of find list {} SortedByName ..", "InvoiceCodeType");
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceSubjectCode.class, "c");
        qb.addOrderCriterion("name", true);
        List<UntdidInvoiceCodeType> invoiceCodeTypes = (List<UntdidInvoiceCodeType>) qb.getQuery(getEntityManager()).getResultList();
        log.debug("start of find list {} SortedByName   result {}", new Object[] { "InvoiceCodeType", invoiceCodeTypes == null ? "null" : invoiceCodeTypes.size() });
        return invoiceCodeTypes;
    }

    public void create(UntdidInvoiceCodeType pUntdidInvoiceCodeType) throws BusinessException {
    	validate(pUntdidInvoiceCodeType);
    	super.create(pUntdidInvoiceCodeType);
    }
    
    public UntdidInvoiceCodeType update(UntdidInvoiceCodeType pOldUntdidInvoiceCodeType, UntdidInvoiceCodeType pNewUntdidInvoiceCodeType) throws BusinessException {     
    	if(pNewUntdidInvoiceCodeType != null && !pNewUntdidInvoiceCodeType.getCode().isBlank() && !pNewUntdidInvoiceCodeType.getCode().equals(pOldUntdidInvoiceCodeType.getCode())){
    		pOldUntdidInvoiceCodeType.setCode(pNewUntdidInvoiceCodeType.getCode());
    	}
    	
    	if(pNewUntdidInvoiceCodeType != null && !pNewUntdidInvoiceCodeType.getInterpretation16931().isBlank() && !pNewUntdidInvoiceCodeType.getInterpretation16931().equals(pOldUntdidInvoiceCodeType.getInterpretation16931())){
    		pOldUntdidInvoiceCodeType.setInterpretation16931(pNewUntdidInvoiceCodeType.getInterpretation16931());
    	}
    	
    	if(pNewUntdidInvoiceCodeType != null && pNewUntdidInvoiceCodeType.getName() != null && !pNewUntdidInvoiceCodeType.getName().isBlank()) {
    		pOldUntdidInvoiceCodeType.setName(pNewUntdidInvoiceCodeType.getName());
    	}
    	
    	return super.update(pOldUntdidInvoiceCodeType);
    }

    private void validate(UntdidInvoiceCodeType pUntdidInvoiceCodeType) {
    	if(isCodeNullOrAlreadyUsed(pUntdidInvoiceCodeType)){
    		throw new BusinessApiException("Code should be not null or already used by another UntdidInvoiceCodeType.");
    	}
    	
    	if(pUntdidInvoiceCodeType.getInterpretation16931() == null || (pUntdidInvoiceCodeType.getInterpretation16931() != null && pUntdidInvoiceCodeType.getInterpretation16931().isBlank())){
    		throw new BusinessApiException("Interpretation16931 should be not null.");
    	}
    }

    private boolean isCodeNullOrAlreadyUsed(UntdidInvoiceCodeType pUntdidInvoiceCodeType) {
    	if (pUntdidInvoiceCodeType.getCode() == null || (pUntdidInvoiceCodeType.getCode() != null && pUntdidInvoiceCodeType.getCode().isBlank())) {
    		return true;
    	} else {
    		UntdidInvoiceCodeType lUntdidInvoiceCodeType = getByCode(pUntdidInvoiceCodeType.getCode());
    		
    		if(lUntdidInvoiceCodeType != null && !lUntdidInvoiceCodeType.getId().equals(pUntdidInvoiceCodeType.getId())){
    			return true;
    		}
    	}
    	
    	return false;
    }

}
