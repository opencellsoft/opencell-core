package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferTemplateCategoryService extends BusinessService<OfferTemplateCategory> {

	@Override
	public void create(OfferTemplateCategory e, User creator, Provider provider) {
		if (e.getOfferTemplateCategory() != null) {
			e.setLevel(e.getOfferTemplateCategory().getLevel() + 1);
		}

		super.create(e, creator, provider);
	}

	@SuppressWarnings("unchecked")
	public List<OfferTemplateCategory> listAllExceptId(Long id, boolean isParent) {
		QueryBuilder qb = new QueryBuilder(OfferTemplateCategory.class, "o", null, getCurrentProvider());
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
		QueryBuilder qb = new QueryBuilder(OfferTemplateCategory.class, "o", null, getCurrentProvider());
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

}
