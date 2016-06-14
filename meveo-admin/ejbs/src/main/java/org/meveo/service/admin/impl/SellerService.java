/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;

@Stateless
public class SellerService extends PersistenceService<Seller> {
	
	@Inject
	private InvoiceTypeService invoiceTypeService;

	public Seller findByCode(String code, Provider provider) {
		return findByCode(code, provider, null);
	}

	public Seller findByCode(String code, Provider provider,
			List<String> fetchFields) {
		QueryBuilder qb = new QueryBuilder(Seller.class, "s", fetchFields,
				provider);

		qb.addCriterion("code", "=", code, true);
		qb.addCriterionEntity("s.provider", provider);

		try {
			return (Seller) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Seller findByCode(EntityManager em, String code, Provider provider) {
		Query query = em
				.createQuery(
						"from " + Seller.class.getSimpleName()
								+ " where code=:code and provider=:provider")
				.setParameter("code", code).setParameter("provider", provider);
		if (query.getResultList().size() == 0) {
			return null;
		}

		return (Seller) query.getResultList().get(0);
	}

	public boolean hasChildren(EntityManager em, Seller seller,
			Provider provider) {
		QueryBuilder qb = new QueryBuilder(Seller.class, "s");
		qb.addCriterionEntity("provider", provider);
		qb.addCriterionEntity("seller", seller);

		try {
			return ((Long) qb.getCountQuery(em).getSingleResult()) > 0;
		} catch (NoResultException e) {
			return false;
		}
	}
	
	@Override
    public void create(Seller seller,User creator) throws BusinessException{
        log.info("start of create seller");
        super.create(seller, creator);
        InvoiceType commType = invoiceTypeService.getDefaultCommertial(creator);
        log.debug("InvoiceTypeCode for commercial bill :"+(commType==null?null:commType.getCode()));
        InvoiceType adjType = invoiceTypeService.getDefaultAdjustement(creator);
        log.debug("InvoiceTypeCode for adjustement bill :"+(adjType==null?null:adjType.getCode()));
    }
}
