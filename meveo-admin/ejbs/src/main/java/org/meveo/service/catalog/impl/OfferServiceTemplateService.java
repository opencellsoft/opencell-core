package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferServiceTemplateService extends PersistenceService<OfferServiceTemplate> {

	public OfferServiceTemplate findByOfferAndServiceTemplate(OfferTemplate offerTemplate,
			ServiceTemplate serviceTemplate) {
		QueryBuilder qb = new QueryBuilder(OfferServiceTemplate.class, "o", null, offerTemplate.getProvider());
		qb.addCriterionEntity("offerTemplate", offerTemplate);
		qb.addCriterionEntity("serviceTemplate", serviceTemplate);

		try {
			return (OfferServiceTemplate) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
