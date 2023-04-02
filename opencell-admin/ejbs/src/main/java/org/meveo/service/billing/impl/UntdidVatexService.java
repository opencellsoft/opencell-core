package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidVatex;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidVatexService extends PersistenceService<UntdidVatex> {
    
	public UntdidVatex getByCode(String byCode) {
        if (byCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidVatex.class, "i");
        qb.addCriterion("code", "=", byCode, false);

        try {
            return (UntdidVatex) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	public void create(UntdidVatex pUntdidVatex) throws BusinessException {
		validate(pUntdidVatex);
		super.create(pUntdidVatex);
	}

	public UntdidVatex update(UntdidVatex pOldUntdidVatex, UntdidVatex pNewUntdidVatex) throws BusinessException {
		if(pNewUntdidVatex != null && !pNewUntdidVatex.getCode().isBlank() && !pNewUntdidVatex.getCode().equals(pOldUntdidVatex.getCode())){
			pOldUntdidVatex.setCode(pNewUntdidVatex.getCode());
		}
		
		if(pNewUntdidVatex != null && !pNewUntdidVatex.getCodeName().isBlank() && !pNewUntdidVatex.getCodeName().equals(pOldUntdidVatex.getCodeName())){
			pOldUntdidVatex.setCodeName(pNewUntdidVatex.getCodeName());
		}
		
		if(pNewUntdidVatex.getRemark() != null && !pNewUntdidVatex.getRemark().isBlank()){
			pOldUntdidVatex.setRemark(pNewUntdidVatex.getRemark());
		}
		
		return super.update(pOldUntdidVatex);
	}

	private void validate(UntdidVatex pUntdidVatex) {
		if(isCodeNullOrAlreadyUsed(pUntdidVatex)){
			throw new BusinessApiException("Code should be not null or already used by another UntdidVatex.");
		}

		if(pUntdidVatex.getCodeName() == null || (pUntdidVatex.getCodeName() != null && pUntdidVatex.getCodeName().isBlank())){
			throw new BusinessApiException("Code Name should be not null.");
		}
	}

	private boolean isCodeNullOrAlreadyUsed(UntdidVatex pUntdidVatex) {
		if (pUntdidVatex.getCode() == null || (pUntdidVatex.getCode() != null && pUntdidVatex.getCode().isBlank())) {
			return true;
		} else {
			UntdidVatex lUntdidVatex = getByCode(pUntdidVatex.getCode());
			
			if(lUntdidVatex != null && !lUntdidVatex.getId().equals(pUntdidVatex.getId())){
				return true;
			}
		}
		
		return false;
	}
}