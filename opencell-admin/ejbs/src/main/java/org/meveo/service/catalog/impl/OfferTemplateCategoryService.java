package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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