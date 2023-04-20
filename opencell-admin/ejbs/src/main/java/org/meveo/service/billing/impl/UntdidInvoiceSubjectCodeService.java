package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidInvoiceSubjectCodeService extends PersistenceService<UntdidInvoiceSubjectCode> {

    public UntdidInvoiceSubjectCode getByCode(String invoiceSubjectCode) {
        if (invoiceSubjectCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceSubjectCode.class, "i");
        qb.addCriterion("code", "=", invoiceSubjectCode, false);

        try {
            return (UntdidInvoiceSubjectCode) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<UntdidInvoiceSubjectCode> getListInvoiceSubjectCodeByName() {
        log.debug("start of find list {} SortedByName ..", "InvoiceSubjectCode");
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceSubjectCode.class, "c");
        qb.addOrderCriterion("codeName", true);
        List<UntdidInvoiceSubjectCode> invoiceSubjectCodes = (List<UntdidInvoiceSubjectCode>) qb.getQuery(getEntityManager()).getResultList();
        log.debug("start of find list {} SortedByName   result {}", new Object[] { "InvoiceSubjectCode", invoiceSubjectCodes == null ? "null" : invoiceSubjectCodes.size() });
        return invoiceSubjectCodes;
    }
    
    public void create(UntdidInvoiceSubjectCode pUntdidInvoiceSubjectCode) throws BusinessException {
    	validate(pUntdidInvoiceSubjectCode);
    	super.create(pUntdidInvoiceSubjectCode);
    }

    public UntdidInvoiceSubjectCode update(UntdidInvoiceSubjectCode pOldUntdidInvoiceSubjectCode, UntdidInvoiceSubjectCode pNewUntdidInvoiceSubjectCode) throws BusinessException {
    	if(pNewUntdidInvoiceSubjectCode != null && !pNewUntdidInvoiceSubjectCode.getCode().isBlank() && !pNewUntdidInvoiceSubjectCode.getCode().equals(pOldUntdidInvoiceSubjectCode.getCode())){
    		pOldUntdidInvoiceSubjectCode.setCode(pNewUntdidInvoiceSubjectCode.getCode());
    	}
    	
    	if(pNewUntdidInvoiceSubjectCode != null && !pNewUntdidInvoiceSubjectCode.getCodeName().isBlank() && !pNewUntdidInvoiceSubjectCode.getCodeName().equals(pOldUntdidInvoiceSubjectCode.getCodeName())){
    		pOldUntdidInvoiceSubjectCode.setCodeName(pNewUntdidInvoiceSubjectCode.getCodeName());
    	}
    	
    	return super.update(pOldUntdidInvoiceSubjectCode);
    }

    private void validate(UntdidInvoiceSubjectCode pUntdidInvoiceSubjectCode) {
    	if(isCodeNullOrAlreadyUsed(pUntdidInvoiceSubjectCode)){
    		throw new BusinessApiException("Code should be not null or already used by another UntdidInvoiceSubjectCode.");
    	}
    	
    	if(pUntdidInvoiceSubjectCode.getCodeName() == null || (pUntdidInvoiceSubjectCode.getCodeName() != null && pUntdidInvoiceSubjectCode.getCodeName().isBlank())){
    		throw new BusinessApiException("CodeName should be not null.");
    	}
    }

    private boolean isCodeNullOrAlreadyUsed(UntdidInvoiceSubjectCode pUntdidInvoiceSubjectCode) {
    	if (pUntdidInvoiceSubjectCode.getCode() == null || (pUntdidInvoiceSubjectCode.getCode() != null && pUntdidInvoiceSubjectCode.getCode().isBlank())) {
    		return true;
    	} else {
    		UntdidInvoiceSubjectCode lUntdidInvoiceSubjectCode = getByCode(pUntdidInvoiceSubjectCode.getCode());
    		
    		if(lUntdidInvoiceSubjectCode != null && !lUntdidInvoiceSubjectCode.getId().equals(pUntdidInvoiceSubjectCode.getId())){
    			return true;
    		}
    	}
    	
    	return false;
    }
}
