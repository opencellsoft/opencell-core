package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidAllowanceCode;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidAllowanceCodeService extends PersistenceService<UntdidAllowanceCode> {

    public UntdidAllowanceCode getByCode(String allowanceCode) {
        if (allowanceCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidAllowanceCode.class, "i");
        qb.addCriterion("code", "=", allowanceCode, false);

        try {
            return (UntdidAllowanceCode) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public UntdidAllowanceCode update(UntdidAllowanceCode pOldUntdidAllowanceCode, UntdidAllowanceCode pNewUntdidAllowanceCode) throws BusinessException {  
        if(pNewUntdidAllowanceCode != null && !pNewUntdidAllowanceCode.getCode().isBlank() && !pNewUntdidAllowanceCode.getCode().equals(pOldUntdidAllowanceCode.getCode())){
        	pOldUntdidAllowanceCode.setCode(pNewUntdidAllowanceCode.getCode());
        }
        
        if(pNewUntdidAllowanceCode != null && !pNewUntdidAllowanceCode.getDescription().isBlank() && !pNewUntdidAllowanceCode.getDescription().equals(pOldUntdidAllowanceCode.getDescription())){
        	pOldUntdidAllowanceCode.setDescription(pNewUntdidAllowanceCode.getDescription());
        }
        
        return super.update(pOldUntdidAllowanceCode);
    }
    
    public void create(UntdidAllowanceCode pUntdidAllowanceCode) throws BusinessException {
    	validate(pUntdidAllowanceCode);
        super.create(pUntdidAllowanceCode);
    }
    
    private void validate(UntdidAllowanceCode pUntdidAllowanceCode) {
        if(isCodeNullOrAlreadyUsed(pUntdidAllowanceCode)){
            throw new BusinessApiException("Code should be not null or already used by another UntdidAllowanceCode.");
        }
        
        if(pUntdidAllowanceCode.getDescription() == null || (pUntdidAllowanceCode.getDescription() != null && pUntdidAllowanceCode.getDescription().isBlank())){
            throw new BusinessApiException("Description should be not null.");
        }
    }
    
    private boolean isCodeNullOrAlreadyUsed(UntdidAllowanceCode pUntdidAllowanceCode) {
        if (pUntdidAllowanceCode.getCode() == null || (pUntdidAllowanceCode.getCode() != null && pUntdidAllowanceCode.getCode().isBlank())) {
            return true;
        } else {
        	UntdidAllowanceCode lUntdidAllowanceCode = getByCode(pUntdidAllowanceCode.getCode());
            
        	if(lUntdidAllowanceCode != null && !lUntdidAllowanceCode.getId().equals(pUntdidAllowanceCode.getId())){
                return true;
            }
        }
        
        return false;
    }

}
