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

package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.admin.impl.GenericModuleService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessAccountModelService extends GenericModuleService<BusinessAccountModel> {

	@SuppressWarnings("unchecked")
	public List<BusinessAccountModel> findByScriptId(Long id) {
		QueryBuilder qb = new QueryBuilder(BusinessAccountModel.class, "b");
		qb.addCriterion("script.id", "=", id, true);

		try {
			return (List<BusinessAccountModel>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<BusinessAccountModel> listInstalled() {
		QueryBuilder queryBuilder = new QueryBuilder(BusinessAccountModel.class, "a", null);
		queryBuilder.addBooleanCriterion("disabled", false);
		queryBuilder.addBooleanCriterion("installed", true);

		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<BusinessEntity> listParents(String searchTerm, Class<? extends BusinessEntity> parentClass, PaginationConfiguration paginationConfiguration) {
		QueryBuilder queryBuilder = new QueryBuilder(parentClass, "p", null);
		if(!StringUtils.isBlank(searchTerm)){
			queryBuilder.like("p.description", searchTerm, QueryBuilder.QueryLikeStyleEnum.MATCH_ANYWHERE, true);
		}
		queryBuilder.addPaginationConfiguration(paginationConfiguration);

		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

}
