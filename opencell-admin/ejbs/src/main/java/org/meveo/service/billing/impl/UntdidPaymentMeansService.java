package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidPaymentMeans;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidPaymentMeansService extends PersistenceService<UntdidPaymentMeans> {

    public UntdidPaymentMeans getByCode(String untdidPayment) {
        if (untdidPayment == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidPaymentMeans.class, "i");
        qb.addCriterion("code", "=", untdidPayment, false);

        try {
            return (UntdidPaymentMeans) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
	public void create(UntdidPaymentMeans pUntdidPaymentMeans) throws BusinessException {
		validate(pUntdidPaymentMeans);
		super.create(pUntdidPaymentMeans);
	}
	
	public UntdidPaymentMeans update(UntdidPaymentMeans pOldUntdidPaymentMeans, UntdidPaymentMeans pNewUntdidPaymentMeans) throws BusinessException {
		if(pNewUntdidPaymentMeans != null && !pNewUntdidPaymentMeans.getCode().isBlank() && !pNewUntdidPaymentMeans.getCode().equals(pOldUntdidPaymentMeans.getCode())){
			pOldUntdidPaymentMeans.setCode(pNewUntdidPaymentMeans.getCode());
		}
		
		if(pNewUntdidPaymentMeans != null && !pNewUntdidPaymentMeans.getCodeName().isBlank() && !pNewUntdidPaymentMeans.getCodeName().equals(pOldUntdidPaymentMeans.getCodeName())){
			pOldUntdidPaymentMeans.setCodeName(pNewUntdidPaymentMeans.getCodeName());
		}
		
		if(pNewUntdidPaymentMeans != null && pNewUntdidPaymentMeans.getUsageEN16931() != null && !pNewUntdidPaymentMeans.getUsageEN16931().isBlank()){
			pOldUntdidPaymentMeans.setUsageEN16931(pNewUntdidPaymentMeans.getUsageEN16931());
		}
		
		return super.update(pOldUntdidPaymentMeans);
	}
	
	private void validate(UntdidPaymentMeans pUntdidPaymentMeans) {
		if(isCodeNullOrAlreadyUsed(pUntdidPaymentMeans)){
			throw new BusinessApiException("Code should be not null or already used by another UntdidPaymentMeans.");
		}
	
		if(pUntdidPaymentMeans.getCodeName() == null || (pUntdidPaymentMeans.getCodeName() != null && pUntdidPaymentMeans.getCodeName().isBlank())){
			throw new BusinessApiException("CodeName should be not null.");
		}
	}
	
	private boolean isCodeNullOrAlreadyUsed(UntdidPaymentMeans pUntdidPaymentMeans) {
		if (pUntdidPaymentMeans.getCode() == null || (pUntdidPaymentMeans.getCode() != null && pUntdidPaymentMeans.getCode().isBlank())) {
			return true;
		} else {
			UntdidPaymentMeans lUntdidPaymentMeans = getByCode(pUntdidPaymentMeans.getCode());
			
			if(lUntdidPaymentMeans != null && !lUntdidPaymentMeans.getId().equals(pUntdidPaymentMeans.getId())){
				return true;
			}
		}
		
		return false;
	}

}
