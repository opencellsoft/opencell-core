package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.IsoIcd;
import org.meveo.service.base.PersistenceService;

@Stateless
public class IsoIcdService extends PersistenceService<IsoIcd> {
	
	public IsoIcd findByCode(String pCode) {
		if (pCode == null) {
			return null;
		}
		
		QueryBuilder qb = new QueryBuilder(IsoIcd.class, "i");
		qb.addCriterion("code", "=", pCode, false);

		try {
			return (IsoIcd) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public void create(IsoIcd pIsoIcd) throws BusinessException {
    	validate(pIsoIcd);
        super.create(pIsoIcd);
    }
    
    public IsoIcd update(IsoIcd pOldIsoIcd, IsoIcd pNewIsoIcd) throws BusinessException {        
        if(pNewIsoIcd != null && !pNewIsoIcd.getCode().isBlank() && !pNewIsoIcd.getCode().equals(pOldIsoIcd.getCode())){
        	pOldIsoIcd.setCode(pNewIsoIcd.getCode());
        }
        
        if(pNewIsoIcd != null && !pNewIsoIcd.getSchemeName().isBlank() && !pNewIsoIcd.getSchemeName().equals(pOldIsoIcd.getSchemeName())){
        	pOldIsoIcd.setSchemeName(pNewIsoIcd.getSchemeName());
        }
        
        return super.update(pOldIsoIcd);
    }
    
    private void validate(IsoIcd pIsoIcd) {
        if(isCodeNullOrAlreadyUsed(pIsoIcd)){
            throw new BusinessApiException("Code should be not null or already used by another IsoIcd.");
        }
        
        if(pIsoIcd.getSchemeName() == null || (pIsoIcd.getSchemeName() != null && pIsoIcd.getSchemeName().isBlank())){
            throw new BusinessApiException("SchemeName should be not null.");
        }
    }

    private boolean isCodeNullOrAlreadyUsed(IsoIcd pIsoIcd) {
        if (pIsoIcd.getCode() == null || (pIsoIcd.getCode() != null && pIsoIcd.getCode().isBlank())) {
            return true;
        } else {
        	IsoIcd lIsoIcd = findByCode(pIsoIcd.getCode());
            
        	if(lIsoIcd != null && !lIsoIcd.getId().equals(pIsoIcd.getId())){
                return true;
            }
        }
        
        return false;
    }

    @SuppressWarnings("unchecked")
    public List<IsoIcd> getListIsoIcdByName() {
        log.debug("start of find list {} SortedByName ..", "InvoiceSubjectCode");
        QueryBuilder qb = new QueryBuilder(IsoIcd.class, "c");
        qb.addOrderCriterion("schemeName", true);
        List<IsoIcd> isoIcds = (List<IsoIcd>) qb.getQuery(getEntityManager()).getResultList();
        log.debug("start of find list {} SortedByName   result {}", new Object[] { "InvoiceSubjectCode", isoIcds == null ? "null" : isoIcds.size() });
        return isoIcds;
    }
}
