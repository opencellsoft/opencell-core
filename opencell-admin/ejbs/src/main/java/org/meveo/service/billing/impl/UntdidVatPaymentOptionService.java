package org.meveo.service.billing.impl;

import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidVatPaymentOption;
import org.meveo.service.base.PersistenceService;

public class UntdidVatPaymentOptionService extends PersistenceService<UntdidVatPaymentOption> {
    public UntdidVatPaymentOption getByCode(String byCode) {
        if (byCode == null) {//code2005 code2475
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
	
	public UntdidVatPaymentOption findTheOldOne(){
		try{
			return (UntdidVatPaymentOption) getEntityManager().createQuery("from UntdidVatPaymentOption u order by  u.id ASC").setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}