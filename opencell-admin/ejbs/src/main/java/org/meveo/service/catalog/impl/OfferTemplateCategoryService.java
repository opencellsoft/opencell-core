/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.catalog.impl;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ExistsRelatedEntityException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.base.BusinessService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferTemplateCategoryService extends BusinessService<OfferTemplateCategory> {

	@Override
	public void create(OfferTemplateCategory e) throws BusinessException {
		if (e.getOfferTemplateCategory() != null) {
			e.setOrderLevel(e.getOfferTemplateCategory().getOrderLevel() + 1);
		}

		super.create(e);
	}

	@SuppressWarnings("unchecked")
	public List<OfferTemplateCategory> listAllExceptId(Long id, boolean isParent) {
		QueryBuilder qb = new QueryBuilder(OfferTemplateCategory.class, "o", null);
		qb.addCriterion("id", "<>", id, true);
		if (isParent) {
			qb.addCriterion("level", "<>", 3, true);
		}

		try {
			return (List<OfferTemplateCategory>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<OfferTemplateCategory> listAllRootsExceptId(Long id) {
		QueryBuilder qb = new QueryBuilder(OfferTemplateCategory.class, "o", null);
		qb.addCriterion("level", "=", 1, true);
		if (id != null) {
			qb.addCriterion("id", "<>", id, true);
		}

		try {
			return (List<OfferTemplateCategory>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

    @SuppressWarnings("unchecked")
    public List<OfferTemplateCategory> findRoots() {
        Query query = getEntityManager()
                .createQuery(
                        "from " + OfferTemplateCategory.class.getSimpleName()
                                + " where offerTemplateCategory.id IS NULL");
        if (query.getResultList().size() == 0) {
            return null;
        }

        return query.getResultList();
    }

    @Override
    public void remove(OfferTemplateCategory entity) throws BusinessException {
     
        if (entity.isAssignedToProductOffering()){
            throw new ExistsRelatedEntityException();
        }
        super.remove(entity);
    }
}