package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class OfferProductTemplateService extends PersistenceService<OfferProductTemplate> {

	@SuppressWarnings("unchecked")
	public List<ProductTemplate> listByOfferTemplate(OfferTemplate offerTemplate) {
		// QueryBuilder qb = new QueryBuilder(OfferProductTemplate.class, "o",
		// null, offerTemplate.getProvider());
		QueryBuilder qb = new QueryBuilder("SELECT o.productTemplate FROM OfferProductTemplate o");
		qb.addCriterionEntity("o.offerTemplate", offerTemplate);

		try {
			return (List<ProductTemplate>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
