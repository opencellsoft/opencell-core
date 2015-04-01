/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.QueryBuilder.QueryLikeStyleEnum;
import org.meveo.model.billing.Tax;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

/**
 * Tax service implementation.
 */
@Stateless
public class TaxService extends PersistenceService<Tax> {

	public Tax findByCode(String code, Provider provider) {
		return findByCode(getEntityManager(), code, provider);
	}

	public Tax findByCode(EntityManager em, String code, Provider provider) {
		QueryBuilder qb = new QueryBuilder(Tax.class, "t");
		qb.addCriterion("t.code", "=", code, false);
		qb.addCriterionEntity("t.provider", provider);

		try {
			return (Tax) qb.getQuery(em).getSingleResult();
		} catch (NoResultException ne ) {
			return null;
		}catch (NonUniqueResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Tax> findStartsWithCode(EntityManager em, String taxId,
			Provider provider) {
		try {
			QueryBuilder qb = new QueryBuilder(Tax.class, "t");
			qb.like("t.code", taxId, QueryLikeStyleEnum.MATCH_BEGINNING, false);
			qb.addCriterionEntity("t.provider", provider);
			return (List<Tax>) qb.getQuery(em).getResultList();
		} catch (NoResultException ne) {
			return null;
		}catch (NonUniqueResultException nre){
			return null;
		}
	}
	
	public int getNbTaxesNotAssociated(Provider provider) { 
		return ((Long)getEntityManager().createQuery("select count(*) from Tax t where t.id not in (select l.tax.id from TaxLanguage l where l.tax.id is not null ) "
				+ " and t.id not in (select cntr.tax.id from InvoiceSubcategoryCountry cntr where cntr.tax.id is not null) and t.provider=:provider").setParameter("provider", provider).getSingleResult()).intValue();
		}
	
	public  List<Tax> getTaxesNotAssociated(Provider provider) { 
		return (List<Tax>)getEntityManager().createQuery("from Tax t where t.id not in (select l.tax.id from TaxLanguage l where l.tax.id is not null ) "
				+ " and t.id not in (select cntr.tax.id from InvoiceSubcategoryCountry cntr where cntr.tax.id is not null) and t.provider=:provider").setParameter("provider", provider).getResultList();
		}
}
