package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidVatPaymentOption;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidVatPaymentOptionService extends PersistenceService<UntdidVatPaymentOption> {
 
	public UntdidVatPaymentOption getByCode(String byCode) {
        if (byCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidVatPaymentOption.class, "i");        
        
        qb.startOrClause();
        qb.addCriterion("code2005", "=", byCode, false);
        qb.addCriterion("code2475", "=", byCode, false);
        qb.endOrClause();
        
        try {
            return (UntdidVatPaymentOption) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	public void create(UntdidVatPaymentOption pUntdidVatPaymentOption) throws BusinessException {
		validate(pUntdidVatPaymentOption);
		super.create(pUntdidVatPaymentOption);
	}

	public UntdidVatPaymentOption update(UntdidVatPaymentOption pOldUntdidVatPaymentOption, UntdidVatPaymentOption pNewUntdidVatPaymentOption) throws BusinessException {
		if(pNewUntdidVatPaymentOption != null && !pNewUntdidVatPaymentOption.getCode2005().isBlank() && !pNewUntdidVatPaymentOption.getCode2005().equals(pOldUntdidVatPaymentOption.getCode2005())){
			pOldUntdidVatPaymentOption.setCode2005(pNewUntdidVatPaymentOption.getCode2005());
		}
		
		if(pNewUntdidVatPaymentOption != null && !pNewUntdidVatPaymentOption.getCode2475().isBlank() && !pNewUntdidVatPaymentOption.getCode2475().equals(pOldUntdidVatPaymentOption.getCode2475())){
			pOldUntdidVatPaymentOption.setCode2475(pNewUntdidVatPaymentOption.getCode2475());
		}
		
		if(pNewUntdidVatPaymentOption != null && !pNewUntdidVatPaymentOption.getValue2005().isBlank() && !pNewUntdidVatPaymentOption.getValue2005().equals(pOldUntdidVatPaymentOption.getValue2005())){
			pOldUntdidVatPaymentOption.setValue2005(pNewUntdidVatPaymentOption.getValue2005());
		}
		
		if(pNewUntdidVatPaymentOption != null && !pNewUntdidVatPaymentOption.getValue2475().isBlank() && !pNewUntdidVatPaymentOption.getValue2475().equals(pOldUntdidVatPaymentOption.getValue2475())){
			pOldUntdidVatPaymentOption.setValue2475(pNewUntdidVatPaymentOption.getValue2475());
		}
		
		return super.update(pOldUntdidVatPaymentOption);
	}

	private void validate(UntdidVatPaymentOption pUntdidVatPaymentOption) {
		if(isCodeNullOrAlreadyUsed2005(pUntdidVatPaymentOption)){
			throw new BusinessApiException("Code2005 should be not null or already used by another UntdidVatPaymentOption.");
		}
		
		if(isCodeNullOrAlreadyUsed2475(pUntdidVatPaymentOption)){
			throw new BusinessApiException("Code2475 should be not null or already used by another UntdidVatPaymentOption.");
		}

		if(pUntdidVatPaymentOption.getValue2005() == null || (pUntdidVatPaymentOption.getValue2005() != null && pUntdidVatPaymentOption.getValue2005().isBlank())){
			throw new BusinessApiException("Value2005 should be not null.");
		}
		
		if(pUntdidVatPaymentOption.getValue2475() == null || (pUntdidVatPaymentOption.getValue2475() != null && pUntdidVatPaymentOption.getValue2475().isBlank())){
			throw new BusinessApiException("Value2475 should be not null.");
		}
	}

	private boolean isCodeNullOrAlreadyUsed2005(UntdidVatPaymentOption pUntdidVatPaymentOption) {
		if (pUntdidVatPaymentOption.getCode2005() == null || (pUntdidVatPaymentOption.getCode2005() != null && pUntdidVatPaymentOption.getCode2005().isBlank())) {
			return true;
		} else {
			UntdidVatPaymentOption lUntdidVatPaymentOption = getByCode(pUntdidVatPaymentOption.getCode2005());
			
			if(lUntdidVatPaymentOption != null && !lUntdidVatPaymentOption.getId().equals(pUntdidVatPaymentOption.getId())){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isCodeNullOrAlreadyUsed2475(UntdidVatPaymentOption pUntdidVatPaymentOption) {
		if (pUntdidVatPaymentOption.getCode2475() == null || (pUntdidVatPaymentOption.getCode2475() != null && pUntdidVatPaymentOption.getCode2475().isBlank())) {
			return true;
		} else {
			UntdidVatPaymentOption lUntdidVatPaymentOption = getByCode(pUntdidVatPaymentOption.getCode2475());
			
			if(lUntdidVatPaymentOption != null && !lUntdidVatPaymentOption.getId().equals(pUntdidVatPaymentOption.getId())){
				return true;
			}
		}
		
		return false;
	}
	
	public UntdidVatPaymentOption findTheOldOne(){
		try{
			return (UntdidVatPaymentOption) getEntityManager().createQuery("from UntdidVatPaymentOption u order by  u.id ASC").setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}