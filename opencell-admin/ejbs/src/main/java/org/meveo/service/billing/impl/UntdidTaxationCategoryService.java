package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidTaxationCategory;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidTaxationCategoryService extends PersistenceService<UntdidTaxationCategory> {
    
	public UntdidTaxationCategory getByCode(String byCode) {
        if (byCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidTaxationCategory.class, "i");
        qb.addCriterion("code", "=", byCode, false);

        try {
            return (UntdidTaxationCategory) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	public void create(UntdidTaxationCategory pUntdidTaxationCategory) throws BusinessException {
		validate(pUntdidTaxationCategory);
		super.create(pUntdidTaxationCategory);
	}
	
	public UntdidTaxationCategory update(UntdidTaxationCategory pOldUntdidTaxationCategory, UntdidTaxationCategory pNewUntdidTaxationCategory) throws BusinessException {
		if(pNewUntdidTaxationCategory != null && !pNewUntdidTaxationCategory.getCode().isBlank() && !pNewUntdidTaxationCategory.getCode().equals(pOldUntdidTaxationCategory.getCode())){
			pOldUntdidTaxationCategory.setCode(pNewUntdidTaxationCategory.getCode());
		}
		
		if(pNewUntdidTaxationCategory != null && !pNewUntdidTaxationCategory.getName().isBlank() && !pNewUntdidTaxationCategory.getName().equals(pOldUntdidTaxationCategory.getName())){
			pOldUntdidTaxationCategory.setName(pNewUntdidTaxationCategory.getName());
		}
		
		if(pNewUntdidTaxationCategory.getSemanticModel() != null && !pNewUntdidTaxationCategory.getSemanticModel().isBlank()){
			pOldUntdidTaxationCategory.setSemanticModel(pNewUntdidTaxationCategory.getSemanticModel());
		}
		
		return super.update(pOldUntdidTaxationCategory);
	}
	
	private void validate(UntdidTaxationCategory pUntdidTaxationCategory) {
		if(isCodeNullOrAlreadyUsed(pUntdidTaxationCategory)){
			throw new BusinessApiException("Code should be not null or already used by another UntdidTaxationCategory.");
		}
	
		if(pUntdidTaxationCategory.getName() == null || (pUntdidTaxationCategory.getName() != null && pUntdidTaxationCategory.getName().isBlank())){
			throw new BusinessApiException("Name should be not null.");
		}
		
		if(pUntdidTaxationCategory.getSemanticModel() == null || (pUntdidTaxationCategory.getSemanticModel() != null && pUntdidTaxationCategory.getSemanticModel().isBlank())){
			throw new BusinessApiException("SemanticModel should be not null.");
		}
	}
	
	private boolean isCodeNullOrAlreadyUsed(UntdidTaxationCategory pUntdidTaxationCategory) {
		if (pUntdidTaxationCategory.getCode() == null || (pUntdidTaxationCategory.getCode() != null && pUntdidTaxationCategory.getCode().isBlank())) {
			return true;
		} else {
			UntdidTaxationCategory lUntdidTaxationCategory = getByCode(pUntdidTaxationCategory.getCode());
			
			if(lUntdidTaxationCategory != null && !lUntdidTaxationCategory.getId().equals(pUntdidTaxationCategory.getId())){
				return true;
			}
		}
		
		return false;
	}
}
