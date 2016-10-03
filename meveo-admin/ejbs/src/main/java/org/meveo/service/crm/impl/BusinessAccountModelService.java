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
import org.meveo.model.crm.Provider;
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
	public List<BusinessAccountModel> listInstalled(Provider provider) {
		QueryBuilder queryBuilder = new QueryBuilder(BusinessAccountModel.class, "a", null, provider);
		queryBuilder.addBooleanCriterion("disabled", false);
		queryBuilder.addBooleanCriterion("installed", true);

		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<BusinessEntity> listParents(String searchTerm, Class<? extends BusinessEntity> parentClass, PaginationConfiguration paginationConfiguration, Provider provider) {
		QueryBuilder queryBuilder = new QueryBuilder(parentClass, "p", null, provider);
		queryBuilder.addCriterionEntity("p.provider", provider);
		if(!StringUtils.isBlank(searchTerm)){
			queryBuilder.like("p.description", searchTerm, QueryBuilder.QueryLikeStyleEnum.MATCH_ANYWHERE, true);
		}
		queryBuilder.addPaginationConfiguration(paginationConfiguration);

		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

}
